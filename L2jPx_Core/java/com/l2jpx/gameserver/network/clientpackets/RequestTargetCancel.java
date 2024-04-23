package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.enums.AiEventType;
import com.l2jpx.gameserver.model.actor.Player;

public final class RequestTargetCancel extends L2GameClientPacket
{
	private int _unselect;
	
	@Override
	protected void readImpl()
	{
		_unselect = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_unselect == 0)
		{
			if (player.getCast().isCastingNow())
			{
				if (player.getCast().canAbortCast())
					player.getAI().notifyEvent(AiEventType.CANCEL, null, null);
			}
			else
				player.setTarget(null);
		}
		else
			player.setTarget(null);
	}
}