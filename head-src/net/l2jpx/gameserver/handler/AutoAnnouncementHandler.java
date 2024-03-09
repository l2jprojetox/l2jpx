package net.l2jpx.gameserver.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.templates.AutoAnnouncement;
import net.l2jpx.util.database.L2DatabaseFactory;

import javolution.text.TextBuilder;

/**
 * Automatically send announcment at a set time interval.
 * @author chief
 * @author ReynalDev
 */
public class AutoAnnouncementHandler
{
	private static final Logger LOGGER = Logger.getLogger(AutoAnnouncementHandler.class);
	
	private static final String SELECT_AUTO_ANNOUNCEMENT = "SELECT id, announcement, delay FROM auto_announcements ORDER BY id";
	private static final String INSERT_AUTO_ANNOUNCEMENT = "INSERT INTO auto_announcements (announcement,delay) VALUES (?,?)";
	private static final String DELETE_AUTO_ANNOUNCEMENT = "DELETE FROM auto_announcements WHERE id=?";
	
	private static AutoAnnouncementHandler instance;
	private Map<Integer, AutoAnnouncement> announcements = new ConcurrentHashMap<>();
	
	protected AutoAnnouncementHandler()
	{
		loadAnnouncements();
	}
	
	private void loadAnnouncements()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_AUTO_ANNOUNCEMENT);
			ResultSet rs = statement.executeQuery())
		{
			while (rs.next())
			{
				announcements.put(rs.getInt("id"), new AutoAnnouncement(rs.getInt("id"), rs.getString("announcement"), rs.getLong("delay")));
			}
			
			if(announcements.size() > 0)
			{
				LOGGER.info("AutoAnnouncementHandler: Loaded " + announcements.size() + " Auto Announcements.");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("AutoAnnouncementHandler.restoreAnnouncementData: Could not load data from auto_announcements table", e);
		}
	}
	
	public void registerAnnouncement(String text, long delay)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_AUTO_ANNOUNCEMENT, Statement.RETURN_GENERATED_KEYS))
		{
			statement.setString(1, text);
			statement.setLong(2, delay);
			
			if(statement.executeUpdate() > 0)
			{
				try(ResultSet rs = statement.getGeneratedKeys())
				{
					if(rs.next())
					{
						int nextId = rs.getInt("id");
						announcements.put(nextId, new AutoAnnouncement(nextId, text, delay));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("System: Could Not Insert Auto Announcment into DataBase: Reason: " + "Duplicate Id");
		}
		
	}
	
	public void removeAnnouncement(int id)
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_AUTO_ANNOUNCEMENT))
		{
			statement.setInt(1, id);
			statement.executeUpdate();
			
			announcements.get(id).getTask().cancel(true);
			announcements.remove(id);
		}
		catch (Exception e)
		{
			LOGGER.error("Could not Delete Auto Announcement in Database, Reason:", e);
		}
	}
	
	public void listAutoAnnouncements(L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		
		TextBuilder replyMSG = new TextBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40></td>");
		replyMSG.append("<button value=\"Main\" action=\"bypass -h admin_admin\" width=50 height=15 back=\"L2UI_ct1.button_df\" " + "fore=\"L2UI_ct1.button_df\"><br>");
		
		replyMSG.append("<td width=180><center>Auto Announcement Menu</center></td>");
		replyMSG.append("<td width=40></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br>");
		replyMSG.append("<center>Add new auto announcement:</center>");
		replyMSG.append("<center><multiedit var=\"new_autoannouncement\" width=240 height=30></center><br>");
		replyMSG.append("<center>Delay in seconds, minimum 30 seconds</center><br1>");
		replyMSG.append("<center>Delay: <edit var=\"delay\" width=70></center>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"Add\" action=\"bypass -h admin_add_autoannouncement $delay $new_autoannouncement\" width=60 " + "height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("</td></tr></table></center>");
		replyMSG.append("<br>");
		
		for (AutoAnnouncement announcement : announcements.values())
		{
			replyMSG.append("<table width=260><tr><td width=220>[" + announcement.getDelay() / 1000 + "s] " + announcement.getText() + "</td><td width=40>");
			replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_autoannouncement " + announcement.getId() + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		}
		
		replyMSG.append("</body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public static AutoAnnouncementHandler getInstance()
	{
		if (instance == null)
		{
			instance = new AutoAnnouncementHandler();
		}
		
		return instance;
	}
}
