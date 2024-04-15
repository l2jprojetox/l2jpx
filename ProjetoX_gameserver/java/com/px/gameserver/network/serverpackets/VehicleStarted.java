package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.actor.Creature;

/**
 * @author Kerberos
 */
public class VehicleStarted extends L2GameServerPacket
{
	private final int _objectId;
	private final int _state;
	
	public VehicleStarted(Creature boat, int state)
	{
		_objectId = boat.getObjectId();
		_state = state;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xBA);
		writeD(_objectId);
		writeD(_state);
	}
}