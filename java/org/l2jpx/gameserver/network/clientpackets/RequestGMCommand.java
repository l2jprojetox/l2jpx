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
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.serverpackets.GMHennaInfo;
import org.l2jpx.gameserver.network.serverpackets.GMViewCharacterInfo;
import org.l2jpx.gameserver.network.serverpackets.GMViewItemList;
import org.l2jpx.gameserver.network.serverpackets.GMViewPledgeInfo;
import org.l2jpx.gameserver.network.serverpackets.GMViewSkillInfo;
import org.l2jpx.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;
import org.l2jpx.gameserver.network.serverpackets.GmViewQuestInfo;

/**
 * @version $Revision: 1.1.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGMCommand extends ClientPacket
{
	private String _targetName;
	private int _command;
	
	@Override
	protected void readImpl()
	{
		_targetName = readString();
		_command = readInt();
		// _unknown = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		// prevent non GM or low level GMs from vieweing player stuff
		final GameClient client = getClient();
		if (!client.getPlayer().isGM() || !client.getPlayer().getAccessLevel().allowAltG())
		{
			return;
		}
		
		final Player player = World.getInstance().getPlayer(_targetName);
		final Clan clan = ClanTable.getInstance().getClanByName(_targetName);
		
		// player name was incorrect?
		if ((player == null) && ((clan == null) || (_command != 6)))
		{
			return;
		}
		
		switch (_command)
		{
			case 1: // player status
			{
				client.sendPacket(new GMViewCharacterInfo(player));
				client.sendPacket(new GMHennaInfo(player));
				break;
			}
			case 2: // player clan
			{
				if ((player != null) && (player.getClan() != null))
				{
					client.sendPacket(new GMViewPledgeInfo(player.getClan(), player));
				}
				break;
			}
			case 3: // player skills
			{
				client.sendPacket(new GMViewSkillInfo(player));
				break;
			}
			case 4: // player quests
			{
				client.sendPacket(new GmViewQuestInfo(player));
				break;
			}
			case 5: // player inventory
			{
				client.sendPacket(new GMViewItemList(player));
				client.sendPacket(new GMHennaInfo(player));
				break;
			}
			case 6: // player warehouse
			{
				// GM warehouse view to be implemented
				if (player != null)
				{
					client.sendPacket(new GMViewWarehouseWithdrawList(player));
					// clan warehouse
				}
				else
				{
					client.sendPacket(new GMViewWarehouseWithdrawList(clan));
				}
				break;
			}
		}
	}
}
