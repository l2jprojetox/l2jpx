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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.ClanTable;
import org.l2jpx.gameserver.enums.ClanWarState;
import org.l2jpx.gameserver.enums.UserInfoType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.ClanMember;
import org.l2jpx.gameserver.model.clan.ClanPrivilege;
import org.l2jpx.gameserver.model.clan.ClanWar;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.PledgeReceiveWarList;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class RequestStartPledgeWar extends ClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		_pledgeName = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Clan clanDeclaringWar = player.getClan();
		if (clanDeclaringWar == null)
		{
			return;
		}
		
		if ((clanDeclaringWar.getLevel() < 3) || (clanDeclaringWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendPacket(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR))
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (clanDeclaringWar.getWarCount() >= 30)
		{
			player.sendPacket(SystemMessageId.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CAN_T_BE_MADE_AT_THE_SAME_TIME);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Clan clanDeclaredWar = ClanTable.getInstance().getClanByName(_pledgeName);
		if (clanDeclaredWar == null)
		{
			player.sendPacket(SystemMessageId.A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (clanDeclaredWar == clanDeclaringWar)
		{
			player.sendPacket(SystemMessageId.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if ((clanDeclaringWar.getAllyId() == clanDeclaredWar.getAllyId()) && (clanDeclaringWar.getAllyId() != 0))
		{
			player.sendPacket(SystemMessageId.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CAN_T_BE_MADE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if ((clanDeclaredWar.getLevel() < 3) || (clanDeclaredWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendPacket(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (clanDeclaredWar.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.A_CLAN_WAR_CAN_NOT_BE_DECLARED_AGAINST_A_CLAN_THAT_IS_BEING_DISSOLVED);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final ClanWar clanWar = clanDeclaringWar.getWarWith(clanDeclaredWar.getId());
		if (clanWar != null)
		{
			if (clanWar.getClanWarState(clanDeclaringWar) == ClanWarState.WIN)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_CAN_T_DECLARE_A_WAR_BECAUSE_THE_21_DAY_PERIOD_HASN_T_PASSED_AFTER_A_DEFEAT_DECLARATION_WITH_THE_S1_CLAN);
				sm.addString(clanDeclaredWar.getName());
				player.sendPacket(sm);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			if (clanWar.getState() == ClanWarState.MUTUAL)
			{
				player.sendMessage("You have already been at war with " + clanDeclaredWar.getName() + ".");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION)
			{
				clanWar.mutualClanWarAccepted(clanDeclaredWar, clanDeclaringWar);
				ClanTable.getInstance().storeClanWars(clanWar);
				for (ClanMember member : clanDeclaringWar.getMembers())
				{
					if ((member != null) && member.isOnline())
					{
						member.getPlayer().broadcastUserInfo(UserInfoType.CLAN);
					}
				}
				for (ClanMember member : clanDeclaredWar.getMembers())
				{
					if ((member != null) && member.isOnline())
					{
						member.getPlayer().broadcastUserInfo(UserInfoType.CLAN);
					}
				}
				
				player.sendPacket(new PledgeReceiveWarList(player.getClan(), 0));
				return;
			}
		}
		
		final ClanWar newClanWar = new ClanWar(clanDeclaringWar, clanDeclaredWar);
		ClanTable.getInstance().storeClanWars(newClanWar);
		
		for (ClanMember member : clanDeclaringWar.getMembers())
		{
			if ((member != null) && member.isOnline())
			{
				member.getPlayer().broadcastUserInfo(UserInfoType.CLAN);
			}
		}
		for (ClanMember member : clanDeclaredWar.getMembers())
		{
			if ((member != null) && member.isOnline())
			{
				member.getPlayer().broadcastUserInfo(UserInfoType.CLAN);
			}
		}
		player.sendPacket(new PledgeReceiveWarList(player.getClan(), 0));
	}
}
