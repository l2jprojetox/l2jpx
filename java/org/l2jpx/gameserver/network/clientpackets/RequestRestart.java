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
package org.l2jpx.gameserver.network.clientpackets;

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.enums.TeleportWhereType;
import org.l2jpx.gameserver.instancemanager.MapRegionManager;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.olympiad.OlympiadManager;
import org.l2jpx.gameserver.model.variables.PlayerVariables;
import org.l2jpx.gameserver.network.ConnectionState;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2jpx.gameserver.network.serverpackets.RestartResponse;
import org.l2jpx.gameserver.util.OfflineTradeUtil;

/**
 * @version $Revision: 1.11.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRestart extends ClientPacket
{
	protected static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.canLogout())
		{
			player.sendPacket(RestartResponse.FALSE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Unregister from olympiad.
		if (OlympiadManager.getInstance().isRegistered(player))
		{
			OlympiadManager.getInstance().unRegisterNoble(player);
		}
		
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			if (Config.RESTORE_PLAYER_INSTANCE)
			{
				player.getVariables().set(PlayerVariables.INSTANCE_RESTORE, world.getId());
			}
			else
			{
				Location location = world.getExitLocation(player);
				if (location == null)
				{
					location = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
				}
				player.getVariables().set(PlayerVariables.RESTORE_LOCATION, location.getX() + ";" + location.getY() + ";" + location.getZ());
			}
			player.setInstance(null);
		}
		
		final GameClient client = getClient();
		LOGGER_ACCOUNTING.info("Logged out, " + client);
		
		if (!OfflineTradeUtil.enteredOfflineMode(player))
		{
			Disconnection.of(client, player).storeMe().deleteMe();
		}
		
		// Return the client to the authenticated status.
		client.setConnectionState(ConnectionState.AUTHENTICATED);
		
		client.sendPacket(RestartResponse.TRUE);
		
		// Send character list.
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1);
		client.sendPacket(new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1));
		client.setCharSelection(cl.getCharInfo());
	}
}
