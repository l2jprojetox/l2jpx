package net.l2jpx.gameserver.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.util.database.L2DatabaseFactory;

import javolution.util.FastMap;

/**
 * @author Layane
 * @author ReynalDev
 */
public class CrestCache
{
	private static final Logger LOGGER = Logger.getLogger(CrestCache.class);
	private static final String UPDATE_CLAN_CREST = "UPDATE clan_data SET crest_id=? WHERE clan_id=?";
	
	private static CrestCache instance;
	
	private final FastMRUCache<Integer, byte[]> cachePledge = new FastMRUCache<>();
	
	private final FastMRUCache<Integer, byte[]> cachePledgeLarge = new FastMRUCache<>();
	
	private final FastMRUCache<Integer, byte[]> cacheAlly = new FastMRUCache<>();
	
	private int loadedFiles;
	
	private long bytesBuffLen;
	
	public static CrestCache getInstance()
	{
		if (instance == null)
		{
			instance = new CrestCache();
		}
		
		return instance;
	}
	
	public CrestCache()
	{
		convertOldPedgeFiles();
		reload();
	}
	
	public void reload()
	{
		final FileFilter filter = new BmpFilter();
		
		final File dir = new File(Config.DATAPACK_ROOT, "data/crests/");
		
		final File[] files = dir.listFiles(filter);
		byte[] content;
		synchronized (this)
		{
			loadedFiles = 0;
			bytesBuffLen = 0;
			
			cachePledge.clear();
			cachePledgeLarge.clear();
			cacheAlly.clear();
		}
		
		final FastMap<Integer, byte[]> mapPledge = cachePledge.getContentMap();
		final FastMap<Integer, byte[]> mapPledgeLarge = cachePledgeLarge.getContentMap();
		final FastMap<Integer, byte[]> mapAlly = cacheAlly.getContentMap();
		
		for (File file : files)
		{
			synchronized (this)
			{
				try(RandomAccessFile f = new RandomAccessFile(file, "r");)
				{
					content = new byte[(int) f.length()];
					f.readFully(content);
					
					if (file.getName().startsWith("Crest_Large_"))
					{
						mapPledgeLarge.put(Integer.valueOf(file.getName().substring(12, file.getName().length() - 4)), content);
					}
					else if (file.getName().startsWith("Crest_"))
					{
						mapPledge.put(Integer.valueOf(file.getName().substring(6, file.getName().length() - 4)), content);
					}
					else if (file.getName().startsWith("AllyCrest_"))
					{
						mapAlly.put(Integer.valueOf(file.getName().substring(10, file.getName().length() - 4)), content);
					}
					
					loadedFiles++;
					bytesBuffLen += content.length;
				}
				catch (Exception e)
				{
					LOGGER.error("problem with crest bmp file", e);
				}
			}
		}
		
		LOGGER.info("Cache[Crest]: " + String.format("%.3f", getMemoryUsage()) + "MB on " + getLoadedFiles() + " files loaded. (Forget Time: " + cachePledge.getForgetTime() / 1000 + "s , Capacity: " + cachePledge.capacity() + ")");
	}
	
	public void convertOldPedgeFiles()
	{
		File dir = new File(Config.DATAPACK_ROOT, "data/crests/");
		
		File[] files = dir.listFiles(new OldPledgeFilter());
		
		if (files == null)
		{
			LOGGER.info("No old crest files found in \"data/crests/\"!!! May be you deleted them?");
			return;
		}
		
		for (final File file : files)
		{
			final int clanId = Integer.parseInt(file.getName().substring(7, file.getName().length() - 4));
			
			LOGGER.info("Found old crest file \"" + file.getName() + "\" for clanId " + clanId);
			
			final int newId = IdFactory.getInstance().getNextId();
			
			L2Clan clan = ClanTable.getInstance().getClan(clanId);
			
			if (clan != null)
			{
				removeOldPledgeCrest(clan.getCrestId());
				
				file.renameTo(new File(Config.DATAPACK_ROOT, "data/crests/Crest_" + newId + ".bmp"));
				LOGGER.info("Renamed Clan crest to new format: Crest_" + newId + ".bmp");
				
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement(UPDATE_CLAN_CREST))
				{
					statement.setInt(1, newId);
					statement.setInt(2, clan.getClanId());
					statement.executeUpdate();
				}
				catch (final SQLException e)
				{
					LOGGER.error("CrestCache.convertOldPedgeFiles : Could not update the crest id", e);
				}
				
				clan.setCrestId(newId);
				clan.setHasCrest(true);
			}
			else
			{
				LOGGER.info("Clan Id: " + clanId + " does not exist in table.. deleting.");
				file.delete();
			}
			
			clan = null;
		}
		
		files = null;
		dir = null;
	}
	
	public float getMemoryUsage()
	{
		return (float) bytesBuffLen / 1048576;
	}
	
	public int getLoadedFiles()
	{
		return loadedFiles;
	}
	
	public byte[] getPledgeCrest(final int id)
	{
		return cachePledge.get(id);
	}
	
	public byte[] getPledgeCrestLarge(final int id)
	{
		return cachePledgeLarge.get(id);
	}
	
	public byte[] getAllyCrest(final int id)
	{
		return cacheAlly.get(id);
	}
	
	public void removePledgeCrest(int id)
	{
		File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/Crest_" + id + ".bmp");
		cachePledge.remove(id);
		
		try
		{
			crestFile.delete();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while removing clan crest from disk", e);
		}
	}
	
	public void removePledgeCrestLarge(int id)
	{
		File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/Crest_Large_" + id + ".bmp");
		cachePledgeLarge.remove(id);
		
		try
		{
			crestFile.delete();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while removing clan crest large from disk", e);
		}
	}
	
	public void removeOldPledgeCrest(int id)
	{
		File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/Pledge_" + id + ".bmp");
		
		try
		{
			crestFile.delete();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while removing old clan crest from disk", e);
		}
	}
	
	public void removeAllyCrest(int id)
	{
		File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/AllyCrest_" + id + ".bmp");
		cacheAlly.remove(id);
		
		try
		{
			crestFile.delete();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while removing clan crest from disk", e);
		}
	}
	
	public boolean savePledgeCrest(int newId, byte[] data)
	{
		boolean output = false;
		final File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/Crest_" + newId + ".bmp");
		
		try(FileOutputStream out = new FileOutputStream(crestFile);)
		{
			out.write(data);
			cachePledge.getContentMap().put(newId, data);
			
			output = true;
		}
		catch (Exception e)
		{
			LOGGER.error("Error saving pledge crest" + crestFile, e);
		}
		
		return output;
	}
	
	public boolean savePledgeCrestLarge(final int newId, final byte[] data)
	{
		boolean output = false;
		final File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/Crest_Large_" + newId + ".bmp");
		
		try(FileOutputStream out = new FileOutputStream(crestFile);)
		{
			out.write(data);
			cachePledgeLarge.getContentMap().put(newId, data);
			
			output = true;
		}
		catch (Exception e)
		{
			LOGGER.error("Error saving pledge crest large" + crestFile, e);
		}
		
		return output;
	}
	
	public boolean saveAllyCrest(final int newId, final byte[] data)
	{
		boolean output = false;
		final File crestFile = new File(Config.DATAPACK_ROOT, "data/crests/AllyCrest_" + newId + ".bmp");
		
		try(FileOutputStream out = new FileOutputStream(crestFile);)
		{
			out.write(data);
			cacheAlly.getContentMap().put(newId, data);
			
			output = true;
		}
		catch (Exception e)
		{
			LOGGER.error("Error saving pledge ally crest" + crestFile, e);
		}
		
		return output;
	}
	
	class BmpFilter implements FileFilter
	{
		@Override
		public boolean accept(final File file)
		{
			return file.getName().endsWith(".bmp");
		}
	}
	
	class OldPledgeFilter implements FileFilter
	{
		@Override
		public boolean accept(final File file)
		{
			return file.getName().startsWith("Pledge_");
		}
	}
}
