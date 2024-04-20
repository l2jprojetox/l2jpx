package com.l2jpx.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jpx.gameserver.enums.StatusType;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.holder.IntIntHolder;

public class StatusUpdate extends L2GameServerPacket
{
	private final int _objectId;
	private final List<IntIntHolder> _attributes;
	
	public StatusUpdate(WorldObject object)
	{
		_attributes = new ArrayList<>();
		_objectId = object.getObjectId();
	}
	
	public void addAttribute(StatusType type, int level)
	{
		_attributes.add(new IntIntHolder(type.getId(), level));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x0e);
		writeD(_objectId);
		writeD(_attributes.size());
		
		for (IntIntHolder temp : _attributes)
		{
			writeD(temp.getId());
			writeD(temp.getValue());
		}
	}
}