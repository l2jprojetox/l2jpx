package net.l2jpx.gameserver.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.util.Util;

/**
 * @author Layane
 * @author ReynalDev
 */
public class HtmCache
{
	private static final Logger LOGGER = Logger.getLogger(HtmCache.class);
	private static HtmCache instance;
	
	private final HashMap<Integer, String> cache;
	
	private int loadedFiles;
	private long bytesBuffLen;
	
	public static HtmCache getInstance()
	{
		if (instance == null)
		{
			instance = new HtmCache();
		}
		
		return instance;
	}
	
	public HtmCache()
	{
		cache = new HashMap<>();
		reload();
	}
	
	public void reload()
	{
		reload(Config.DATAPACK_ROOT);
	}
	
	public void reload(final File f)
	{
		if (!Config.LAZY_CACHE)
		{
			LOGGER.info("Html cache start...");
			parseDir(f);
			LOGGER.info("Cache[HTML]: " + String.format("%.3f", getMemoryUsage()) + " megabytes on " + getLoadedFiles() + " files loaded");
		}
		else
		{
			cache.clear();
			loadedFiles = 0;
			bytesBuffLen = 0;
			LOGGER.info("Cache[HTML]: Running lazy cache");
		}
	}
	
	public void reloadPath(final File f)
	{
		parseDir(f);
		LOGGER.info("Cache[HTML]: Reloaded specified path.");
	}
	
	public double getMemoryUsage()
	{
		return (float) bytesBuffLen / 1048576;
	}
	
	public int getLoadedFiles()
	{
		return loadedFiles;
	}
	
	class HtmFilter implements FileFilter
	{
		@Override
		public boolean accept(final File file)
		{
			if (!file.isDirectory())
			{
				return file.getName().endsWith(".htm") || file.getName().endsWith(".html");
			}
			return true;
		}
	}
	
	private void parseDir(final File dir)
	{
		FileFilter filter = new HtmFilter();
		File[] files = dir.listFiles(filter);
		
		for (final File file : files)
		{
			if (!file.isDirectory())
			{
				loadFile(file);
			}
			else
			{
				parseDir(file);
			}
		}
		
		files = null;
		filter = null;
	}
	
	public String loadFile(final File file)
	{
		HtmFilter filter = new HtmFilter();
		
		String content = null;
		
		if (file.exists() && filter.accept(file) && !file.isDirectory())
		{
			try(FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);)
			{
				
				
				final int bytes = bis.available();
				final byte[] raw = new byte[bytes];
				
				bis.read(raw);
				
				content = new String(raw, "UTF-8");
				
				// Minify html as posible
				content = content.replace("\r", "");
				content = content.replace("\n", "");
				content = content.replace("\t", "");
				content = content.replaceAll("\\s{2,}", " "); // two blank space of more
				content = content.replace("> <", "><");
				
				final String relpath = Util.getRelativePath(Config.DATAPACK_ROOT, file);
				final int hashcode = relpath.hashCode();
				
				final String oldContent = cache.get(hashcode);
				
				if (oldContent == null)
				{
					bytesBuffLen += bytes;
					loadedFiles++;
				}
				else
				{
					bytesBuffLen = bytesBuffLen - oldContent.length() + bytes;
				}
				
				cache.put(hashcode, content);
				
			}
			catch (Exception e)
			{
				LOGGER.error("Error loading HTM file", e);
			}
			
		}
		
		return content;
	}
	
	public String getHtmForce(final String path)
	{
		String content = getHtm(path);
		
		if (content == null)
		{
			content = "<html><body>My text is missing:<br>" + path + "</body></html>";
			LOGGER.warn("Cache[HTML]: Missing HTML page: " + path);
		}
		
		return content;
	}
	
	public String getHtm(final String path)
	{
		String content = cache.get(path.hashCode());
		
		if (Config.LAZY_CACHE && content == null)
		{
			content = loadFile(new File(Config.DATAPACK_ROOT, path));
		}
		
		return content;
	}
	
	public boolean contains(final String path)
	{
		return cache.containsKey(path.hashCode());
	}
	
	/**
	 * Check if an HTM exists and can be loaded
	 * @param  path The path to the HTM
	 * @return
	 */
	public boolean isLoadable(final String path)
	{
		File file = new File(path);
		HtmFilter filter = new HtmFilter();
		
		if (file.exists() && filter.accept(file) && !file.isDirectory())
		{
			return true;
		}
		
		filter = null;
		file = null;
		
		return false;
	}
}
