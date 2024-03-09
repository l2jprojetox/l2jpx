package net.l2jpx.gameserver.handler.voicedcommandhandlers;

import net.l2jpx.Config;
import net.l2jpx.gameserver.handler.IVoicedCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;

public class StatsCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"stat",
	};
	
	private enum CommandEnum
	{
		stat,
	}
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		final CommandEnum comm = CommandEnum.valueOf(command);
		
		if (comm == null)
		{
			return false;
		}
		
		switch (comm)
		{
			case stat:
			{
				
				if (!Config.ALLOW_DETAILED_STATS_VIEW)
				{
					return false;
				}
				
				if (activeChar.getTarget() == null)
				{
					activeChar.sendMessage("You have no one targeted.");
					return false;
				}
				if (activeChar.getTarget() == activeChar)
				{
					activeChar.sendMessage("You cannot request your stats.");
					return false;
				}
				
				if (!(activeChar.getTarget() instanceof L2PcInstance))
				{
					activeChar.sendMessage("You can only get the info of a player.");
					return false;
				}
				
				NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
				L2PcInstance targetp = (L2PcInstance) activeChar.getTarget();
				
				TextBuilder replyMSG = new TextBuilder("<html><body><center>");
				replyMSG.append("<br><br><font color=\"00FF00\">=========>>" + targetp.getName() + "<<=========</font><br>");
				replyMSG.append("<font color=\"FF0000\">Level: " + targetp.getLevel() + "</font><br>");
				
				if (targetp.getClan() != null)
				{
					replyMSG.append("<font color=\"FF0000\">Clan: " + targetp.getClan().getName() + "</font><br>");
					replyMSG.append("<font color=\"FF0000\">Alliance: " + targetp.getClan().getAllyName() + "</font><br>");
				}
				else
				{
					replyMSG.append("<font color=\"FF0000\">Alliance: None</font><br>");
					replyMSG.append("<font color=\"FF0000\">Clan: None</font><br>");
				}
				
				replyMSG.append("<font color=\"FF0000\">Adena: " + targetp.getAdena() + "</font><br>");
				
				if (targetp.getInventory().getItemByItemId(6393) == null)
				{
					replyMSG.append("<font color=\"FF0000\">Medals : 0</font><br>");
				}
				else
				{
					replyMSG.append("<font color=\"FF0000\">Medals : " + targetp.getInventory().getItemByItemId(6393).getCount() + "</font><br>");
				}
				
				if (targetp.getInventory().getItemByItemId(3470) == null)
				{
					replyMSG.append("<font color=\"FF0000\">Gold Bars : 0</font><br>");
				}
				else
				{
					replyMSG.append("<font color=\"FF0000\">Gold Bars : " + targetp.getInventory().getItemByItemId(3470).getCount() + "</font><br>");
				}
				
				replyMSG.append("<font color=\"FF0000\">PvP Kills: " + targetp.getPvpKills() + "</font><br>");
				replyMSG.append("<font color=\"FF0000\">PvP Flags: " + targetp.getPvpFlag() + "</font><br>");
				replyMSG.append("<font color=\"FF0000\">PK Kills: " + targetp.getPkKills() + "</font><br>");
				replyMSG.append("<font color=\"FF0000\">HP, CP, MP: " + targetp.getMaxHp() + ", " + targetp.getMaxCp() + ", " + targetp.getMaxMp() + "</font><br>");
				
				if (targetp.getActiveWeaponInstance() == null)
				{
					replyMSG.append("<font color=\"FF0000\">No Weapon!</font><br>");
				}
				else
				{
					replyMSG.append("<font color=\"FF0000\">Wep Enchant: " + targetp.getActiveWeaponInstance().getEnchantLevel() + "</font><br>");
				}
				
				replyMSG.append("<font color=\"00FF00\">=========>>" + targetp.getName() + "<<=========" + "</font><br>");
				replyMSG.append("</center></body></html>");
				
				adminReply.setHtml(replyMSG.toString());
				activeChar.sendPacket(adminReply);
				
				adminReply = null;
				targetp = null;
				replyMSG = null;
				
				return true;
			}
			default:
			{
				return false;
			}
		}
		
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}
