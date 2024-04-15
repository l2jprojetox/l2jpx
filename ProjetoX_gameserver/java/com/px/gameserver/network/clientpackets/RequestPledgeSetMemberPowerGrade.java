package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.pledge.ClanMember;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) Sd
 * @author -Wooden-
 */
public final class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	private int _powerGrade;
	private String _member;
	
	@Override
	protected void readImpl()
	{
		_member = readS();
		_powerGrade = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Clan clan = activeChar.getClan();
		if (clan == null)
			return;
		
		final ClanMember member = clan.getClanMember(_member);
		if (member == null)
			return;
		
		if (member.getPledgeType() == Clan.SUBUNIT_ACADEMY)
			return;
		
		member.setPowerGrade(_powerGrade);
		clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(member), SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_PRIVILEGE_CHANGED_TO_S2).addString(member.getName()).addNumber(_powerGrade));
	}
}