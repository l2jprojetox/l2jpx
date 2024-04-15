package com.px.gameserver.handler;

import com.px.gameserver.model.actor.Player;

public interface IAdminCommandHandler
{
	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param activeChar
	 * @param command
	 * @return command success
	 */
	public boolean useAdminCommand(String command, Player activeChar);
	
	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	public String[] getAdminCommandList();
}
