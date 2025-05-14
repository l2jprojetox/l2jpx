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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.gameserver.instancemanager.ClanEntryManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.ExPledgeRecruitBoardSearch;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitBoardSearch extends ClientPacket
{
	private int _clanLevel;
	private int _karma;
	private int _type;
	private String _query;
	private int _sort;
	private boolean _descending;
	private int _page;
	@SuppressWarnings("unused")
	private int _applicationType;
	
	@Override
	protected void readImpl()
	{
		_clanLevel = readInt();
		_karma = readInt();
		_type = readInt();
		_query = readString();
		_sort = readInt();
		_descending = readInt() == 2;
		_page = readInt();
		_applicationType = readInt(); // Helios
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_query.isEmpty())
		{
			if ((_karma < 0) && (_clanLevel < 0))
			{
				player.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getUnSortedClanList(), _page));
			}
			else
			{
				player.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanList(_clanLevel, _karma, _sort, _descending), _page));
			}
		}
		else
		{
			player.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanListByName(_query.toLowerCase(), _type), _page));
		}
	}
}
