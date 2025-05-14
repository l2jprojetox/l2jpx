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
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardSearch extends ServerPacket
{
	final List<PledgeRecruitInfo> _clanList;
	private final int _currentPage;
	private final int _totalNumberOfPage;
	private final int _clanOnCurrentPage;
	private final int _startIndex;
	private final int _endIndex;
	static final int CLAN_PER_PAGE = 12;
	
	public ExPledgeRecruitBoardSearch(List<PledgeRecruitInfo> clanList, int currentPage)
	{
		_clanList = clanList;
		_currentPage = currentPage;
		_totalNumberOfPage = (int) Math.ceil((double) _clanList.size() / CLAN_PER_PAGE);
		_startIndex = (_currentPage - 1) * CLAN_PER_PAGE;
		_endIndex = (_startIndex + CLAN_PER_PAGE) > _clanList.size() ? _clanList.size() : _startIndex + CLAN_PER_PAGE;
		_clanOnCurrentPage = _endIndex - _startIndex;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PLEDGE_RECRUIT_BOARD_SEARCH.writeId(this, buffer);
		buffer.writeInt(_currentPage);
		buffer.writeInt(_totalNumberOfPage);
		buffer.writeInt(_clanOnCurrentPage);
		for (int i = _startIndex; i < _endIndex; i++)
		{
			buffer.writeInt(_clanList.get(i).getClanId());
			buffer.writeInt(_clanList.get(i).getClan().getAllyId());
		}
		for (int i = _startIndex; i < _endIndex; i++)
		{
			final Clan clan = _clanList.get(i).getClan();
			buffer.writeInt(clan.getCrestId());
			buffer.writeInt(clan.getAllyCrestId());
			buffer.writeString(clan.getName());
			buffer.writeString(clan.getLeaderName());
			buffer.writeInt(clan.getLevel());
			buffer.writeInt(clan.getMembersCount());
			buffer.writeInt(_clanList.get(i).getKarma());
			buffer.writeString(_clanList.get(i).getInformation());
			buffer.writeInt(_clanList.get(i).getApplicationType()); // Helios
			buffer.writeInt(_clanList.get(i).getRecruitType()); // Helios
		}
	}
}
