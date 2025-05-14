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

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.enums.TaxType;
import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowCastleInfo extends ServerPacket
{
	public ExShowCastleInfo()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_CASTLE_INFO.writeId(this, buffer);
		final Collection<Castle> castles = CastleManager.getInstance().getCastles();
		buffer.writeInt(castles.size());
		for (Castle castle : castles)
		{
			buffer.writeInt(castle.getResidenceId());
			if (castle.getOwnerId() > 0)
			{
				if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null)
				{
					buffer.writeString(ClanTable.getInstance().getClan(castle.getOwnerId()).getName());
				}
				else
				{
					PacketLogger.warning("Castle owner with no name! Castle: " + castle.getName() + " has an OwnerId = " + castle.getOwnerId() + " who does not have a  name!");
					buffer.writeString("");
				}
			}
			else
			{
				buffer.writeString("");
			}
			buffer.writeInt(castle.getTaxPercent(TaxType.BUY));
			buffer.writeInt((int) (castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
			buffer.writeByte(castle.getSiege().isInProgress()); // Grand Crusade
			buffer.writeByte(castle.getSide().ordinal()); // Grand Crusade
		}
	}
}
