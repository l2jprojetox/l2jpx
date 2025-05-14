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
import org.l2jpx.gameserver.instancemanager.ClanEntryManager;
import org.l2jpx.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2jpx.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends ServerPacket
{
	private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
	private final PledgeRecruitInfo _pledgeRecruitInfo;
	
	public ExPledgeWaitingListApplied(int clanId, int playerId)
	{
		_pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
		_pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PLEDGE_WAITING_LIST_APPLIED.writeId(this, buffer);
		buffer.writeInt(_pledgeRecruitInfo.getClan().getId());
		buffer.writeString(_pledgeRecruitInfo.getClan().getName());
		buffer.writeString(_pledgeRecruitInfo.getClan().getLeaderName());
		buffer.writeInt(_pledgeRecruitInfo.getClan().getLevel());
		buffer.writeInt(_pledgeRecruitInfo.getClan().getMembersCount());
		buffer.writeInt(_pledgeRecruitInfo.getKarma());
		buffer.writeString(_pledgeRecruitInfo.getInformation());
		buffer.writeString(_pledgePlayerRecruitInfo.getMessage());
	}
}
