package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.RelationManager;
import com.px.gameserver.data.sql.PlayerInfoTable;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// ======<Friend List>======
		player.sendPacket(SystemMessageId.FRIEND_LIST_HEADER);
		
		for (int id : RelationManager.getInstance().getFriendList(player.getObjectId()))
		{
			final String friendName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (friendName == null)
				continue;
			
			final Player friend = World.getInstance().getPlayer(id);
			
			player.sendPacket(SystemMessage.getSystemMessage((friend == null || !friend.isOnline()) ? SystemMessageId.S1_OFFLINE : SystemMessageId.S1_ONLINE).addString(friendName));
		}
		
		// =========================
		player.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
	}
}