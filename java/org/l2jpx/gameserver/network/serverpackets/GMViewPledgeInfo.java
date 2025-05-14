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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.ClanMember;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class GMViewPledgeInfo extends ServerPacket
{
	private final Clan _clan;
	private final Player _player;
	
	public GMViewPledgeInfo(Clan clan, Player player)
	{
		_clan = clan;
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_PLEDGE_INFO.writeId(this, buffer);
		buffer.writeInt(0);
		buffer.writeString(_player.getName());
		buffer.writeInt(_clan.getId());
		buffer.writeInt(0);
		buffer.writeString(_clan.getName());
		buffer.writeString(_clan.getLeaderName());
		buffer.writeInt(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
		buffer.writeInt(_clan.getLevel());
		buffer.writeInt(_clan.getCastleId());
		buffer.writeInt(_clan.getHideoutId());
		buffer.writeInt(_clan.getFortId());
		buffer.writeInt(_clan.getRank());
		buffer.writeInt(_clan.getReputationScore());
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(0);
		buffer.writeInt(_clan.getAllyId()); // c2
		buffer.writeString(_clan.getAllyName()); // c2
		buffer.writeInt(_clan.getAllyCrestId()); // c2
		buffer.writeInt(_clan.isAtWar()); // c3
		buffer.writeInt(0); // T3 Unknown
		buffer.writeInt(_clan.getMembers().size());
		for (ClanMember member : _clan.getMembers())
		{
			if (member != null)
			{
				buffer.writeString(member.getName());
				buffer.writeInt(member.getLevel());
				buffer.writeInt(member.getClassId());
				buffer.writeInt(member.getSex());
				buffer.writeInt(member.getRaceOrdinal());
				buffer.writeInt(member.isOnline() ? member.getObjectId() : 0);
				buffer.writeInt(member.getSponsor() != 0);
			}
		}
	}
}
