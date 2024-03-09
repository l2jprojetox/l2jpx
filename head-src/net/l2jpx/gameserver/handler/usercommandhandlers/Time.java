package net.l2jpx.gameserver.handler.usercommandhandlers;

import net.l2jpx.gameserver.controllers.GameTimeController;
import net.l2jpx.gameserver.handler.IUserCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class Time implements IUserCommandHandler
{
	// time
	private static final int[] COMMAND_IDS =
	{
		77
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (COMMAND_IDS[0] != id)
		{
			return false;
		}
		
		int time = GameTimeController.getInstance().getGameTime();
		
		String hour = Integer.toString(time / 60 % 24);
		String minute;
		
		minute = (time % 60 < 10 ? "0" : "") + time % 60;
		
		SystemMessage sm = new SystemMessage(GameTimeController.getInstance().isNowNight() ? SystemMessageId.THE_CURRENT_TIME_IS_S1_S2_IN_THE_NIGHT : SystemMessageId.THE_CURRENT_TIME_IS_S1_S2_IN_THE_DAY);
		sm.addString(hour);
		sm.addString(minute);
		activeChar.sendPacket(sm);
		
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
