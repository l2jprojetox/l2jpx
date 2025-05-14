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
import org.l2jpx.gameserver.model.Spawn;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * TODO: Rewrite!!!!!!
 * @author KenM
 */
public class ExShowFortressMapInfo extends ServerPacket
{
	private final Fort _fortress;
	
	public ExShowFortressMapInfo(Fort fortress)
	{
		_fortress = fortress;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_FORTRESS_MAP_INFO.writeId(this, buffer);
		buffer.writeInt(_fortress.getResidenceId());
		buffer.writeInt(_fortress.getSiege().isInProgress()); // fortress siege status
		buffer.writeInt(_fortress.getFortSize()); // barracks count
		final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortress.getResidenceId());
		if ((commanders != null) && !commanders.isEmpty() && _fortress.getSiege().isInProgress())
		{
			switch (commanders.size())
			{
				case 3:
				{
					for (FortSiegeSpawn spawn : commanders)
					{
						if (isSpawned(spawn.getId()))
						{
							buffer.writeInt(0);
						}
						else
						{
							buffer.writeInt(1);
						}
					}
					break;
				}
				case 4: // TODO: change 4 to 5 once control room supported
				{
					int count = 0;
					for (FortSiegeSpawn spawn : commanders)
					{
						count++;
						if (count == 4)
						{
							buffer.writeInt(1); // TODO: control room emulated
						}
						if (isSpawned(spawn.getId()))
						{
							buffer.writeInt(0);
						}
						else
						{
							buffer.writeInt(1);
						}
					}
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < _fortress.getFortSize(); i++)
			{
				buffer.writeInt(0);
			}
		}
	}
	
	/**
	 * @param npcId
	 * @return
	 */
	private boolean isSpawned(int npcId)
	{
		boolean ret = false;
		for (Spawn spawn : _fortress.getSiege().getCommanders())
		{
			if (spawn.getId() == npcId)
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
}
