package com.l2jpx.gameserver.model.itemcontainer.listeners;

import com.l2jpx.gameserver.enums.Paperdoll;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener
{
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor);
	
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor);
}