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

import org.l2jpx.gameserver.instancemanager.CastleManager;
import org.l2jpx.gameserver.instancemanager.ClanEntryManager;
import org.l2jpx.gameserver.instancemanager.FortManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.siege.Castle;
import org.l2jpx.gameserver.model.siege.Fort;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExPledgeCount;
import org.l2jpx.gameserver.network.serverpackets.JoinPledge;
import org.l2jpx.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2jpx.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2jpx.gameserver.network.serverpackets.PledgeShowMemberListAll;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingUserAccept extends ClientPacket
{
	private boolean _acceptRequest;
	private int _playerId;
	
	@Override
	protected void readImpl()
	{
		_acceptRequest = readInt() == 1;
		_playerId = readInt();
		readInt(); // Clan Id.
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		final int clanId = clan.getId();
		if (_acceptRequest)
		{
			final Player target = World.getInstance().getPlayer(_playerId);
			if (target != null)
			{
				final long currentTime = System.currentTimeMillis();
				if ((target.getClan() == null) && (target.getClanJoinExpiryTime() < currentTime))
				{
					target.sendPacket(new JoinPledge(clan.getId()));
					
					// player.setPowerGrade(9); // academy
					target.setPowerGrade(5); // New member starts at 5, not confirmed.
					clan.addClanMember(target);
					target.setClanPrivileges(target.getClan().getRankPrivs(target.getPowerGrade()));
					target.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
					
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
					sm.addString(target.getName());
					clan.broadcastToOnlineMembers(sm);
					
					if (clan.getCastleId() > 0)
					{
						final Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
						if (castle != null)
						{
							castle.giveResidentialSkills(target);
						}
					}
					if (clan.getFortId() > 0)
					{
						final Fort fort = FortManager.getInstance().getFortByOwner(clan);
						if (fort != null)
						{
							fort.giveResidentialSkills(target);
						}
					}
					target.sendSkillList();
					
					clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(target), target);
					clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
					clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
					
					// This activates the clan tab on the new member.
					PledgeShowMemberListAll.sendAllTo(target);
					target.setClanJoinExpiryTime(0);
					target.broadcastUserInfo();
					
					ClanEntryManager.getInstance().removePlayerApplication(clanId, _playerId);
				}
				else if (target.getClanJoinExpiryTime() > currentTime)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN);
					sm.addString(target.getName());
					player.sendPacket(sm);
				}
			}
		}
		else
		{
			ClanEntryManager.getInstance().removePlayerApplication(clanId, _playerId);
		}
	}
}
