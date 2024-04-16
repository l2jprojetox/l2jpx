package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.pledge.ClanMember;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
	private String _memberName;
	private int _powerGrade;
	
	@Override
	protected void readImpl()
	{
		_memberName = readS();
		_powerGrade = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		final ClanMember member = clan.getClanMember(_memberName);
		if (member == null)
			return;
		
		if (member.getPledgeType() == Clan.SUBUNIT_ACADEMY)
			return;
		
		member.setPowerGrade(_powerGrade);
		
		clan.broadcastToMembers(new PledgeShowMemberListUpdate(member), SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_PRIVILEGE_CHANGED_TO_S2).addString(member.getName()).addNumber(_powerGrade));
	}
}