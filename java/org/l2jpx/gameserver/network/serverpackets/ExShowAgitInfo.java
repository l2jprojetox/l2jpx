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
import org.l2jpx.gameserver.data.xml.ClanHallData;
import org.l2jpx.gameserver.model.residences.ClanHall;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends ServerPacket
{
	public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();
	
	private ExShowAgitInfo()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_AGIT_INFO.writeId(this, buffer);
		final Collection<ClanHall> clanHalls = ClanHallData.getInstance().getClanHalls();
		buffer.writeInt(clanHalls.size());
		clanHalls.forEach(clanHall ->
		{
			buffer.writeInt(clanHall.getResidenceId());
			buffer.writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()); // owner clan name
			buffer.writeString(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()); // leader name
			buffer.writeInt(clanHall.getType().getClientVal()); // Clan hall type
		});
	}
}
