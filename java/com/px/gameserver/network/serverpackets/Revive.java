package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.WorldObject;

/**
 * format d
 */
public class Revive extends L2GameServerPacket
{
	private final int _objectId;
	
	public Revive(WorldObject obj)
	{
		_objectId = obj.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x07);
		writeD(_objectId);
	}
}