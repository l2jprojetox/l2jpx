package com.px.gameserver.handler.usercommandhandlers;

import com.px.commons.data.StatSet;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.olympiad.Olympiad;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class OlympiadStat implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		109
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (!player.isNoble())
		{
			player.sendPacket(SystemMessageId.NOBLESSE_ONLY);
			return;
		}
		
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
		
		final StatSet set = Olympiad.getInstance().getNobleStats(player.getObjectId());
		if (set == null)
		{
			sm.addNumber(0);
			sm.addNumber(0);
			sm.addNumber(0);
			sm.addNumber(0);
		}
		else
		{
			sm.addNumber(set.getInteger(Olympiad.COMP_DONE));
			sm.addNumber(set.getInteger(Olympiad.COMP_WON));
			sm.addNumber(set.getInteger(Olympiad.COMP_LOST));
			sm.addNumber(set.getInteger(Olympiad.POINTS));
		}
		player.sendPacket(sm);
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}