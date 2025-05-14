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

import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.data.sql.CrestTable;
import org.l2jpx.gameserver.enums.CrestType;
import org.l2jpx.gameserver.model.Crest;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * Client packet for setting ally crest.
 */
public class RequestSetAllyCrest extends ClientPacket
{
	private int _length;
	private byte[] _data = null;
	
	@Override
	protected void readImpl()
	{
		_length = readInt();
		if (_length > 192)
		{
			return;
		}
		
		_data = readBytes(_length);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_length < 0)
		{
			player.sendMessage("File transfer error.");
			return;
		}
		
		if (_length > 192)
		{
			player.sendPacket(SystemMessageId.PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12);
			return;
		}
		
		if (player.getAllyId() == 0)
		{
			player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}
		
		final Clan leaderClan = ClanTable.getInstance().getClan(player.getAllyId());
		if ((player.getClanId() != leaderClan.getId()) || !player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}
		
		if (_length == 0)
		{
			if (leaderClan.getAllyCrestId() != 0)
			{
				leaderClan.changeAllyCrest(0, false);
			}
		}
		else
		{
			final Crest crest = CrestTable.getInstance().createCrest(_data, CrestType.ALLY);
			if (crest != null)
			{
				leaderClan.changeAllyCrest(crest.getId(), false);
				player.sendPacket(SystemMessageId.THE_CREST_WAS_SUCCESSFULLY_REGISTERED);
			}
		}
	}
}
