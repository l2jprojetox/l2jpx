package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.items.WeaponType;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.item.kind.Weapon;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.MagicSkillUse;

public class FishShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final Weapon weaponItem = player.getActiveWeaponItem();
		
		if (weaponInst == null || weaponItem.getItemType() != WeaponType.FISHINGROD)
			return;
		
		// Fishshot is already active
		if (player.isChargedShot(ShotType.FISH_SOULSHOT))
			return;
		
		// Wrong grade of soulshot for that fishing pole.
		if (weaponItem.getCrystalType() != item.getItem().getCrystalType())
		{
			player.sendPacket(SystemMessageId.WRONG_FISHINGSHOT_GRADE);
			return;
		}
		
		if (!player.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
			return;
		}
		
		final IntIntHolder[] skills = item.getItem().getSkills();
		
		player.setChargedShot(ShotType.FISH_SOULSHOT, true);
		player.broadcastPacket(new MagicSkillUse(player, skills[0].getId(), 1, 0, 0));
	}
}