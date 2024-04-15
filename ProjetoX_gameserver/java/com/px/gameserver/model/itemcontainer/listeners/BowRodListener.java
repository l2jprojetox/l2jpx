package com.px.gameserver.model.itemcontainer.listeners;

import com.px.gameserver.enums.items.WeaponType;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.itemcontainer.Inventory;

public class BowRodListener implements OnEquipListener
{
	private static BowRodListener instance = new BowRodListener();
	
	public static BowRodListener getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if (slot != Inventory.PAPERDOLL_RHAND)
			return;
		
		if (item.getItemType() == WeaponType.BOW)
		{
			final ItemInstance arrow = actor.getInventory().findArrowForBow(item.getItem());
			if (arrow != null)
				actor.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, arrow);
		}
	}
	
	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if (slot != Inventory.PAPERDOLL_RHAND)
			return;
		
		if (item.getItemType() == WeaponType.BOW)
		{
			final ItemInstance arrow = actor.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if (arrow != null)
				actor.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
		}
		else if (item.getItemType() == WeaponType.FISHINGROD)
		{
			final ItemInstance lure = actor.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if (lure != null)
				actor.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
		}
	}
}