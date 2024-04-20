package com.l2jpx.gameserver.network.serverpackets;

import com.l2jpx.gameserver.model.actor.Boat;
import com.l2jpx.gameserver.model.location.Location;

public class VehicleDeparture extends L2GameServerPacket
{
	private final int _objectId;
	private final Location _loc;
	private final int _moveSpeed;
	private final int _rotationSpeed;
	
	public VehicleDeparture(Boat boat)
	{
		_objectId = boat.getObjectId();
		_loc = boat.getMove().getDestination().clone();
		_moveSpeed = (int) boat.getStatus().getMoveSpeed();
		_rotationSpeed = boat.getStatus().getRotationSpeed();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x5A);
		writeD(_objectId);
		writeD(_moveSpeed);
		writeD(_rotationSpeed);
		writeLoc(_loc);
	}
}