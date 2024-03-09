package net.l2jpx.gameserver.network;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Nick
 */
public class Disconnection implements Runnable
{
	
	private final L2PcInstance activeChar;
	
	public Disconnection(final L2PcInstance activeChar)
	{
		this.activeChar = activeChar;
	}
	
	@Override
	public void run()
	{
		activeChar.closeNetConnection();
	}
}
