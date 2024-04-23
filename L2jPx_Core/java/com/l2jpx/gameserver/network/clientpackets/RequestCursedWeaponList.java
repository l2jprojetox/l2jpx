package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.CursedWeaponManager;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.ExCursedWeaponList;

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