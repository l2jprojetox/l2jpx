package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.FestivalOfDarknessManager;
import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.ActionFailed;
import com.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

import l2jbrasil.AutoFarm.AutofarmPlayerRoutine;

public final class Logout extends L2GameClientPacket
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
		
		if (player.getActiveEnchantItem() != null || player.isLocked())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_RESTART))
		{
			player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isFestivalParticipant() && FestivalOfDarknessManager.getInstance().isFestivalInitialized())
		{
			player.sendPacket(SystemMessageId.NO_LOGOUT_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isAutoFarm())
		{
			if(AutofarmPlayerRoutine.isIpAllowed(player.getIP())) 
			{
				AutofarmPlayerRoutine.removeIpEntry(player.getObjectId());
			}
		}
		
		player.removeFromBossZone();
		player.logout(true);
	}
}