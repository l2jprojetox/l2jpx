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
import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.Clan.SubPledge;
import org.l2jpx.gameserver.model.clan.ClanMember;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class PledgeShowMemberListAll extends ServerPacket
{
	private final Clan _clan;
	private final SubPledge _pledge;
	private final String _name;
	private final String _leaderName;
	private final Collection<ClanMember> _members;
	private final int _pledgeId;
	private final boolean _isSubPledge;
	
	private PledgeShowMemberListAll(Clan clan, SubPledge pledge, boolean isSubPledge)
	{
		_clan = clan;
		_pledge = pledge;
		_pledgeId = _pledge == null ? 0 : _pledge.getId();
		_leaderName = pledge == null ? clan.getLeaderName() : CharInfoTable.getInstance().getNameById(pledge.getLeaderId());
		_name = pledge == null ? clan.getName() : pledge.getName();
		_members = _clan.getMembers();
		_isSubPledge = isSubPledge;
	}
	
	public static void sendAllTo(Player player)
	{
		final Clan clan = player.getClan();
		if (clan != null)
		{
			for (SubPledge subPledge : clan.getAllSubPledges())
			{
				player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
			}
			player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(this, buffer);
		buffer.writeInt(!_isSubPledge);
		buffer.writeInt(_clan.getId());
		buffer.writeInt(Config.SERVER_ID);
		buffer.writeInt(_pledgeId);
		buffer.writeString(_name);
		buffer.writeString(_leaderName);
		buffer.writeInt(_clan.getCrestId()); // crest id .. is used again
		buffer.writeInt(_clan.getLevel());
		buffer.writeInt(_clan.getCastleId());
		buffer.writeInt(0);
		buffer.writeInt(_clan.getHideoutId());
		buffer.writeInt(_clan.getFortId());
		buffer.writeInt(_clan.getRank());
		buffer.writeInt(_clan.getReputationScore());
		buffer.writeInt(0); // 0
		buffer.writeInt(0); // 0
		buffer.writeInt(_clan.getAllyId());
		buffer.writeString(_clan.getAllyName());
		buffer.writeInt(_clan.getAllyCrestId());
		buffer.writeInt(_clan.isAtWar()); // new c3
		buffer.writeInt(0); // Territory castle ID
		buffer.writeInt(_clan.getSubPledgeMembersCount(_pledgeId));
		for (ClanMember m : _members)
		{
			if (m.getPledgeType() != _pledgeId)
			{
				continue;
			}
			buffer.writeString(m.getName());
			buffer.writeInt(m.getLevel());
			buffer.writeInt(m.getClassId());
			final Player player = m.getPlayer();
			if (player != null)
			{
				buffer.writeInt(player.getAppearance().isFemale()); // no visible effect
				buffer.writeInt(player.getRace().ordinal()); // buffer.writeInt(1);
			}
			else
			{
				buffer.writeInt(1); // no visible effect
				buffer.writeInt(1); // buffer.writeInt(1);
			}
			buffer.writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
			buffer.writeInt(m.getSponsor() != 0);
			buffer.writeByte(m.getOnlineStatus());
		}
	}
}
