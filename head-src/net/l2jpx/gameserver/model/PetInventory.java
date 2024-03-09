package net.l2jpx.gameserver.model;

import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance.ItemLocation;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;

public class PetInventory extends Inventory
{
	private final L2PetInstance owner;
	
	public PetInventory(final L2PetInstance owner)
	{
		this.owner = owner;
	}
	
	@Override
	public L2PetInstance getOwner()
	{
		return owner;
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
}
