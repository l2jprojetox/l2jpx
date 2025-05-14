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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.enums.SiegeClanType;
import org.l2jpx.gameserver.model.SiegeClan;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<br>
 * <br>
 * c = 0xcb<br>
 * d = CastleID<br>
 * d = unknown (0)<br>
 * d = unknown (1)<br>
 * d = unknown (0)<br>
 * d = Number of Defending Clans?<br>
 * d = Number of Defending Clans<br>
 * { //repeats<br>
 * d = ClanID<br>
 * S = ClanName<br>
 * S = ClanLeaderName<br>
 * d = ClanCrestID<br>
 * d = signed time (seconds)<br>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<br>
 * d = AllyID<br>
 * S = AllyName<br>
 * S = AllyLeaderName<br>
 * d = AllyCrestID<br>
 * @author Atronic
 */
public class SiegeDefenderList extends ServerPacket
{
	private final Castle _castle;
	
	public SiegeDefenderList(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.CASTLE_SIEGE_DEFENDER_LIST.writeId(this, buffer);
		buffer.writeInt(_castle.getResidenceId());
		buffer.writeInt(0); // Unknown.
		
		final Clan owner = _castle.getOwner();
		buffer.writeInt((owner != null) && _castle.isTimeRegistrationOver()); // Valid registration.
		buffer.writeInt(0); // Unknown.
		
		// Add owners.
		final List<Clan> defenders = new ArrayList<>();
		if (owner != null)
		{
			defenders.add(owner);
		}
		
		// List of confirmed defenders.
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderClans())
		{
			final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if ((clan != null) && (clan != owner))
			{
				defenders.add(clan);
			}
		}
		
		// List of not confirmed defenders.
		for (SiegeClan siegeClan : _castle.getSiege().getDefenderWaitingClans())
		{
			final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if (clan != null)
			{
				defenders.add(clan);
			}
		}
		
		final int size = defenders.size();
		buffer.writeInt(size);
		buffer.writeInt(size);
		
		for (Clan clan : defenders)
		{
			buffer.writeInt(clan.getId());
			buffer.writeString(clan.getName());
			buffer.writeString(clan.getLeaderName());
			buffer.writeInt(clan.getCrestId());
			buffer.writeInt(0); // Signed time in seconds.
			if (clan == owner)
			{
				buffer.writeInt(SiegeClanType.OWNER.ordinal() + 1);
			}
			else if (_castle.getSiege().getDefenderClans().stream().anyMatch(defender -> defender.getClanId() == clan.getId()))
			{
				buffer.writeInt(SiegeClanType.DEFENDER.ordinal() + 1);
			}
			else
			{
				buffer.writeInt(SiegeClanType.DEFENDER_PENDING.ordinal() + 1);
			}
			buffer.writeInt(clan.getAllyId());
			if (clan.getAllyId() != 0)
			{
				final AllianceInfo info = new AllianceInfo(clan.getAllyId());
				buffer.writeString(info.getName());
				buffer.writeString(info.getLeaderP()); // Ally leader name.
				buffer.writeInt(clan.getAllyCrestId());
			}
			else
			{
				buffer.writeString("");
				buffer.writeString(""); // Ally leader name.
				buffer.writeInt(0);
			}
		}
	}
}
