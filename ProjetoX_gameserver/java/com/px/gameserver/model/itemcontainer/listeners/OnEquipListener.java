package com.px.gameserver.model.itemcontainer.listeners;

import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener
{
	public void onEquip(int slot, ItemInstance item, Playable actor);
	
	public void onUnequip(int slot, ItemInstance item, Playable actor);
}