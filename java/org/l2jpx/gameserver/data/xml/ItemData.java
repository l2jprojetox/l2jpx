/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.data.xml;

import static org.l2jpx.gameserver.model.itemcontainer.Inventory.ADENA_ID;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.StringUtil;
import org.l2jpx.commons.util.file.filter.XMLFilter;
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.instancemanager.IdManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Attackable;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.instance.EventMonster;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.item.OnItemCreate;
import org.l2jpx.gameserver.model.item.Armor;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.util.DocumentItem;
import org.l2jpx.gameserver.util.GMAudit;

/**
 * This class serves as a container for all item templates in the game.
 */
public class ItemData
{
	private static final Logger LOGGER = Logger.getLogger(ItemData.class.getName());
	private static final Logger LOGGER_ITEMS = Logger.getLogger("item");
	
	private ItemTemplate[] _allTemplates;
	private final Map<Integer, EtcItem> _etcItems = new HashMap<>();
	private final Map<Integer, Armor> _armors = new HashMap<>();
	private final Map<Integer, Weapon> _weapons = new HashMap<>();
	private final List<File> _itemFiles = new ArrayList<>();
	
	public static final Map<String, Integer> SLOTS = new HashMap<>();
	static
	{
		SLOTS.put("shirt", ItemTemplate.SLOT_UNDERWEAR);
		SLOTS.put("lbracelet", ItemTemplate.SLOT_L_BRACELET);
		SLOTS.put("rbracelet", ItemTemplate.SLOT_R_BRACELET);
		SLOTS.put("talisman", ItemTemplate.SLOT_DECO);
		SLOTS.put("chest", ItemTemplate.SLOT_CHEST);
		SLOTS.put("fullarmor", ItemTemplate.SLOT_FULL_ARMOR);
		SLOTS.put("head", ItemTemplate.SLOT_HEAD);
		SLOTS.put("hair", ItemTemplate.SLOT_HAIR);
		SLOTS.put("hairall", ItemTemplate.SLOT_HAIRALL);
		SLOTS.put("underwear", ItemTemplate.SLOT_UNDERWEAR);
		SLOTS.put("back", ItemTemplate.SLOT_BACK);
		SLOTS.put("neck", ItemTemplate.SLOT_NECK);
		SLOTS.put("legs", ItemTemplate.SLOT_LEGS);
		SLOTS.put("feet", ItemTemplate.SLOT_FEET);
		SLOTS.put("gloves", ItemTemplate.SLOT_GLOVES);
		SLOTS.put("chest,legs", ItemTemplate.SLOT_CHEST | ItemTemplate.SLOT_LEGS);
		SLOTS.put("belt", ItemTemplate.SLOT_BELT);
		SLOTS.put("rhand", ItemTemplate.SLOT_R_HAND);
		SLOTS.put("lhand", ItemTemplate.SLOT_L_HAND);
		SLOTS.put("lrhand", ItemTemplate.SLOT_LR_HAND);
		SLOTS.put("rear;lear", ItemTemplate.SLOT_R_EAR | ItemTemplate.SLOT_L_EAR);
		SLOTS.put("rfinger;lfinger", ItemTemplate.SLOT_R_FINGER | ItemTemplate.SLOT_L_FINGER);
		SLOTS.put("wolf", ItemTemplate.SLOT_WOLF);
		SLOTS.put("greatwolf", ItemTemplate.SLOT_GREATWOLF);
		SLOTS.put("hatchling", ItemTemplate.SLOT_HATCHLING);
		SLOTS.put("strider", ItemTemplate.SLOT_STRIDER);
		SLOTS.put("babypet", ItemTemplate.SLOT_BABYPET);
		SLOTS.put("brooch", ItemTemplate.SLOT_BROOCH);
		SLOTS.put("brooch_jewel", ItemTemplate.SLOT_BROOCH_JEWEL);
		SLOTS.put("none", ItemTemplate.SLOT_NONE);
		
		// retail compatibility
		SLOTS.put("onepiece", ItemTemplate.SLOT_FULL_ARMOR);
		SLOTS.put("hair2", ItemTemplate.SLOT_HAIR2);
		SLOTS.put("dhair", ItemTemplate.SLOT_HAIRALL);
		SLOTS.put("alldress", ItemTemplate.SLOT_ALLDRESS);
		SLOTS.put("deco1", ItemTemplate.SLOT_DECO);
		SLOTS.put("waist", ItemTemplate.SLOT_BELT);
	}
	
	protected ItemData()
	{
		processDirectory("data/stats/items", _itemFiles);
		if (Config.CUSTOM_ITEMS_LOAD)
		{
			processDirectory("data/stats/items/custom", _itemFiles);
		}
		
		load();
	}
	
	private void processDirectory(String dirName, List<File> list)
	{
		final File dir = new File(Config.DATAPACK_ROOT, dirName);
		if (!dir.exists())
		{
			LOGGER.warning("Dir " + dir.getAbsolutePath() + " does not exist.");
			return;
		}
		final File[] files = dir.listFiles(new XMLFilter());
		for (File file : files)
		{
			list.add(file);
		}
	}
	
	private Collection<ItemTemplate> loadItems()
	{
		final Collection<ItemTemplate> list = ConcurrentHashMap.newKeySet();
		if (Config.THREADS_FOR_LOADING)
		{
			final Collection<ScheduledFuture<?>> jobs = ConcurrentHashMap.newKeySet();
			for (File file : _itemFiles)
			{
				jobs.add(ThreadPool.schedule(() ->
				{
					final DocumentItem document = new DocumentItem(file);
					document.parse();
					list.addAll(document.getItemList());
				}, 0));
			}
			while (!jobs.isEmpty())
			{
				for (ScheduledFuture<?> job : jobs)
				{
					if ((job == null) || job.isDone() || job.isCancelled())
					{
						jobs.remove(job);
					}
				}
			}
		}
		else
		{
			for (File file : _itemFiles)
			{
				final DocumentItem document = new DocumentItem(file);
				document.parse();
				list.addAll(document.getItemList());
			}
		}
		return list;
	}
	
	private void load()
	{
		int highest = 0;
		_armors.clear();
		_etcItems.clear();
		_weapons.clear();
		for (ItemTemplate item : loadItems())
		{
			if (highest < item.getId())
			{
				highest = item.getId();
			}
			if (item instanceof EtcItem)
			{
				_etcItems.put(item.getId(), (EtcItem) item);
			}
			else if (item instanceof Armor)
			{
				_armors.put(item.getId(), (Armor) item);
			}
			else
			{
				_weapons.put(item.getId(), (Weapon) item);
			}
		}
		buildFastLookupTable(highest);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _etcItems.size() + " Etc Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _armors.size() + " Armor Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _weapons.size() + " Weapon Items");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + (_etcItems.size() + _armors.size() + _weapons.size()) + " Items in total.");
	}
	
	/**
	 * Builds a variable in which all items are putting in in function of their ID.
	 * @param size
	 */
	private void buildFastLookupTable(int size)
	{
		// Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
		LOGGER.info(getClass().getSimpleName() + ": Highest item id used: " + size);
		_allTemplates = new ItemTemplate[size + 1];
		
		// Insert armor item in Fast Look Up Table
		for (Armor item : _armors.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert weapon item in Fast Look Up Table
		for (Weapon item : _weapons.values())
		{
			_allTemplates[item.getId()] = item;
		}
		
		// Insert etcItem item in Fast Look Up Table
		for (EtcItem item : _etcItems.values())
		{
			_allTemplates[item.getId()] = item;
		}
	}
	
	/**
	 * Returns the item corresponding to the item ID
	 * @param id : int designating the item
	 * @return Item
	 */
	public ItemTemplate getTemplate(int id)
	{
		if ((id >= _allTemplates.length) || (id < 0))
		{
			return null;
		}
		return _allTemplates[id];
	}
	
	/**
	 * Create the Item corresponding to the Item Identifier and quantitiy add logs the activity. <b><u>Actions</u>:</b>
	 * <li>Create and Init the Item corresponding to the Item Identifier and quantity</li>
	 * <li>Add the Item object to _allObjects of L2world</li>
	 * <li>Logs Item creation according to log settings</li><br>
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be created
	 * @param count : int Quantity of items to be created for stackable items
	 * @param actor : Creature requesting the item creation
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return Item corresponding to the new item
	 */
	public Item createItem(String process, int itemId, long count, Creature actor, Object reference)
	{
		// Create and Init the Item corresponding to the Item Identifier
		final Item item = new Item(IdManager.getInstance().getNextId(), itemId);
		if (process.equalsIgnoreCase("loot") && !Config.AUTO_LOOT_ITEM_IDS.contains(itemId))
		{
			ScheduledFuture<?> itemLootShedule;
			if ((reference instanceof Attackable) && ((Attackable) reference).isRaid()) // loot privilege for raids
			{
				final Attackable raid = (Attackable) reference;
				// if in CommandChannel and was killing a World/RaidBoss
				if ((raid.getFirstCommandChannelAttacked() != null) && !Config.AUTO_LOOT_RAIDS)
				{
					item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
					itemLootShedule = ThreadPool.schedule(new ResetOwner(item), Config.LOOT_RAIDS_PRIVILEGE_INTERVAL);
					item.setItemLootShedule(itemLootShedule);
				}
			}
			else if (!Config.AUTO_LOOT || ((reference instanceof EventMonster) && ((EventMonster) reference).eventDropOnGround()))
			{
				item.setOwnerId(actor.getObjectId());
				itemLootShedule = ThreadPool.schedule(new ResetOwner(item), 15000);
				item.setItemLootShedule(itemLootShedule);
			}
		}
		
		// Add the Item object to _allObjects of L2world
		World.getInstance().addObject(item);
		
		// Set Item parameters
		if (item.isStackable() && (count > 1))
		{
			item.setCount(count);
		}
		
		if ((Config.LOG_ITEMS && !process.equals("Reset") && ((!Config.LOG_ITEMS_SMALL_LOG) && (!Config.LOG_ITEMS_IDS_ONLY))) || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))) || (Config.LOG_ITEMS_IDS_ONLY && Config.LOG_ITEMS_IDS_LIST.contains(item.getId())))
		{
			if (item.getEnchantLevel() > 0)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("CREATE:");
				sb.append(process);
				sb.append(", item ");
				sb.append(item.getObjectId());
				sb.append(":+");
				sb.append(item.getEnchantLevel());
				sb.append(" ");
				sb.append(item.getTemplate().getName());
				sb.append("(");
				sb.append(item.getCount());
				sb.append("), ");
				sb.append(actor);
				sb.append(", ");
				sb.append(reference);
				LOGGER_ITEMS.info(sb.toString());
			}
			else
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("CREATE:");
				sb.append(process);
				sb.append(", item ");
				sb.append(item.getObjectId());
				sb.append(":");
				sb.append(item.getTemplate().getName());
				sb.append("(");
				sb.append(item.getCount());
				sb.append("), ");
				sb.append(actor);
				sb.append(", ");
				sb.append(reference);
				LOGGER_ITEMS.info(sb.toString());
			}
		}
		
		if ((actor != null) && actor.isGM() && Config.GMAUDIT)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(process);
			sb.append("(id: ");
			sb.append(itemId);
			sb.append(" count: ");
			sb.append(count);
			sb.append(" name: ");
			sb.append(item.getItemName());
			sb.append(" objId: ");
			sb.append(item.getObjectId());
			sb.append(")");
			
			final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
			
			String referenceName = "no-reference";
			if (reference instanceof WorldObject)
			{
				referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
			}
			else if (reference instanceof String)
			{
				referenceName = (String) reference;
			}
			
			GMAudit.auditGMAction(actor.toString(), sb.toString(), targetName, StringUtil.concat("Object referencing this action is: ", referenceName));
		}
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_CREATE, item.getTemplate()))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getTemplate());
		}
		
		return item;
	}
	
	public Item createItem(String process, int itemId, long count, Player actor)
	{
		return createItem(process, itemId, count, actor, null);
	}
	
	/**
	 * Destroys the Item.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Sets Item parameters to be unusable</li>
	 * <li>Removes the Item object to _allObjects of L2world</li>
	 * <li>Logs Item deletion according to log settings</li>
	 * </ul>
	 * @param process a string identifier of process triggering this action.
	 * @param item the item instance to be destroyed.
	 * @param actor the player requesting the item destroy.
	 * @param reference the object referencing current action like NPC selling item or previous item in transformation.
	 */
	public void destroyItem(String process, Item item, Player actor, Object reference)
	{
		synchronized (item)
		{
			final long old = item.getCount();
			item.setCount(0);
			item.setOwnerId(0);
			item.setItemLocation(ItemLocation.VOID);
			item.setLastChange(Item.REMOVED);
			
			World.getInstance().removeObject(item);
			IdManager.getInstance().releaseId(item.getObjectId());
			
			if ((Config.LOG_ITEMS && ((!Config.LOG_ITEMS_SMALL_LOG) && (!Config.LOG_ITEMS_IDS_ONLY))) || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == ADENA_ID))) || (Config.LOG_ITEMS_IDS_ONLY && Config.LOG_ITEMS_IDS_LIST.contains(item.getId())))
			{
				if (item.getEnchantLevel() > 0)
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("DELETE:");
					sb.append(process);
					sb.append(", item ");
					sb.append(item.getObjectId());
					sb.append(":+");
					sb.append(item.getEnchantLevel());
					sb.append(" ");
					sb.append(item.getTemplate().getName());
					sb.append("(");
					sb.append(item.getCount());
					sb.append("), PrevCount(");
					sb.append(old);
					sb.append("), ");
					sb.append(actor);
					sb.append(", ");
					sb.append(reference);
					LOGGER_ITEMS.info(sb.toString());
				}
				else
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("DELETE:");
					sb.append(process);
					sb.append(", item ");
					sb.append(item.getObjectId());
					sb.append(":");
					sb.append(item.getTemplate().getName());
					sb.append("(");
					sb.append(item.getCount());
					sb.append("), PrevCount(");
					sb.append(old);
					sb.append("), ");
					sb.append(actor);
					sb.append(", ");
					sb.append(reference);
					LOGGER_ITEMS.info(sb.toString());
				}
			}
			
			if ((actor != null) && actor.isGM() && Config.GMAUDIT)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append(process);
				sb.append("(id: ");
				sb.append(item.getId());
				sb.append(" count: ");
				sb.append(item.getCount());
				sb.append(" itemObjId: ");
				sb.append(item.getObjectId());
				sb.append(")");
				
				final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
				
				String referenceName = "no-reference";
				if (reference instanceof WorldObject)
				{
					referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
				}
				else if (reference instanceof String)
				{
					referenceName = (String) reference;
				}
				
				GMAudit.auditGMAction(actor.toString(), sb.toString(), targetName, StringUtil.concat("Object referencing this action is: ", referenceName));
			}
			
			// if it's a pet control item, delete the pet as well
			if (item.getTemplate().isPetItem())
			{
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
				{
					// Delete the pet in db
					statement.setInt(1, item.getObjectId());
					statement.execute();
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete pet objectid:", e);
				}
			}
		}
	}
	
	public void reload()
	{
		load();
		EnchantItemHPBonusData.getInstance().load();
	}
	
	public Set<Integer> getAllArmorsId()
	{
		return _armors.keySet();
	}
	
	public Collection<Armor> getAllArmors()
	{
		return _armors.values();
	}
	
	public Set<Integer> getAllWeaponsId()
	{
		return _weapons.keySet();
	}
	
	public Collection<Weapon> getAllWeapons()
	{
		return _weapons.values();
	}
	
	public Set<Integer> getAllEtcItemsId()
	{
		return _etcItems.keySet();
	}
	
	public Collection<EtcItem> getAllEtcItems()
	{
		return _etcItems.values();
	}
	
	public ItemTemplate[] getAllItems()
	{
		return _allTemplates;
	}
	
	public int getArraySize()
	{
		return _allTemplates.length;
	}
	
	protected static class ResetOwner implements Runnable
	{
		Item _item;
		
		public ResetOwner(Item item)
		{
			_item = item;
		}
		
		@Override
		public void run()
		{
			// Set owner id to 0 only when location is VOID.
			if (_item.getItemLocation() == ItemLocation.VOID)
			{
				_item.setOwnerId(0);
			}
			_item.setItemLootShedule(null);
		}
	}
	
	public static ItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemData INSTANCE = new ItemData();
	}
}
