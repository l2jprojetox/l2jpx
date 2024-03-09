package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) S
 * @author -Wooden-
 */
public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private static String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Send PartyRoom invite request (with activeChar) name to the target
		final L2PcInstance target = L2World.getInstance().getPlayerByName(name);
		if (target != null)
		{
			if (!target.isProcessingRequest())
			{
				activeChar.onTransactionRequest(target);
				target.sendPacket(new ExAskJoinPartyRoom(activeChar.getName()));
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER).addString(target.getName()));
			}
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:14 RequestAskJoinPartyRoom";
	}
	
}
