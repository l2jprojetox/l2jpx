package com.l2jpx.gameserver.handler.usercommandhandlers;

import com.l2jpx.commons.data.StatSet;

import com.l2jpx.gameserver.handler.IUserCommandHandler;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.olympiad.Olympiad;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;

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