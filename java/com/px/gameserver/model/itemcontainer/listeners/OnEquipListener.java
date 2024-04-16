package com.px.gameserver.model.itemcontainer.listeners;

import com.px.gameserver.enums.Paperdoll;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener
{
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor);
	
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor);
}