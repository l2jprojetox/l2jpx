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
package org.l2jpx.gameserver.network.serverpackets.pledgebonus;

import java.util.Comparator;
import java.util.logging.Logger;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.ClanRewardData;
import org.l2jpx.gameserver.enums.ClanRewardType;
import org.l2jpx.gameserver.model.clan.ClanRewardBonus;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusList extends ServerPacket
{
	private static final Logger LOGGER = Logger.getLogger(ExPledgeBonusList.class.getName());
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_PLEDGE_BONUS_LIST.writeId(this, buffer);
		for (ClanRewardType type : ClanRewardType.values())
		{
			ClanRewardData.getInstance().getClanRewardBonuses(type).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
			{
				switch (type)
				{
					case MEMBERS_ONLINE:
					{
						if (bonus.getSkillReward() == null)
						{
							LOGGER.warning("Missing clan reward skill for reward level: " + bonus.getLevel());
							buffer.writeInt(0);
							return;
						}
						buffer.writeInt(bonus.getSkillReward().getSkillId());
						break;
					}
					case HUNTING_MONSTERS:
					{
						if (bonus.getItemReward() == null)
						{
							LOGGER.warning("Missing clan reward skill for reward level: " + bonus.getLevel());
							buffer.writeInt(0);
							return;
						}
						buffer.writeInt(bonus.getItemReward().getId());
						break;
					}
				}
			});
		}
	}
}
