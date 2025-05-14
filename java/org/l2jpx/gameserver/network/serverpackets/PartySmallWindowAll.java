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

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class PartySmallWindowAll extends ServerPacket
{
	private final Party _party;
	private final Player _exclude;
	
	public PartySmallWindowAll(Player exclude, Party party)
	{
		_exclude = exclude;
		_party = party;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PARTY_SMALL_WINDOW_ALL.writeId(this, buffer);
		buffer.writeInt(_party.getLeaderObjectId());
		buffer.writeByte(_party.getDistributionType().getId());
		buffer.writeByte(_party.getMemberCount() - 1);
		for (Player member : _party.getMembers())
		{
			if ((member != null) && (member != _exclude))
			{
				buffer.writeInt(member.getObjectId());
				buffer.writeString(member.getName());
				buffer.writeInt((int) member.getCurrentCp()); // c4
				buffer.writeInt(member.getMaxCp()); // c4
				buffer.writeInt((int) member.getCurrentHp());
				buffer.writeInt(member.getMaxHp());
				buffer.writeInt((int) member.getCurrentMp());
				buffer.writeInt(member.getMaxMp());
				buffer.writeInt(member.getVitalityPoints());
				buffer.writeByte(member.getLevel());
				buffer.writeShort(member.getClassId().getId());
				buffer.writeByte(1); // Unk
				buffer.writeShort(member.getRace().ordinal());
				final Summon pet = member.getPet();
				buffer.writeInt(member.getServitors().size() + (pet != null ? 1 : 0)); // Summon size, one only atm
				if (pet != null)
				{
					buffer.writeInt(pet.getObjectId());
					buffer.writeInt(pet.getId() + 1000000);
					buffer.writeByte(pet.getSummonType());
					buffer.writeString(pet.getName());
					buffer.writeInt((int) pet.getCurrentHp());
					buffer.writeInt(pet.getMaxHp());
					buffer.writeInt((int) pet.getCurrentMp());
					buffer.writeInt(pet.getMaxMp());
					buffer.writeByte(pet.getLevel());
				}
				member.getServitors().values().forEach(s ->
				{
					buffer.writeInt(s.getObjectId());
					buffer.writeInt(s.getId() + 1000000);
					buffer.writeByte(s.getSummonType());
					buffer.writeString(s.getName());
					buffer.writeInt((int) s.getCurrentHp());
					buffer.writeInt(s.getMaxHp());
					buffer.writeInt((int) s.getCurrentMp());
					buffer.writeInt(s.getMaxMp());
					buffer.writeByte(s.getLevel());
				});
			}
		}
	}
}
