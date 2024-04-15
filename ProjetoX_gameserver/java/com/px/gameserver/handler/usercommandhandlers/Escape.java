package com.px.gameserver.handler.usercommandhandlers;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.PlaySound;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (activeChar.isCastingNow() || activeChar.isSitting() || activeChar.isMovementDisabled() || activeChar.isOutOfControl() || activeChar.isInOlympiadMode() || activeChar.isInObserverMode() || activeChar.isFestivalParticipant() || activeChar.isInJail() || activeChar.isInsideZone(ZoneId.BOSS))
		{
			activeChar.sendPacket(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
			return false;
		}
		
		activeChar.stopMove(null);
		
		// Official timer 5 minutes, for GM 1 second
		if (activeChar.isGM())
			activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		else
		{
			activeChar.sendPacket(new PlaySound("systemmsg_e.809"));
			activeChar.sendPacket(SystemMessageId.STUCK_TRANSPORT_IN_FIVE_MINUTES);
			
			activeChar.doCast(SkillTable.getInstance().getInfo(2099, 1));
		}
		
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}