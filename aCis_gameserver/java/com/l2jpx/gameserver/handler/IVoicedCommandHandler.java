package com.l2jpx.gameserver.handler;

import com.l2jpx.gameserver.model.actor.Player;

public interface IVoicedCommandHandler
{
    public boolean useVoicedCommand(String command, Player activeChar, String params);

    public String[] getVoicedCommandList();
}
