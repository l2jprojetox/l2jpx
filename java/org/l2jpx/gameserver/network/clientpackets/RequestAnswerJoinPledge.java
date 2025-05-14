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
import org.l2jpx.gameserver.instancemanager.FortManager;
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
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerJoinPledge extends ClientPacket
{
	private int _answer;
	
	@Override
	protected void readImpl()
	{
		_answer = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Player requestor = player.getRequest().getPartner();
		if (requestor == null)
		{
			return;
		}
		
		if (_answer == 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED);
			sm.addString(requestor.getName());
			player.sendPacket(sm);
			sm = new SystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
			sm.addString(player.getName());
			requestor.sendPacket(sm);
		}
		else
		{
			if (!((requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge) || (requestor.getRequest().getRequestPacket() instanceof RequestClanAskJoinByName)))
			{
				return; // hax
			}
			
			final int pledgeType;
			if (requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge)
			{
				pledgeType = ((RequestJoinPledge) requestor.getRequest().getRequestPacket()).getPledgeType();
			}
			else
			{
				pledgeType = ((RequestClanAskJoinByName) requestor.getRequest().getRequestPacket()).getPledgeType();
			}
			
			final Clan clan = requestor.getClan();
			// we must double check this cause during response time conditions can be changed, i.e. another player could join clan
			if (clan.checkClanJoinCondition(requestor, player, pledgeType))
			{
				if (player.getClan() != null)
				{
					return;
				}
				
				player.sendPacket(new JoinPledge(requestor.getClanId()));
				player.setPledgeType(pledgeType);
				if (pledgeType == Clan.SUBUNIT_ACADEMY)
				{
					player.setPowerGrade(9); // academy
					player.setLvlJoinedAcademy(player.getLevel());
				}
				else
				{
					player.setPowerGrade(5); // new member starts at 5, not confirmed
				}
				
				clan.addClanMember(player);
				player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
				player.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
				
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
				sm.addString(player.getName());
				clan.broadcastToOnlineMembers(sm);
				
				if (clan.getCastleId() > 0)
				{
					final Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
					if (castle != null)
					{
						castle.giveResidentialSkills(player);
					}
				}
				if (clan.getFortId() > 0)
				{
					final Fort fort = FortManager.getInstance().getFortByOwner(clan);
					if (fort != null)
					{
						fort.giveResidentialSkills(player);
					}
				}
				player.sendSkillList();
				
				clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(player), player);
				clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
				clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
				
				// this activates the clan tab on the new member
				PledgeShowMemberListAll.sendAllTo(player);
				player.setClanJoinExpiryTime(0);
				player.broadcastUserInfo();
			}
		}
		
		player.getRequest().onRequestResponse();
	}
}
