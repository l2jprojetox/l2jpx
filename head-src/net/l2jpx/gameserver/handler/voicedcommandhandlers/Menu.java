package net.l2jpx.gameserver.handler.voicedcommandhandlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.l2jpx.gameserver.handler.IVoicedCommandHandler;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class Menu implements IVoicedCommandHandler
{
	private final String[] _voicedCommands =
	{
		"menu",
		"buffprot",
		"tradeprot",
		"ssprot",
		"xpnot",
		"pmref",
		"partyin"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("menu"))
		{
			showMainHtml(activeChar);
		}
		else
		{
			if (command.startsWith("buffprot"))
			{
				if (activeChar.isBuffProtected())
				{
					activeChar.setIsBuffProtected(false);
					activeChar.sendMessage("Buff protection is disabled.");
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsBuffProtected(true);
					activeChar.sendMessage("Buff protection is enabled.");
					showMainHtml(activeChar);
				}
			}
			else if (command.startsWith("tradeprot"))
			{
				if (activeChar.isInTradeProt())
				{
					activeChar.setIsInTradeProt(false);
					activeChar.sendMessage("Trade acceptance mode is enabled.");
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsInTradeProt(true);
					activeChar.sendMessage("Trade refusal mode is enabled.");
					showMainHtml(activeChar);
				}
			}
			else if (command.startsWith("ssprot"))
			{
				if (activeChar.isSSDisabled())
				{
					activeChar.setIsSSDisabled(false);
					activeChar.sendMessage("Soulshots effects are enabled.");
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsSSDisabled(true);
					activeChar.sendMessage("Soulshots effects are disabled.");
					showMainHtml(activeChar);
				}
			}
			else if (command.startsWith("xpnot"))
			{
				if (activeChar.cantGainXP())
				{
					activeChar.cantGainXP(false);
					activeChar.sendMessage("Enable Xp");
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.cantGainXP(true);
					activeChar.sendMessage("Disable Xp");
					showMainHtml(activeChar);
				}
			}
			else if (command.startsWith("pmref"))
			{
				if (activeChar.getMessageRefusal())
				{
					activeChar.setMessageRefusal(false);
					activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_ACCEPTANCE_MODE));
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.setMessageRefusal(true);
					activeChar.sendPacket(new SystemMessage(SystemMessageId.MESSAGE_REFUSAL_MODE));
					showMainHtml(activeChar);
				}
			}
			else if (command.startsWith("partyin"))
			{
				if (activeChar.isPartyInvProt())
				{
					activeChar.setIsPartyInvProt(false);
					activeChar.sendMessage("Party acceptance mode is enabled.");
					showMainHtml(activeChar);
				}
				else
				{
					activeChar.setIsPartyInvProt(true);
					activeChar.sendMessage("Party refusal mode is enabled.");
					showMainHtml(activeChar);
				}
			}
			showMainHtml(activeChar);
		}
		return true;
	}
	
	public static void showMainHtml(L2PcInstance activeChar)
	{
		String filePath = "data/html/mods/menu/menu.htm";
		String content = loadHtmlContent(filePath, activeChar);
		if (content != null)
		{
			NpcHtmlMessage nhm = new NpcHtmlMessage(0); // Zero pode ser substituído pelo ID do NPC, se necessário.
			nhm.setHtml(content);
			activeChar.sendPacket(nhm);
		}
		else
		{
			activeChar.sendMessage("Não foi possível carregar o menu.");
		}
	}
	
	private static String loadHtmlContent(String filePath, L2PcInstance activeChar)
	{
		try
		{
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("%players_online%", String.valueOf(L2World.getInstance().getAllPlayers().size()));
			content = content.replace("%player_name%", activeChar.getName());
			return content;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
