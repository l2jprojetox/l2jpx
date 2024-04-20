package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.holder.IntIntHolder;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.model.item.kind.Weapon;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.MagicSkillUse;

public class BlessedSpiritShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final Weapon weaponItem = player.getActiveWeaponItem();
		
		// Check if bss can be used
		if (weaponInst == null || weaponItem == null || weaponItem.getSpiritShotCount() == 0)
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.CANNOT_USE_SPIRITSHOTS);
			
			return;
		}
		
		// Check if bss is already active (it can be charged over SpiritShot)
		if (player.isChargedShot(ShotType.BLESSED_SPIRITSHOT))
			return;
		
		// Check for correct grade.
		if (weaponItem.getCrystalType() != item.getItem().getCrystalType())
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
			
			return;
		}
		
		// Consume bss if player has enough of them
		if (!player.destroyItemWithoutTrace(item.getObjectId(), weaponItem.getSpiritShotCount()))
		{
			if (!player.disableAutoShot(item.getItemId()))
				player.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS);
			
			return;
		}
		
		final IntIntHolder[] skills = item.getItem().getSkills();
		
		player.sendPacket(SystemMessageId.ENABLED_SPIRITSHOT);
		player.setChargedShot(ShotType.BLESSED_SPIRITSHOT, true);
		player.broadcastPacketInRadius(new MagicSkillUse(player, player, skills[0].getId(), 1, 0, 0), 600);
	}
}