package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.manager.CursedWeaponManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.ExCursedWeaponList;

public class RequestCursedWeaponList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.sendPacket(new ExCursedWeaponList(CursedWeaponManager.getInstance().getCursedWeaponsIds()));
	}
}