package net.l2jpx.gameserver.model.entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.clientpackets.Say2;
import net.l2jpx.gameserver.network.serverpackets.CreatureSay;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

import javolution.text.TextBuilder;

/**
 * @author ProGramMoS
 */
public class Announcements
{
	private static Logger LOGGER = Logger.getLogger(Announcements.class);
	
	private static Announcements instance;
	private final List<String> announcements = new ArrayList<>();
	
	public Announcements()
	{
		loadAnnouncements();
	}
	
	public static Announcements getInstance()
	{
		if (instance == null)
		{
			instance = new Announcements();
		}
		
		return instance;
	}
	
	public void loadAnnouncements()
	{
		announcements.clear();
		File file = new File(Config.DATAPACK_ROOT, "data/announcements.txt");
		
		if (file.exists())
		{
			readFromDisk(file);
		}
		else
		{
			LOGGER.warn("data/announcements.txt doesn't exist");
		}
		
	}
	
	public void showAnnouncements(final L2PcInstance activeChar)
	{
		for (final String announcement : announcements)
		{
			CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, activeChar.getName(), announcement.replace("%name%", activeChar.getName()));
			activeChar.sendPacket(cs);
		}
	}
	
	public void listAnnouncements(final L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/announce.htm");
		TextBuilder replyMSG = new TextBuilder("<br>");
		
		for (int i = 0; i < announcements.size(); i++)
		{
			replyMSG.append("<table width=260><tr><td width=220>" + announcements.get(i) + "</td><td width=40>");
			replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		}
		
		adminReply.replace("%announces%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public void addAnnouncement(final String text)
	{
		announcements.add(text);
		saveToDisk();
	}
	
	public void delAnnouncement(final int line)
	{
		announcements.remove(line);
		saveToDisk();
	}
	
	private void readFromDisk(final File file)
	{
		// 3 cause special characters that you can not see in editor such "\n"
		if(file.length() <= 3)
			return;
		
		try(FileReader reader = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(reader))
		{
			int i = 0;
			
			String line = null;
			
			while ((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				
				if (st.hasMoreTokens())
				{
					String announcement = st.nextToken();
					
					announcements.add(announcement);
					
					i++;
				}
			}
			LOGGER.info("Announcements: Loaded " + i + " Announcements.");
		}
		catch (Exception e)
		{
			LOGGER.error("Error reading announcements", e);
		}
	}
	
	private void saveToDisk()
	{
		final File file = new File("data/announcements.txt");
		FileWriter save = null;
		
		try
		{
			save = new FileWriter(file);
			for (final String announcement : announcements)
			{
				save.write(announcement);
				save.write("\r\n");
			}
			save.flush();
		}
		catch (final IOException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}
			
			LOGGER.warn("saving the announcements file has failed: " + e);
		}
		finally
		{
			
			if (save != null)
			{
				try
				{
					save.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void announceToAll(final String text)
	{
		CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, "", text);
		
		for (final L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player != null)
			{
				if (player.isOnline())
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	
	// Colored Announcements 8D
	// Used for events
	public void gameAnnounceToAll(final String text)
	{
		CreatureSay cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, null, text);
		
		for (final L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player != null)
			{
				if (player.isOnline())
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	
	public void announceToAll(final SystemMessage sm)
	{
		for (final L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player != null)
			{
				if (player.isOnline())
				{
					player.sendPacket(sm);
				}
			}
		}
	}
	
	// Method fo handling announcements from admin
	public void handleAnnounce(final String command, final int lengthToTrim)
	{
		try
		{
			// Announce string to everyone on server
			String text = command.substring(lengthToTrim);
			Announcements.getInstance().announceToAll(text);
			text = null;
		}
		
		// No body cares!
		catch (final StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}
		}
	}
}
