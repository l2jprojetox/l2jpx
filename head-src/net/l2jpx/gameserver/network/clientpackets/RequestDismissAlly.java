package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestDismissAlly extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger packet
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!activeChar.isClanLeader())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_ALLIANCE_LEADERS));
			return;
		}
		
		activeChar.getClan().dissolveAlly(activeChar);
	}
	
	@Override
	public String getType()
	{
		return "[C] 86 RequestDismissAlly";
	}
}
