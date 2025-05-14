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
package org.l2jpx.gameserver.network.serverpackets;

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.instancemanager.FortSiegeManager;
import org.l2jpx.gameserver.model.FortSiegeSpawn;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * TODO: Rewrite!!!
 * @author KenM
 */
public class ExShowFortressSiegeInfo extends ServerPacket
{
	private final int _fortId;
	private final int _size;
	private final int _csize;
	private final int _csize2;
	
	/**
	 * @param fort
	 */
	public ExShowFortressSiegeInfo(Fort fort)
	{
		_fortId = fort.getResidenceId();
		_size = fort.getFortSize();
		final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortId);
		_csize = ((commanders == null) ? 0 : commanders.size());
		_csize2 = fort.getSiege().getCommanders().size();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_FORTRESS_SIEGE_INFO.writeId(this, buffer);
		buffer.writeInt(_fortId); // Fortress Id
		buffer.writeInt(_size); // Total Barracks Count
		if (_csize > 0)
		{
			switch (_csize)
			{
				case 3:
				{
					switch (_csize2)
					{
						case 0:
						{
							buffer.writeInt(3);
							break;
						}
						case 1:
						{
							buffer.writeInt(2);
							break;
						}
						case 2:
						{
							buffer.writeInt(1);
							break;
						}
						case 3:
						{
							buffer.writeInt(0);
							break;
						}
					}
					break;
				}
				case 4: // TODO: change 4 to 5 once control room supported
				{
					switch (_csize2)
					{
						// TODO: once control room supported, update writeInt(0x0x) to support 5th room
						case 0:
						{
							buffer.writeInt(5);
							break;
						}
						case 1:
						{
							buffer.writeInt(4);
							break;
						}
						case 2:
						{
							buffer.writeInt(3);
							break;
						}
						case 3:
						{
							buffer.writeInt(2);
							break;
						}
						case 4:
						{
							buffer.writeInt(1);
							break;
						}
					}
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < _size; i++)
			{
				buffer.writeInt(0);
			}
		}
	}
}
