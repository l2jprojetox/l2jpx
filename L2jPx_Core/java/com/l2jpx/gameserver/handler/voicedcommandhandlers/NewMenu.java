package com.l2jpx.gameserver.handler.voicedcommandhandlers;

import com.l2jpx.gameserver.handler.IVoicedCommandHandler;
import com.l2jpx.gameserver.model.World;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewMenu implements IVoicedCommandHandler
{
    private static final String[] _voicedCommands =
            {
                    "menu",
                    "setPartyRefuse",
                    "setTradeRefuse",
                    "setbuffsRefuse",
                    "setMessageRefuse",
            };

    private static final String ACTIVED = "<font color=00FF00>ON</font>";
    private static final String DESATIVED = "<font color=FF0000>OFF</font>";

    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target)
    {
        if (command.equals("menu"))
            showMenuHtml(activeChar);

        else if (command.equals("setPartyRefuse"))
        {
            if (activeChar.isPartyInRefuse())
                activeChar.setIsPartyInRefuse(false);
            else
                activeChar.setIsPartyInRefuse(true);
            showMenuHtml(activeChar);
        }
        else if (command.equals("setbuffsRefuse"))
        {
            if (activeChar.isBuffProtected())
                activeChar.setIsBuffProtected(false);
            else
                activeChar.setIsBuffProtected(true);
            showMenuHtml(activeChar);
        }
        return true;
    }

    private static void showMenuHtml(Player activeChar)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/mods/menu/menu.htm");
        html.replace("%online%", World.getInstance().getPlayers().size());
        html.replace("%partyRefusal%", activeChar.isPartyInRefuse() ? ACTIVED : DESATIVED);
        html.replace("%buffsRefusal%", activeChar.isBuffProtected() ? ACTIVED : DESATIVED);

        html.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));

        activeChar.sendPacket(html);
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}

