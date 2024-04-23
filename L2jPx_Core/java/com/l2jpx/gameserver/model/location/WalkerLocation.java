package com.l2jpx.gameserver.model.location;

import com.l2jpx.commons.data.StatSet;

/**
 * A datatype extending {@link Location}, used as a unique node of a pre-programmed route for Walker NPCs.<br>
 * <br>
 * Added to the x/y/z informations, you can also find delay (the time the Walker NPC will stand on the point without moving), the String to broadcast (null if none) and the running behavior.
 */
public class WalkerLocation extends Location
{
	private final boolean _mustRun;
	private final int _delay;
	private final String _chat;
	
	public WalkerLocation(StatSet set, boolean run)
	{
		super(set.getInteger("X"), set.getInteger("Y"), set.getInteger("Z"));
		
		_mustRun = run;
		_delay = set.getInteger("delay", 0) * 1000;
		_chat = set.getString("chat", null);
	}
	
	public boolean mustRun()
	{
		return _mustRun;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public String getChat()
	{
		return _chat;
	}
}