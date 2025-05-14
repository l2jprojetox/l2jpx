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

import org.l2jpx.gameserver.enums.CharacterDeleteFailType;
import org.l2jpx.gameserver.model.CharSelectInfoPackage;
import org.l2jpx.gameserver.model.events.Containers;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerDelete;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.CharDeleteFail;
import org.l2jpx.gameserver.network.serverpackets.CharDeleteSuccess;
import org.l2jpx.gameserver.network.serverpackets.CharSelectionInfo;

/**
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterDelete extends ClientPacket
{
	// cd
	private int _charSlot;
	
	@Override
	protected void readImpl()
	{
		_charSlot = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		
		// if (!client.getFloodProtectors().canSelectCharacter())
		// {
		// client.sendPacket(new CharDeleteFail(CharacterDeleteFailType.UNKNOWN));
		// return;
		// }
		
		try
		{
			final CharacterDeleteFailType failType = client.markToDeleteChar(_charSlot);
			switch (failType)
			{
				case NONE:// Success!
				{
					client.sendPacket(new CharDeleteSuccess());
					final CharSelectInfoPackage charInfo = client.getCharSelection(_charSlot);
					if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_DELETE, Containers.Players()))
					{
						EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), client), Containers.Players());
					}
					break;
				}
				default:
				{
					client.sendPacket(new CharDeleteFail(failType));
					break;
				}
			}
		}
		catch (Exception e)
		{
			PacketLogger.warning(getClass().getSimpleName() + ": " + e.getMessage());
		}
		
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1, 0);
		client.sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}
