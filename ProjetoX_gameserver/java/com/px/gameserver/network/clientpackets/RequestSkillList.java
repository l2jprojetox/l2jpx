package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;

public final class RequestSkillList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player cha = getClient().getPlayer();
		if (cha == null)
			return;
		
		cha.sendSkillList();
	}
}