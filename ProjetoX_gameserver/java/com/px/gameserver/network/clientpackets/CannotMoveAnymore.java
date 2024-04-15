package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.location.SpawnLocation;

public final class CannotMoveAnymore extends L2GameClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	
	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Creature player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.hasAI())
			player.getAI().notifyEvent(AiEventType.ARRIVED_BLOCKED, new SpawnLocation(_x, _y, _z, _heading));
	}
}