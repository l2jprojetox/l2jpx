package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.PartyMatchWaitingList;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: (ch) this is just a trigger : no data
 * @author -Wooden-
 */
public final class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		PartyMatchWaitingList.getInstance().removePlayer(activeChar);
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:17 RequestExitPartyMatchingWaitingRoom";
	}
	
}
