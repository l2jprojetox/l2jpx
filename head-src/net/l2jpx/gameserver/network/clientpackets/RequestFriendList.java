package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendList extends L2GameClientPacket
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
		
		SystemMessage sm;
		
		// ======<Friend List>======
		activeChar.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_HEAD));
		
		L2PcInstance friend = null;
		for (final String friendName : activeChar.getFriendList())
		{
			friend = L2World.getInstance().getPlayerByName(friendName);
			
			if (friend == null || !friend.isOnline())
			{
				// (Currently: Offline)
				sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE);
				sm.addString(friendName);
			}
			else
			{
				// (Currently: Online)
				sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_ONLINE);
				sm.addString(friendName);
			}
			
			activeChar.sendPacket(sm);
		}
		
		// =========================
		activeChar.sendPacket(new SystemMessage(SystemMessageId.FOOTER));
	}
	
	@Override
	public String getType()
	{
		return "[C] 60 RequestFriendList";
	}
}
