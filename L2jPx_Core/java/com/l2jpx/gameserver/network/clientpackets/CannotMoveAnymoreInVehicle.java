package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.ActionFailed;
import com.l2jpx.gameserver.network.serverpackets.StopMoveInVehicle;

public final class CannotMoveAnymoreInVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isInBoat() && player.getBoat().getObjectId() == _boatId)
		{
			player.getBoatPosition().set(_x, _y, _z, _heading);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.broadcastPacket(new StopMoveInVehicle(player, _boatId));
		}
	}
}