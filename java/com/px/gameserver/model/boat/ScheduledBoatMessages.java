package com.px.gameserver.model.boat;

import java.util.List;

import com.px.gameserver.network.serverpackets.L2GameServerPacket;

public class ScheduledBoatMessages
{
	private final int _delay;
	private final List<L2GameServerPacket> _messages;
	
	public ScheduledBoatMessages(int delay, List<L2GameServerPacket> messages)
	{
		_delay = delay;
		_messages = messages;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public List<L2GameServerPacket> getMessages()
	{
		return _messages;
	}
}