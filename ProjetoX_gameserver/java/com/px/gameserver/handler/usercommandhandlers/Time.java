package com.px.gameserver.handler.usercommandhandlers;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.taskmanager.GameTimeTaskManager;

public class Time implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		77
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		final int hour = GameTimeTaskManager.getInstance().getGameHour();
		final int minute = GameTimeTaskManager.getInstance().getGameMinute();
		
		final String min = ((minute < 10) ? "0" : "") + minute;
		
		activeChar.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.TIME_S1_S2_IN_THE_NIGHT : SystemMessageId.TIME_S1_S2_IN_THE_DAY).addNumber(hour).addString(min));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}