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

import org.l2jpx.commons.network.ReadablePacket;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;

/**
 * @author Mobius
 */
public abstract class ClientPacket extends ReadablePacket<GameClient>
{
	@Override
	public boolean read()
	{
		try
		{
			readImpl();
			return true;
		}
		catch (Exception e)
		{
			PacketLogger.warning("Client: " + getClient() + " - Failed reading: " + getClass().getSimpleName() + " ; " + e.getMessage());
			PacketLogger.warning(CommonUtil.getStackTrace(e));
		}
		return false;
	}
	
	protected abstract void readImpl();
	
	@Override
	public void run()
	{
		try
		{
			runImpl();
		}
		catch (Exception e)
		{
			PacketLogger.warning("Client: " + getClient() + " - Failed running: " + getClass().getSimpleName() + " ; " + e.getMessage());
			PacketLogger.warning(CommonUtil.getStackTrace(e));
			
			// In case of EnterWorld error kick player from game.
			if (this instanceof EnterWorld)
			{
				getClient().closeNow();
			}
		}
	}
	
	protected abstract void runImpl();
	
	/**
	 * @return the active player if exist, otherwise null.
	 */
	protected Player getPlayer()
	{
		return getClient().getPlayer();
	}
}
