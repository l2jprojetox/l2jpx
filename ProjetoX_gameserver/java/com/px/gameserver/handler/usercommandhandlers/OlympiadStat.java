package com.px.gameserver.handler.usercommandhandlers;

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
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (!activeChar.isNoble())
		{
			activeChar.sendPacket(SystemMessageId.NOBLESSE_ONLY);
			return false;
		}
		
		int nobleObjId = activeChar.getObjectId();
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
		sm.addNumber(Olympiad.getInstance().getCompetitionDone(nobleObjId));
		sm.addNumber(Olympiad.getInstance().getCompetitionWon(nobleObjId));
		sm.addNumber(Olympiad.getInstance().getCompetitionLost(nobleObjId));
		sm.addNumber(Olympiad.getInstance().getNoblePoints(nobleObjId));
		activeChar.sendPacket(sm);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}