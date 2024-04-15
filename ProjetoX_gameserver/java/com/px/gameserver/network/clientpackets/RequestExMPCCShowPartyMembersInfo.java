package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

/**
 * Format:(ch) d
 * @author chris_00
 */
public final class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int _partyLeaderId;
	
	@Override
	protected void readImpl()
	{
		_partyLeaderId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		Player player = World.getInstance().getPlayer(_partyLeaderId);
		if (player != null && player.isInParty())
			activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
	}
}