package com.px.gameserver.model.itemcontainer.listeners;

import java.util.ArrayList;
import java.util.List;

import com.px.gameserver.enums.Paperdoll;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.itemcontainer.Inventory;

/**
 * Recorder of alterations in a given {@link Inventory}.
 */
public class ChangeRecorderListener implements OnEquipListener
{
	private final List<ItemInstance> _changed = new ArrayList<>();
	
	public ChangeRecorderListener(Inventory inventory)
	{
		inventory.addPaperdollListener(this);
	}
	
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!_changed.contains(item))
			_changed.add(item);
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!_changed.contains(item))
			_changed.add(item);
	}
	
	/**
	 * @return The array of alterated {@link ItemInstance}.
	 */
	public ItemInstance[] getChangedItems()
	{
		return _changed.toArray(new ItemInstance[_changed.size()]);
	}
}