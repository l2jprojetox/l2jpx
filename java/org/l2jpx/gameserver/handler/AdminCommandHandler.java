/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.enums.PlayerAction;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ConfirmDlg;
import org.l2jpx.gameserver.util.GMAudit;
import org.l2jpx.gameserver.util.TimeAmountInterpreter;

/**
 * @author UnAfraid
 */
public class AdminCommandHandler implements IHandler<IAdminCommandHandler, String>
{
	private static final Logger LOGGER = Logger.getLogger(AdminCommandHandler.class.getName());
	
	private final Map<String, IAdminCommandHandler> _datatable;
	
	protected AdminCommandHandler()
	{
		_datatable = new HashMap<>();
	}
	
	@Override
	public void registerHandler(IAdminCommandHandler handler)
	{
		for (String id : handler.getAdminCommandList())
		{
			_datatable.put(id, handler);
		}
	}
	
	@Override
	public synchronized void removeHandler(IAdminCommandHandler handler)
	{
		for (String id : handler.getAdminCommandList())
		{
			_datatable.remove(id);
		}
	}
	
	/**
	 * WARNING: Please use {@link #useAdminCommand(Player, String, boolean)} instead.
	 */
	@Override
	public IAdminCommandHandler getHandler(String adminCommand)
	{
		String command = adminCommand;
		if (adminCommand.contains(" "))
		{
			command = adminCommand.substring(0, adminCommand.indexOf(' '));
		}
		return _datatable.get(command);
	}
	
	public void useAdminCommand(Player player, String fullCommand, boolean useConfirm)
	{
		if (!player.isGM())
		{
			return;
		}
		
		final String command = fullCommand.split(" ")[0];
		final String commandNoPrefix = command.substring(6);
		final IAdminCommandHandler handler = getHandler(command);
		if (handler == null)
		{
			player.sendMessage("The command '" + commandNoPrefix + "' does not exist!");
			LOGGER.warning("No handler registered for admin command '" + command + "'");
			return;
		}
		
		if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
		{
			player.sendMessage("You don't have the access rights to use this command!");
			LOGGER.warning(player + " tried to use admin command '" + command + "', without proper access level!");
			return;
		}
		
		if (useConfirm && AdminData.getInstance().requireConfirm(command))
		{
			player.setAdminConfirmCmd(fullCommand);
			final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1_3);
			dlg.getSystemMessage().addString("Are you sure you want execute command '" + commandNoPrefix + "' ?");
			player.addAction(PlayerAction.ADMIN_COMMAND);
			player.sendPacket(dlg);
		}
		else
		{
			// Admin Commands must run through a long running task, otherwise a command that takes too much time will freeze the server, this way you'll feel only a minor spike.
			ThreadPool.execute(() ->
			{
				final long begin = System.currentTimeMillis();
				try
				{
					if (Config.GMAUDIT)
					{
						final WorldObject target = player.getTarget();
						GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", fullCommand, (target != null ? target.getName() : "no-target"));
					}
					
					handler.useAdminCommand(fullCommand, player);
				}
				catch (RuntimeException e)
				{
					player.sendMessage("Exception during execution of  '" + fullCommand + "': " + e.toString());
					LOGGER.log(Level.WARNING, "Exception during execution of " + fullCommand, e);
				}
				finally
				{
					final long runtime = System.currentTimeMillis() - begin;
					if (runtime > 5000)
					{
						player.sendMessage("The execution of '" + fullCommand + "' took " + TimeAmountInterpreter.consolidateMillis(runtime) + ".");
					}
				}
			});
		}
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}
