package net.l2jpx.gameserver.handler;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Azagthtot
 */
public interface IBBSHandler
{
	/**
	 * @return as String [] bbs)
	 */
	public String[] getBBSCommands();
	
	/**
	 * @param command    as String - bbs<br>
	 * @param activeChar as L2PcInstance - Alt+B<br>
	 * @param params     as String -
	 */
	public void handleCommand(String command, L2PcInstance activeChar, String params);
}
