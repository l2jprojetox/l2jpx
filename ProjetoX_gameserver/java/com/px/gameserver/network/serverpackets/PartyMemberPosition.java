package com.px.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.model.location.Location;

public class PartyMemberPosition extends L2GameServerPacket
{
	Map<Integer, Location> _locations = new HashMap<>();
	
	public PartyMemberPosition(Party party)
	{
		reuse(party);
	}
	
	public void reuse(Party party)
	{
		_locations.clear();
		
		for (Player member : party.getMembers())
			_locations.put(member.getObjectId(), new Location(member.getX(), member.getY(), member.getZ()));
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xa7);
		writeD(_locations.size());
		
		for (Map.Entry<Integer, Location> entry : _locations.entrySet())
		{
			writeD(entry.getKey());
			writeLoc(entry.getValue());
		}
	}
}