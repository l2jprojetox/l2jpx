package com.px.gameserver.model.itemcontainer.listeners;

import com.px.gameserver.enums.Paperdoll;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.item.instance.ItemInstance;

public class StatsListener implements OnEquipListener
{
	private static StatsListener instance = new StatsListener();
	
	public static StatsListener getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable playable)
	{
		playable.addStatFuncs(item.getStatFuncs(playable));
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable playable)
	{
		playable.removeStatsByOwner(item);
	}
}