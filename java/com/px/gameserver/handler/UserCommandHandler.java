package com.px.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.px.gameserver.handler.usercommandhandlers.ChannelDelete;
import com.px.gameserver.handler.usercommandhandlers.ChannelLeave;
import com.px.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import com.px.gameserver.handler.usercommandhandlers.ClanPenalty;
import com.px.gameserver.handler.usercommandhandlers.ClanWarsList;
import com.px.gameserver.handler.usercommandhandlers.Dismount;
import com.px.gameserver.handler.usercommandhandlers.Escape;
import com.px.gameserver.handler.usercommandhandlers.Loc;
import com.px.gameserver.handler.usercommandhandlers.Mount;
import com.px.gameserver.handler.usercommandhandlers.OlympiadStat;
import com.px.gameserver.handler.usercommandhandlers.PartyInfo;
import com.px.gameserver.handler.usercommandhandlers.SiegeStatus;
import com.px.gameserver.handler.usercommandhandlers.Time;

public class UserCommandHandler
{
	private final Map<Integer, IUserCommandHandler> _entries = new HashMap<>();
	
	protected UserCommandHandler()
	{
		registerHandler(new ChannelDelete());
		registerHandler(new ChannelLeave());
		registerHandler(new ChannelListUpdate());
		registerHandler(new ClanPenalty());
		registerHandler(new ClanWarsList());
		registerHandler(new Dismount());
		registerHandler(new Escape());
		registerHandler(new Loc());
		registerHandler(new Mount());
		registerHandler(new OlympiadStat());
		registerHandler(new PartyInfo());
		registerHandler(new SiegeStatus());
		registerHandler(new Time());
	}
	
	private void registerHandler(IUserCommandHandler handler)
	{
		for (int id : handler.getUserCommandList())
			_entries.put(id, handler);
	}
	
	public IUserCommandHandler getHandler(int userCommand)
	{
		return _entries.get(userCommand);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static UserCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final UserCommandHandler INSTANCE = new UserCommandHandler();
	}
}