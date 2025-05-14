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
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.ClanWar;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author -Wooden-
 */
public class PledgeReceiveWarList extends ServerPacket
{
	private final Clan _clan;
	private final int _tab;
	private final Collection<ClanWar> _clanList;
	
	public PledgeReceiveWarList(Clan clan, int tab)
	{
		_clan = clan;
		_tab = tab;
		_clanList = clan.getWarList().values();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_RECEIVE_WAR_LIST.writeId(this, buffer);
		buffer.writeInt(_tab); // page
		buffer.writeInt(_clanList.size());
		for (ClanWar clanWar : _clanList)
		{
			final Clan clan = clanWar.getOpposingClan(_clan);
			if (clan == null)
			{
				continue;
			}
			buffer.writeString(clan.getName());
			buffer.writeInt(clanWar.getState().ordinal()); // type: 0 = Declaration, 1 = Blood Declaration, 2 = In War, 3 = Victory, 4 = Defeat, 5 = Tie, 6 = Error
			buffer.writeInt(clanWar.getRemainingTime()); // Time if friends to start remaining
			buffer.writeInt(clanWar.getKillDifference(_clan)); // Score
			buffer.writeInt(0); // @TODO: Recent change in points
			buffer.writeInt(clanWar.getKillToStart()); // Friends to start war left
		}
	}
}
