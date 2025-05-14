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
import org.l2jpx.Config;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class PledgeShowInfoUpdate extends ServerPacket
{
	private final Clan _clan;
	
	public PledgeShowInfoUpdate(Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PLEDGE_SHOW_INFO_UPDATE.writeId(this, buffer);
		// sending empty data so client will ask all the info in response ;)
		buffer.writeInt(_clan.getId());
		buffer.writeInt(Config.SERVER_ID);
		buffer.writeInt(_clan.getCrestId());
		buffer.writeInt(_clan.getLevel()); // clan level
		buffer.writeInt(_clan.getCastleId());
		buffer.writeInt(0); // castle state ?
		buffer.writeInt(_clan.getHideoutId());
		buffer.writeInt(_clan.getFortId());
		buffer.writeInt(_clan.getRank());
		buffer.writeInt(_clan.getReputationScore()); // clan reputation score
		buffer.writeInt(0); // ?
		buffer.writeInt(0); // ?
		buffer.writeInt(_clan.getAllyId());
		buffer.writeString(_clan.getAllyName()); // c5
		buffer.writeInt(_clan.getAllyCrestId()); // c5
		buffer.writeInt(_clan.isAtWar()); // c5
		buffer.writeInt(0); // TODO: Find me!
		buffer.writeInt(0); // TODO: Find me!
	}
}
