package com.l2jpx.gameserver.network.serverpackets;

import com.l2jpx.gameserver.model.actor.Creature;

public class ValidateLocation extends L2GameServerPacket
{
	private final int _objectId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	
	public ValidateLocation(Creature creature)
	{
		_objectId = creature.getObjectId();
		_x = creature.getX();
		_y = creature.getY();
		_z = creature.getZ();
		_heading = creature.getHeading();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x61);
		writeD(_objectId);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
	}
}