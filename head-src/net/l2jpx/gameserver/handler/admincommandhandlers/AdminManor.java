package net.l2jpx.gameserver.handler.admincommandhandlers;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import net.l2jpx.Config;
import net.l2jpx.gameserver.handler.IAdminCommandHandler;
import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.managers.CastleManorManager;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.siege.Castle;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Admin comand handler for Manor System This class handles following admin commands: - manor_info = shows info about current manor state - manor_approve = approves settings for the next manor period - manor_setnext = changes manor settings to the next day's - manor_reset castle = resets all manor
 * data for specified castle (or all) - manor_setmaintenance = sets manor system under maintenance mode - manor_save = saves all manor data into database - manor_disable = disables manor system
 * @author l3x
 */
public class AdminManor implements IAdminCommandHandler
{
	private static final String[] adminCommands =
	{
		"admin_manor",
		"admin_manor_reset",
		"admin_manor_save",
		"admin_manor_disable"
	};
	
	@Override
	public boolean useAdminCommand(String command, final L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		command = st.nextToken();
		
		switch (command)
		{
			case "admin_manor":
				showMainPage(activeChar);
				break;
			case "admin_manor_reset":
				int castleId = 0;
				
				try
				{
					castleId = Integer.parseInt(st.nextToken());
				}
				catch (final Exception e)
				{
					if (Config.ENABLE_ALL_EXCEPTIONS)
					{
						e.printStackTrace();
					}
				}
				
				if (castleId > 0)
				{
					final Castle castle = CastleManager.getInstance().getCastleById(castleId);
					castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
					castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
					castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
					castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
					
					if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
					{
						castle.saveCropData();
						castle.saveSeedData();
					}
					
					activeChar.sendMessage("Manor data for " + castle.getName() + " was nulled");
				}
				else
				{
					for (final Castle castle : CastleManager.getInstance().getCastles())
					{
						castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
						castle.setCropProcure(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
						castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_CURRENT);
						castle.setSeedProduction(new ArrayList<>(), CastleManorManager.PERIOD_NEXT);
						
						if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
						{
							castle.saveCropData();
							castle.saveSeedData();
						}
					}
					
					activeChar.sendMessage("Manor data was nulled");
				}
				
				showMainPage(activeChar);
				break;
			case "admin_manor_save":
				CastleManorManager.getInstance().save();
				activeChar.sendMessage("Manor System: all data saved");
				showMainPage(activeChar);
				break;
			case "admin_manor_disable":
				final boolean mode = CastleManorManager.getInstance().isDisabled();
				
				CastleManorManager.getInstance().setDisabled(!mode);
				
				if (mode)
				{
					activeChar.sendMessage("Manor System: enabled");
				}
				else
				{
					activeChar.sendMessage("Manor System: disabled");
				}
				
				showMainPage(activeChar);
				break;
		}
		
		st = null;
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return adminCommands;
	}
	
	/*
	 * private String formatTime(long millis) { String s = ""; int secs = (int) millis/1000; int mins = secs/60; secs -= mins*60; int hours = mins/60; mins -= hours*60; if (hours>0) s += hours + ":"; s += mins + ":"; s += secs; return s; }
	 */
	
	private void showMainPage(final L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><body>");
		
		replyMSG.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
		replyMSG.append("<table width=\"100%\"><tr><td>");
		replyMSG.append("Disabled: " + (CastleManorManager.getInstance().isDisabled() ? "yes" : "no") + "</td><td>");
		replyMSG.append("Under Maintenance: " + (CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no") + "</td></tr><tr><td>");
		replyMSG.append("<tr><td>Approved: " + (CastleManorManager.APPROVE == 1 ? "yes" : "no") + "</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"" + (CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr><tr><td>");
		replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		replyMSG.append("<button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("<br><center>Castle Information:<table width=\"100%\">");
		replyMSG.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");
		
		for (final Castle c : CastleManager.getInstance().getCastles())
		{
			replyMSG.append("<tr><td>" + c.getName() + "</td><td>" + c.getManorCost(CastleManorManager.PERIOD_CURRENT) + "a</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_NEXT) + "a</td></tr>");
		}
		
		replyMSG.append("</table><br>");
		replyMSG.append("</body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
		
		adminReply = null;
		replyMSG = null;
	}
}
