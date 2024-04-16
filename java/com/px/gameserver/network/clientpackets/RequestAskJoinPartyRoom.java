package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private String _targetName;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// Send invite request with player name to the target.
		final Player target = World.getInstance().getPlayer(_targetName);
		if (target != null)
		{
			if (!target.isProcessingRequest())
			{
				player.onTransactionRequest(target);
				target.sendPacket(new ExAskJoinPartyRoom(player.getName()));
			}
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
		}
		else
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
	}
}