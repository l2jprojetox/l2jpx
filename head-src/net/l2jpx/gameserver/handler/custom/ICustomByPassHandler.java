package net.l2jpx.gameserver.handler.custom;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Azagthtot
 */
public interface ICustomByPassHandler
{
	/**
	 * @return as String -
	 */
	public String[] getByPassCommands();
	
	public void handleCommand(String command, L2PcInstance player, String parameters);
}
