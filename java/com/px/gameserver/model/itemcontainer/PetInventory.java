package com.px.gameserver.model.itemcontainer;

import com.px.gameserver.enums.items.EtcItemType;
import com.px.gameserver.enums.items.ItemLocation;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.item.instance.ItemInstance;

public class PetInventory extends Inventory
{
	public PetInventory(Pet owner)
	{
		super(owner);
	}
	
	@Override
	public Pet getOwner()
	{
		return (Pet) _owner;
	}
	
	@Override
	public int getOwnerId()
	{
		int id;
		try
		{
			id = getOwner().getOwner().getObjectId();
		}
		catch (NullPointerException e)
		{
			return 0;
		}
		return id;
	}
	
	@Override
	public boolean updateWeight()
	{
		if (!super.updateWeight())
			return false;
		
		getOwner().updateAndBroadcastStatus(1);
		getOwner().sendPetInfosToOwner();
		return true;
	}
	
	public boolean validateCapacity(ItemInstance item)
	{
		int slots = 0;
		
		if (!(item.isStackable() && getItemByItemId(item.getItemId()) != null) && item.getItemType() != EtcItemType.HERB)
			slots++;
		
		return validateCapacity(slots);
	}
	
	@Override
	public boolean validateCapacity(int slotCount)
	{
		if (slotCount == 0)
			return true;
		
		return _items.size() + slotCount <= getOwner().getInventoryLimit();
	}
	
	public boolean validateWeight(ItemInstance item, int count)
	{
		return validateWeight(count * item.getItem().getWeight());
	}
	
	@Override
	public boolean validateWeight(int weight)
	{
		return _totalWeight + weight <= _owner.getWeightLimit();
	}
	
	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.PET;
	}
	
	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PET_EQUIP;
	}
	
	@Override
	public void deleteMe()
	{
		final Player petOwner = getOwner().getOwner();
		if (petOwner != null)
		{
			for (ItemInstance item : _items)
			{
				if (petOwner.getInventory().validateCapacity(1))
					getOwner().transferItem("return", item.getObjectId(), item.getCount(), petOwner);
				else
				{
					final ItemInstance droppedItem = dropItem("drop", item.getObjectId(), item.getCount(), petOwner, _owner);
					droppedItem.dropMe(getOwner(), 70);
				}
			}
		}
		
		_items.clear();
	}
}