package com.l2jpx.gameserver.network.serverpackets;

import com.l2jpx.Config;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.location.Location;

public class ExFishingStart extends L2GameServerPacket
{
	private final int _objectId;
	private final Location _loc;
	private final int _fishType;
	private final boolean _isNightLure;
	
	public ExFishingStart(Creature creature, int fishType, Location loc, boolean isNightLure)
	{
		_objectId = creature.getObjectId();
		_fishType = fishType;
		_loc = loc;
		_isNightLure = isNightLure;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x13);
		
		writeD(_objectId);
		writeD(_fishType);
		writeLoc(_loc);
		writeC(_isNightLure ? 0x01 : 0x00);
		writeC(Config.ALLOW_FISH_CHAMPIONSHIP ? 0x01 : 0x00); // show fish rank result button
	}
}