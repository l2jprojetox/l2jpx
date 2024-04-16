package com.px.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import com.px.commons.logging.CLogger;
import com.px.commons.pool.ConnectionPool;

import com.px.Config;
import com.px.gameserver.data.xml.ItemData;
import com.px.gameserver.enums.items.ItemLocation;
import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.taskmanager.ItemInstanceTaskManager;

public abstract class ItemContainer
{
	protected static final CLogger LOGGER = new CLogger(ItemContainer.class.getName());
	
	private static final String RESTORE_ITEMS = "SELECT * FROM items WHERE owner_id=? AND (loc=?)";
	
	protected final Set<ItemInstance> _items = new ConcurrentSkipListSet<>();
	
	protected ItemContainer()
	{
	}
	
	protected abstract Playable getOwner();
	
	protected abstract ItemLocation getBaseLocation();
	
	public String getName()
	{
		return "ItemContainer";
	}
	
	/**
	 * @return the owner objectId of the inventory.
	 */
	public int getOwnerId()
	{
		return (getOwner() == null) ? 0 : getOwner().getObjectId();
	}
	
	/**
	 * @return the quantity of items in the inventory.
	 */
	public int getSize()
	{
		return _items.size();
	}
	
	/**
	 * @return the list of items in inventory.
	 */
	public Set<ItemInstance> getItems()
	{
		return _items;
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return True if the item id exists in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasItems(int itemId)
	{
		return getItemByItemId(itemId) != null;
	}
	
	/**
	 * @param itemIds : A list of item IDs to check.
	 * @return True if all item ids exist in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasItems(int... itemIds)
	{
		for (int itemId : itemIds)
		{
			if (getItemByItemId(itemId) == null)
				return false;
		}
		return true;
	}
	
	/**
	 * @param itemIds : A list of item IDs to check.
	 * @return True if at least one item id exists in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasAtLeastOneItem(int... itemIds)
	{
		for (int itemId : itemIds)
		{
			if (getItemByItemId(itemId) != null)
				return true;
		}
		return false;
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return A {@link List} of {@link ItemInstance}s by given item ID, or an empty {@link List} if none are found.
	 */
	public List<ItemInstance> getItemsByItemId(int itemId)
	{
		return _items.stream().filter(i -> i.getItemId() == itemId).collect(Collectors.toList());
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return An {@link ItemInstance} using its item ID, or null if not found in this {@link ItemContainer}.
	 */
	public ItemInstance getItemByItemId(int itemId)
	{
		return _items.stream().filter(i -> i.getItemId() == itemId).findFirst().orElse(null);
	}
	
	/**
	 * @param objectId : The object ID to check.
	 * @return An {@link ItemInstance} using its object ID, or null if not found in this {@link ItemContainer}.
	 */
	public ItemInstance getItemByObjectId(int objectId)
	{
		return _items.stream().filter(i -> i.getObjectId() == objectId).findFirst().orElse(null);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return The quantity of items hold by this {@link ItemContainer} (item enchant level does not matter, including equipped items).
	 */
	public int getItemCount(int itemId)
	{
		return getItemCount(itemId, -1, true);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @param enchantLevel : The enchant level to match on (-1 for ANY enchant level).
	 * @return The quantity of items hold by this {@link ItemContainer} (including equipped items).
	 */
	public int getItemCount(int itemId, int enchantLevel)
	{
		return getItemCount(itemId, enchantLevel, true);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @param enchantLevel : The enchant level to match on (-1 for ANY enchant level).
	 * @param includeEquipped : Include equipped items.
	 * @return The quantity of items hold by this {@link ItemContainer}.
	 */
	public int getItemCount(int itemId, int enchantLevel, boolean includeEquipped)
	{
		int count = 0;
		
		for (ItemInstance item : _items)
		{
			if (item.getItemId() == itemId && (item.getEnchantLevel() == enchantLevel || enchantLevel < 0) && (includeEquipped || !item.isEquipped()))
			{
				if (item.isStackable())
					return item.getCount();
				
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Adds item to inventory
	 * @param process : String identifier of process triggering this action.
	 * @param item : ItemInstance to add.
	 * @param actor : The player requesting the item addition.
	 * @param reference : The WorldObject referencing current action (like NPC selling item or previous item in transformation,...)
	 * @return the ItemInstance corresponding to the new or updated item.
	 */
	public ItemInstance addItem(String process, ItemInstance item, Playable actor, WorldObject reference)
	{
		// If existing stackable item is found.
		final ItemInstance oldItem = getItemByItemId(item.getItemId());
		if (oldItem != null && oldItem.isStackable())
		{
			// Add to current ItemInstance the requested item quantity.
			oldItem.changeCount(process, item.getCount(), actor, reference);
			
			// Destroy the item.
			item.destroyMe(process, actor, reference);
			
			// Return the existing ItemInstance.
			return oldItem;
		}
		
		// If item hasn't be found in inventory, set ownership and location.
		item.setOwnerId(process, getOwnerId(), actor, reference);
		item.setLocation(getBaseLocation());
		
		// Add item in inventory.
		addItem(item);
		
		return item;
	}
	
	/**
	 * Adds an item to inventory.
	 * @param process : String identifier of process triggering this action.
	 * @param itemId : The itemId of the ItemInstance to add.
	 * @param count : The quantity of items to add.
	 * @param actor : The player requesting the item addition.
	 * @param reference : The WorldObject referencing current action (like NPC selling item or previous item in transformation,...)
	 * @return the ItemInstance corresponding to the new or updated item.
	 */
	public ItemInstance addItem(String process, int itemId, int count, Playable actor, WorldObject reference)
	{
		ItemInstance item = getItemByItemId(itemId);
		
		// If existing stackable item is found, add to current ItemInstance the requested item quantity.
		if (item != null && item.isStackable())
			item.changeCount(process, count, actor, reference);
		// If item hasn't be found in inventory, create new one
		else
		{
			final Item template = ItemData.getInstance().getTemplate(itemId);
			if (template == null)
				return null;
			
			for (int i = 0; i < count; i++)
			{
				item = ItemInstance.create(itemId, template.isStackable() ? count : 1, actor, reference);
				item.setOwnerId(getOwnerId());
				item.setLocation(getBaseLocation());
				
				// Add item in inventory
				addItem(item);
				
				// If stackable, end loop as entire count is included in 1 instance of item
				if (template.isStackable() || !Config.MULTIPLE_ITEM_DROP)
					break;
			}
		}
		return item;
	}
	
	public ItemInstance transferItem(String process, int objectId, int count, ItemContainer target, Player actor, WorldObject reference)
	{
		if (target == null)
			return null;
		
		ItemInstance sourceItem = getItemByObjectId(objectId);
		if (sourceItem == null)
			return null;
		
		ItemInstance targetItem = sourceItem.isStackable() ? target.getItemByItemId(sourceItem.getItemId()) : null;
		
		synchronized (sourceItem)
		{
			// check if this item still present in this container
			if (getItemByObjectId(objectId) != sourceItem)
				return null;
			
			// Check if requested quantity is available
			if (count > sourceItem.getCount())
				count = sourceItem.getCount();
			
			// If possible, move entire item object
			if (sourceItem.getCount() == count && targetItem == null)
			{
				removeItem(sourceItem);
				
				target.addItem(process, sourceItem, actor, reference);
				targetItem = sourceItem;
			}
			else
			{
				// If possible, only update counts
				if (sourceItem.getCount() > count)
					sourceItem.changeCount(process, -count, actor, reference);
				else
				// Otherwise destroy old item
				{
					removeItem(sourceItem);
					
					sourceItem.destroyMe(process, actor, reference);
				}
				
				// If possible, only update counts
				if (targetItem != null)
					targetItem.changeCount(process, count, actor, reference);
				// Otherwise add new item
				else
					targetItem = target.addItem(process, sourceItem.getItemId(), count, actor, reference);
			}
			
			if (sourceItem.isAugmented())
				sourceItem.getAugmentation().removeBonus(actor);
		}
		return targetItem;
	}
	
	public ItemInstance transferItem(String process, int objectId, int amount, Playable target, WorldObject reference)
	{
		if (target == null)
			return null;
		
		ItemInstance sourceitem = getItemByObjectId(objectId);
		if (sourceitem == null)
			return null;
		
		Inventory inventory = target.getInventory();
		ItemInstance targetitem = sourceitem.isStackable() ? inventory.getItemByItemId(sourceitem.getItemId()) : null;
		
		synchronized (sourceitem)
		{
			// check if this item still present in this container
			if (getItemByObjectId(objectId) != sourceitem)
				return null;
			
			// Check if requested quantity is available
			if (amount > sourceitem.getCount())
				amount = sourceitem.getCount();
			
			// If possible, move entire item object
			if (sourceitem.getCount() == amount && targetitem == null)
			{
				removeItem(sourceitem);
				
				inventory.addItem(process, sourceitem, target, reference);
				targetitem = sourceitem;
			}
			else
			{
				// If possible, only update counts
				if (sourceitem.getCount() > amount)
					sourceitem.changeCount(process, -amount, getOwner(), reference);
				// Otherwise destroy old item
				else
				{
					removeItem(sourceitem);
					
					sourceitem.destroyMe(process, getOwner(), reference);
				}
				
				// If possible, only update counts
				if (targetitem != null)
					targetitem.changeCount(process, amount, target, reference);
				// Otherwise add new item
				else
					targetitem = inventory.addItem(process, sourceitem.getItemId(), amount, target, reference);
			}
		}
		return targetitem;
	}
	
	/**
	 * Destroy item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be destroyed
	 * @param actor : Player Player requesting the item destroy
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, ItemInstance item, Player actor, WorldObject reference)
	{
		return destroyItem(process, item, item.getCount(), actor, reference);
	}
	
	/**
	 * Destroy item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be destroyed
	 * @param count
	 * @param actor : Player Player requesting the item destroy
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, ItemInstance item, int count, Player actor, WorldObject reference)
	{
		synchronized (item)
		{
			// Adjust item quantity
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				
				return item;
			}
			
			if (item.getCount() < count)
				return null;
			
			if (!removeItem(item))
				return null;
			
			item.destroyMe(process, actor, reference);
		}
		return item;
	}
	
	/**
	 * Destroy item from inventory by using its <B>objectID</B> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param actor : Player Player requesting the item destroy
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, int objectId, int count, Player actor, WorldObject reference)
	{
		ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
			return null;
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param actor : Player Player requesting the item destroy
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItemByItemId(String process, int itemId, int count, Player actor, WorldObject reference)
	{
		ItemInstance item = getItemByItemId(itemId);
		if (item == null)
			return null;
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Destroy all items from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param actor : Player Player requesting the item destroy
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void destroyAllItems(String process, Player actor, WorldObject reference)
	{
		for (ItemInstance item : _items)
			destroyItem(process, item, actor, reference);
	}
	
	/**
	 * @return the amount of adena (itemId 57)
	 */
	public int getAdena()
	{
		for (ItemInstance item : _items)
		{
			if (item.getItemId() == 57)
				return item.getCount();
		}
		return 0;
	}
	
	/**
	 * Add the {@link ItemInstance} set as parameter to inventory.
	 * @param item : The {@link ItemInstance} to add.
	 */
	protected void addItem(ItemInstance item)
	{
		item.actualizeTime();
		
		_items.add(item);
	}
	
	/**
	 * @param item : The {@link ItemInstance} to remove.
	 * @return True if the {@link ItemInstance} set as parameter was successfully removed, or false otherwise.
	 */
	protected boolean removeItem(ItemInstance item)
	{
		return _items.remove(item);
	}
	
	/**
	 * Delete this {@link ItemContainer}, aswell as contained {@link ItemInstance}s, from {@link World}.<br>
	 * <br>
	 * Before deletion, {@link ItemInstance}s are saved in database.
	 */
	public void deleteMe()
	{
		if (getOwner() != null)
		{
			// Delete all related items from World.
			World.getInstance().removeObjects(_items);
			
			// Remove all ItemContainer items from ItemInstanceTaskManager to avoid them to be gathered and processed automatically by the delayed task.
			ItemInstanceTaskManager.getInstance().removeItems(_items);
			
			// Instantly save all ItemContainer items current state, _items is cleared from the method.
			ItemInstanceTaskManager.getInstance().updateItems(_items);
		}
		// Clear items.
		else
			_items.clear();
	}
	
	/**
	 * Get back items in container from database
	 */
	public void restore()
	{
		final Player owner = (getOwner() == null) ? null : getOwner().getActingPlayer();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_ITEMS))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					// Restore the item.
					final ItemInstance item = ItemInstance.restoreFromDb(rs);
					if (item == null)
						continue;
					
					// ItemInstanceTaskManager didn't yet process the item, which means the item wasn't anymore part of this ItemContainer - don't reload it.
					if (ItemInstanceTaskManager.getInstance().contains(item))
						continue;
					
					// Add the item to world objects list.
					World.getInstance().addObject(item);
					
					// If stackable item is found in inventory just add to current quantity
					if (item.isStackable() && getItemByItemId(item.getItemId()) != null)
						addItem("Restore", item, owner, null);
					else
						addItem(item);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore container for {}.", e, getOwnerId());
		}
	}
	
	public boolean validateCapacity(int slotCount)
	{
		return true;
	}
	
	public boolean validateWeight(int weight)
	{
		return true;
	}
}