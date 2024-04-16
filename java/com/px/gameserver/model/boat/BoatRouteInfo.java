package com.px.gameserver.model.boat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.model.location.BoatLocation;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.BoatSay;
import com.px.gameserver.network.serverpackets.ExServerPrimitive;
import com.px.gameserver.network.serverpackets.L2GameServerPacket;

public class BoatRouteInfo
{
	private final BoatLocation[] _paths;
	private final BoatDock _dock;
	private final int _itemId;
	private final BoatSay _isBusy;
	
	private final List<ScheduledBoatMessages> _scheduledBoatMessages = new ArrayList<>();
	
	public BoatRouteInfo(BoatLocation[] paths, BoatDock dock, int itemId)
	{
		_paths = paths;
		_dock = dock;
		_itemId = itemId;
		
		_isBusy = _paths[_paths.length - 1].getBusyMessage();
		
		if (dock.isBusyOnStart())
			dock.setBusy(true);
		
		// Add scheduled messages.
		for (BoatLocation path : _paths)
		{
			final IntIntHolder[] messages = path.getScheduledMessages();
			if (messages == null)
				continue;
			
			for (IntIntHolder holder : messages)
			{
				final BoatSay bs = new BoatSay(SystemMessageId.getSystemMessageId(holder.getId()));
				
				for (ScheduledBoatMessages sbm : _scheduledBoatMessages)
				{
					if (sbm.getDelay() == holder.getValue())
					{
						sbm.getMessages().add(bs);
						return;
					}
				}
				
				final List<L2GameServerPacket> list = new ArrayList<>();
				list.add(bs);
				
				_scheduledBoatMessages.add(new ScheduledBoatMessages(holder.getValue(), list));
			}
		}
	}
	
	public List<ScheduledBoatMessages> getScheduledMessages()
	{
		return _scheduledBoatMessages;
	}
	
	public BoatDock getDock()
	{
		return _dock;
	}
	
	public BoatSay getBusyMessage()
	{
		return _isBusy;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public BoatLocation[] getPaths()
	{
		return _paths;
	}
	
	public void visualize(ExServerPrimitive debug)
	{
		for (int i = 0; i < _paths.length; i++)
		{
			int nextIndex = i + 1;
			
			// ending point to first one
			if (nextIndex == _paths.length)
				continue;
			
			final BoatLocation curPoint = _paths[i];
			final BoatLocation nextPoint = _paths[nextIndex];
			
			debug.addLine("Segment #" + i, Color.YELLOW, true, curPoint, nextPoint);
		}
	}
}