package com.px.gameserver.model.item.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.px.commons.pool.ConnectionPool;
import com.px.commons.pool.ThreadPool;

import com.px.Config;
import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.xml.ItemData;
import com.px.gameserver.enums.items.EtcItemType;
import com.px.gameserver.enums.items.ItemLocation;
import com.px.gameserver.enums.items.ItemState;
import com.px.gameserver.enums.items.ItemType;
import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.Augmentation;
import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.MercenaryTicket;
import com.px.gameserver.model.item.kind.Armor;
import com.px.gameserver.model.item.kind.EtcItem;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.model.item.kind.Weapon;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.residence.castle.Castle;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.DropItem;
import com.px.gameserver.network.serverpackets.GetItem;
import com.px.gameserver.network.serverpackets.SpawnItem;
import com.px.gameserver.scripting.Quest;
import com.px.gameserver.scripting.QuestState;
import com.px.gameserver.skills.basefuncs.Func;
import com.px.gameserver.taskmanager.ItemInstanceTaskManager;
import com.px.gameserver.taskmanager.ItemsOnGroundTaskManager;

/**
 * This class manages items.
 */
public final class ItemInstance extends WorldObject implements Runnable, Comparable<ItemInstance>
{
	private static final Logger ITEM_LOG = Logger.getLogger("item");
	
	private static final String RESTORE_AUGMENTATION = "SELECT attributes, skill_id, skill_level FROM augmentations WHERE item_oid = ?";
	
	private static final long REGULAR_LOOT_PROTECTION_TIME = 15000;
	private static final long RAID_LOOT_PROTECTION_TIME = 300000;
	
	private final Item _item;
	
	private int _ownerId;
	private int _dropperObjectId;
	private int _count;
	private int _enchantLevel;
	private int _manaLeft;
	private int _type1;
	private int _type2;
	private int _shotsMask;
	
	private ItemLocation _loc;
	private int _locationSlot;
	
	private long _time;
	
	private Augmentation _augmentation;
	
	private boolean _destroyProtected;
	
	private ScheduledFuture<?> _dropProtection;
	
	public ItemInstance(int objectId, int itemId)
	{
		this(objectId, ItemData.getInstance().getTemplate(itemId));
	}
	
	public ItemInstance(int objectId, Item item)
	{
		super(objectId);
		
		_item = item;
		_loc = ItemLocation.VOID;
		_manaLeft = (isShadowItem()) ? _item.getDuration() * 60 : -1;
		
		setName(item.getName());
		setCount(1);
	}
	
	public ItemInstance(int objectId, int itemId, int count, int enchantLevel)
	{
		super(objectId);
		
		_item = ItemData.getInstance().getTemplate(itemId);
		_count = count;
		_enchantLevel = enchantLevel;
		_loc = ItemLocation.VOID;
		_manaLeft = (isShadowItem()) ? _item.getDuration() * 60 : -1;
		
		setName(_item.getName());
	}
	
	public ItemInstance(ResultSet rs) throws SQLException
	{
		super(rs.getInt("object_id"));
		
		_item = ItemData.getInstance().getTemplate(rs.getInt("item_id"));
		_count = rs.getInt("count");
		_enchantLevel = rs.getInt("enchant_level");
		_ownerId = rs.getInt("owner_id");
		_type1 = rs.getInt("custom_type1");
		_type2 = rs.getInt("custom_type2");
		_loc = ItemLocation.valueOf(rs.getString("loc"));
		_locationSlot = rs.getInt("loc_data");
		_manaLeft = rs.getInt("mana_left");
		_time = rs.getLong("time");
		
		setName(_item.getName());
	}
	
	@Override
	public int compareTo(ItemInstance item)
	{
		final int time = Long.compare(item.getTime(), _time);
		if (time != 0)
			return time;
		
		return Integer.compare(item.getObjectId(), getObjectId());
	}
	
	@Override
	public void decayMe()
	{
		ItemsOnGroundTaskManager.getInstance().remove(this);
		
		super.decayMe();
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.isFlying())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Mercenaries tickets case.
		if (_item.getItemType() == EtcItemType.CASTLE_GUARD)
		{
			if (player.isInParty())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Castle castle = CastleManager.getInstance().getCastle(player);
			if (castle == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final MercenaryTicket ticket = castle.getTicket(_item.getItemId());
			if (ticket == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!player.isCastleLord(castle.getCastleId()))
			{
				player.sendPacket(SystemMessageId.THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_CANNOT_CANCEL_POSITIONING);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		player.getAI().tryToPickUp(getObjectId(), isShiftPressed);
	}
	
	@Override
	public synchronized void run()
	{
		_ownerId = 0;
		_dropProtection = null;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		if (_dropperObjectId != 0)
			player.sendPacket(new DropItem(this, _dropperObjectId));
		else
			player.sendPacket(new SpawnItem(this));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public String toString()
	{
		return "(" + getObjectId() + ") " + getName();
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param process : String Identifier of process triggering this action
	 * @param ownerId : int designating the ID of the owner
	 * @param creator : Player Player requesting the item creation
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void setOwnerId(String process, int ownerId, Playable creator, WorldObject reference)
	{
		setOwnerId(ownerId);
		
		if (Config.LOG_ITEMS)
		{
			final LogRecord record = new LogRecord(Level.INFO, "CHANGE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				creator,
				this,
				reference
			});
			ITEM_LOG.log(record);
		}
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param ownerId : int designating the ID of the owner
	 */
	public void setOwnerId(int ownerId)
	{
		if (ownerId == _ownerId)
			return;
		
		_ownerId = ownerId;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * Returns the ownerID of the item
	 * @return int : ownerID of the item
	 */
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setLocation(ItemLocation loc)
	{
		setLocation(loc, 0);
	}
	
	/**
	 * Sets the location of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param locData : int designating the slot where the item is stored or the village for freights
	 */
	public void setLocation(ItemLocation loc, int locData)
	{
		if (loc == _loc && locData == _locationSlot)
			return;
		
		_loc = loc;
		_locationSlot = locData;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public ItemLocation getLocation()
	{
		return _loc;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * @param count the new count to set
	 */
	public void setCount(int count)
	{
		if (_count == count)
			return;
		
		_count = Math.max(0, count);
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * Returns the quantity of item
	 * @return int
	 */
	public int getCount()
	{
		return _count;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param process : String Identifier of process triggering this action
	 * @param count : int
	 * @param creator : Player Player requesting the item creation
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void changeCount(String process, int count, Playable creator, WorldObject reference)
	{
		if (count == 0)
			return;
		
		if (count > 0 && getCount() > Integer.MAX_VALUE - count)
			setCount(Integer.MAX_VALUE);
		else
			setCount(getCount() + count);
		
		// List this item as IU-friendly.
		updateState(creator, ItemState.MODIFIED);
		
		if (Config.LOG_ITEMS && process != null)
		{
			final LogRecord record = new LogRecord(Level.INFO, "CHANGE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				creator,
				this,
				reference
			});
			ITEM_LOG.log(record);
		}
	}
	
	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return !(_item.getBodyPart() == 0 || _item.getItemType() == EtcItemType.ARROW || _item.getItemType() == EtcItemType.LURE);
	}
	
	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return _loc == ItemLocation.PAPERDOLL || _loc == ItemLocation.PET_EQUIP;
	}
	
	/**
	 * @return The slot where the item is stored (paperdoll slot or freight town id).
	 */
	public int getLocationSlot()
	{
		return _locationSlot;
	}
	
	/**
	 * @return The {@link Item} associated to that {@link ItemInstance}.
	 */
	public Item getItem()
	{
		return _item;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public void setCustomType1(int type)
	{
		_type1 = type;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public void setCustomType2(int type)
	{
		_type2 = type;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public boolean isOlyRestrictedItem()
	{
		return getItem().isOlyRestrictedItem();
	}
	
	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public ItemType getItemType()
	{
		return _item.getItemType();
	}
	
	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public int getItemId()
	{
		return _item.getItemId();
	}
	
	/**
	 * Returns true if item is an EtcItem
	 * @return boolean
	 */
	public boolean isEtcItem()
	{
		return (_item instanceof EtcItem);
	}
	
	/**
	 * @return True if this {@link ItemInstance} is an {@link Weapon}, or false otherwise.
	 */
	public boolean isWeapon()
	{
		return _item instanceof Weapon;
	}
	
	/**
	 * @return True if this {@link ItemInstance} is an {@link Armor}, or false otherwise.
	 */
	public boolean isArmor()
	{
		return _item instanceof Armor;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as a {@link EtcItem}, or null if it isn't the good instance type.
	 */
	public EtcItem getEtcItem()
	{
		return (_item instanceof EtcItem) ? (EtcItem) _item : null;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as a {@link Weapon}, or null if it isn't the good instance type.
	 */
	public Weapon getWeaponItem()
	{
		return (_item instanceof Weapon) ? (Weapon) _item : null;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as an {@link Armor}, or null if it isn't the good instance type.
	 */
	public Armor getArmorItem()
	{
		return (_item instanceof Armor) ? (Armor) _item : null;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _item.getCrystalCount(_enchantLevel);
	}
	
	/**
	 * @return the reference price of the item.
	 */
	public int getReferencePrice()
	{
		return _item.getReferencePrice();
	}
	
	/**
	 * @return the name of the item.
	 */
	public String getItemName()
	{
		return _item.getName();
	}
	
	/**
	 * Add an {@link ItemState} update for the Inventory of the {@link Creature} set as parameter.
	 * @param creature : The {@link Creature} owning the item.
	 * @param state : The {@link ItemState} to send as update.
	 */
	public void updateState(Creature creature, ItemState state)
	{
		if (creature == null)
			return;
		
		creature.getInventory().addUpdate(this, state);
	}
	
	/**
	 * @return True if this {@link ItemInstance} is stackable, or false otherwise.
	 */
	public boolean isStackable()
	{
		return _item.isStackable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is dropable, or false otherwise.
	 */
	public boolean isDropable()
	{
		return !isAugmented() && _item.isDropable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is dropable, or false otherwise.
	 */
	public boolean isDestroyable()
	{
		return !isQuestItem() && _item.isDestroyable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is tradable, or false otherwise.
	 */
	public boolean isTradable()
	{
		return !isAugmented() && _item.isTradable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is sellable, or false otherwise.
	 */
	public boolean isSellable()
	{
		return !isAugmented() && _item.isSellable();
	}
	
	/**
	 * @param isPrivateWarehouse : make additionals checks on tradable / shadow items.
	 * @return True if this {@link ItemInstance} can be deposited in warehouse or freight, or false otherwise.
	 */
	public boolean isDepositable(boolean isPrivateWarehouse)
	{
		// Equipped, hero and quest items can't be deposited.
		if (isEquipped() || !_item.isDepositable())
			return false;
		
		if (!isPrivateWarehouse)
		{
			// Non tradable or shadow items can't be deposited if not part of private warehouse.
			if (!isTradable() || isShadowItem())
				return false;
		}
		return true;
	}
	
	/**
	 * @return True if this {@link ItemInstance} is a consumable, or false otherwise.
	 */
	public boolean isConsumable()
	{
		return _item.isConsumable();
	}
	
	/**
	 * @param player : the player to check.
	 * @param allowAdena : if true, count adenas.
	 * @param allowNonTradable : if true, count non tradable items.
	 * @param allowStoreBuy
	 * @return if item is available for manipulation.
	 */
	public boolean isAvailable(Player player, boolean allowAdena, boolean allowNonTradable, boolean allowStoreBuy)
	{
		return ((!isEquipped() || allowStoreBuy) // Not equipped
			&& (getItem().getType2() != Item.TYPE2_QUEST) // Not Quest Item
			&& (getItem().getType2() != Item.TYPE2_MONEY || getItem().getType1() != Item.TYPE1_SHIELD_ARMOR) // not money, not shield
			&& (player.getSummon() == null || getObjectId() != player.getSummon().getControlItemId()) // Not Control item of currently summoned pet
			&& (player.getActiveEnchantItem() != this) // Not momentarily used enchant scroll
			&& (allowAdena || getItemId() != 57) // Not adena
			&& (player.getCast().getCurrentSkill() == null || player.getCast().getCurrentSkill().getItemConsumeId() != getItemId()) && (allowNonTradable || isTradable()));
	}
	
	/**
	 * @return the level of enchantment of the item.
	 */
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	/**
	 * Sets the level of enchantment of the item
	 * @param enchantLevel : number to apply.
	 * @param playable
	 */
	public void setEnchantLevel(int enchantLevel, Playable playable)
	{
		if (_enchantLevel == enchantLevel)
			return;
		
		_enchantLevel = enchantLevel;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
		
		// List this item as IU-friendly.
		updateState(playable, ItemState.MODIFIED);
	}
	
	/**
	 * @return whether this item is augmented or not ; true if augmented.
	 */
	public boolean isAugmented()
	{
		return _augmentation != null;
	}
	
	/**
	 * @return the augmentation object for this item.
	 */
	public Augmentation getAugmentation()
	{
		return _augmentation;
	}
	
	/**
	 * Set a new {@link Augmentation} to this {@link ItemInstance}.
	 * @param augmentation : The {@link Augmentation} to apply.
	 * @param player : The {@link Player} to refresh inventory from.
	 * @return True if the operation is successful, or false otherwise.
	 */
	public boolean setAugmentation(Augmentation augmentation, Player player)
	{
		// There shall be no previous augmentation.
		if (_augmentation != null)
			return false;
		
		_augmentation = augmentation;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
		
		// List this item as IU-friendly.
		updateState(player, ItemState.MODIFIED);
		
		return true;
	}
	
	/**
	 * Remove the augmentation associated to this {@link ItemInstance}.
	 * @param player : The {@link Player} to refresh inventory from.
	 */
	public void removeAugmentation(Player player)
	{
		if (_augmentation == null)
			return;
		
		_augmentation = null;
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
		
		// List this item as IU-friendly.
		updateState(player, ItemState.MODIFIED);
	}
	
	private void restoreAttributes()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_AUGMENTATION))
		{
			ps.setInt(1, getObjectId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
					_augmentation = new Augmentation(rs.getInt("attributes"), rs.getInt("skill_id"), rs.getInt("skill_level"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore augmentation for {}.", e, toString());
		}
	}
	
	/**
	 * @return True if this {@link ItemInstance} is a shadow item. Shadow items have a limited life-time.
	 */
	public boolean isShadowItem()
	{
		return _item.getDuration() > -1;
	}
	
	/**
	 * Decrease the mana for this {@link ItemInstance}.
	 * @param amount : The amount to decrease out of manaLeft variable.
	 */
	public void decreaseMana(int amount)
	{
		_manaLeft -= Math.min(_manaLeft, amount);
		
		// List this item as database-friendly.
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * @return The remaining mana of this {@link ItemInstance} in seconds.
	 */
	public int getManaLeft()
	{
		return _manaLeft;
	}
	
	/**
	 * @return The remaining mana of this {@link ItemInstance} for display purpose (as a minute).
	 */
	public int getDisplayedManaLeft()
	{
		return _manaLeft / 60;
	}
	
	/**
	 * @param creature : The {@link Creature} used as parameter.
	 * @return An array of {@link Func}s based on this {@link ItemInstance}'s {@link Item} template and the {@link Creature} set as parameter.
	 */
	public List<Func> getStatFuncs(Creature creature)
	{
		return getItem().getStatFuncs(this, creature);
	}
	
	/**
	 * @param rs : The {@link ResultSet} of the item.
	 * @return A new {@link ItemInstance} from database using a {@link ResultSet} content.
	 */
	public static ItemInstance restoreFromDb(ResultSet rs)
	{
		try
		{
			final ItemInstance item = new ItemInstance(rs);
			
			// Load augmentation.
			if (item.isEquipable())
				item.restoreAttributes();
			
			return item;
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore an owned item.", e);
			return null;
		}
	}
	
	/**
	 * Validate intended dropping location, set it and spawn this {@link ItemInstance} to the world.
	 * @param dropper : The {@link Creature} dropper.
	 * @param x : The X coordinate of intended location.
	 * @param y : The Y coordinate of intended location.
	 * @param z : The Z coordinate of intended location.
	 */
	public final void dropMe(Creature dropper, int x, int y, int z)
	{
		ThreadPool.execute(() ->
		{
			// Set the dropper OID for sendInfo show correct dropping animation.
			setDropperObjectId(dropper.getObjectId());
			
			// Drop current World registration, mostly for FREIGHT case.
			World.getInstance().removeObject(this);
			
			// Validate location and spawn.
			spawnMe(GeoEngine.getInstance().getValidLocation(dropper, x, y, z));
			ItemsOnGroundTaskManager.getInstance().add(this, dropper);
			
			// Set the dropper OID back to 0, so sendInfo show item on ground.
			setDropperObjectId(0);
		});
	}
	
	/**
	 * Calculate dropping location from {@link Creature} location and offset, validate it, set it and spawn this {@link ItemInstance} to the world.
	 * @param dropper : The {@link Creature} dropper.
	 * @param offset : The offset used to calculate dropping location around {@link Creature}.
	 */
	public final void dropMe(Creature dropper, int offset)
	{
		// Create drop location.
		final Location loc = dropper.getPosition().clone();
		loc.addRandomOffset(offset);
		
		ThreadPool.execute(() ->
		{
			// Set the dropper OID for sendInfo show correct dropping animation.
			setDropperObjectId(dropper.getObjectId());
			
			// Drop current World registration, mostly for FREIGHT case.
			World.getInstance().removeObject(this);
			
			// Validate location itself and spawn.
			spawnMe(GeoEngine.getInstance().getValidLocation(dropper, loc));
			ItemsOnGroundTaskManager.getInstance().add(this, dropper);
			
			// Set the dropper OID back to 0, so sendInfo show item on ground.
			setDropperObjectId(0);
		});
	}
	
	/**
	 * Remove this {@link ItemInstance} from the visible world and broadcast GetItem packet.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _objects of World.</B></FONT><BR>
	 * <BR>
	 * @param creature : The {@link Creature} that pick up the item.
	 */
	public final void pickupMe(Creature creature)
	{
		creature.broadcastPacket(new GetItem(this, creature.getObjectId()));
		
		// Unregister dropped ticket from castle, if that item is on a castle area and is a valid ticket.
		final Castle castle = CastleManager.getInstance().getCastle(this);
		if (castle != null && castle.getTicket(getItemId()) != null)
			castle.removeDroppedTicket(this);
		
		if (getItemId() == 57 || getItemId() == 6353)
		{
			final Player actor = creature.getActingPlayer();
			if (actor != null)
			{
				final QuestState qs = actor.getQuestList().getQuestState("Tutorial");
				if (qs != null)
					qs.getQuest().notifyEvent("CE" + getItemId() + "", null, actor);
			}
		}
		
		// Calls directly setRegion(null), we don't have to care about.
		setIsVisible(false);
	}
	
	public synchronized boolean hasDropProtection()
	{
		return _dropProtection != null;
	}
	
	public synchronized void setDropProtection(int ownerId, boolean isRaidParty)
	{
		_ownerId = ownerId;
		_dropProtection = ThreadPool.schedule(this, (isRaidParty) ? RAID_LOOT_PROTECTION_TIME : REGULAR_LOOT_PROTECTION_TIME);
	}
	
	public synchronized void removeDropProtection()
	{
		if (_dropProtection != null)
		{
			_dropProtection.cancel(true);
			_dropProtection = null;
		}
		
		_ownerId = 0;
	}
	
	public void setDestroyProtected(boolean destroyProtected)
	{
		_destroyProtected = destroyProtected;
	}
	
	public boolean isDestroyProtected()
	{
		return _destroyProtected;
	}
	
	public long getTime()
	{
		return _time;
	}
	
	public void actualizeTime()
	{
		_time = System.currentTimeMillis();
	}
	
	public boolean isPetItem()
	{
		return getItem().isPetItem();
	}
	
	public boolean isPotion()
	{
		return getItem().isPotion();
	}
	
	public boolean isElixir()
	{
		return getItem().isElixir();
	}
	
	public boolean isHerb()
	{
		return getItem().getItemType() == EtcItemType.HERB;
	}
	
	public boolean isSummonItem()
	{
		return getItem().getItemType() == EtcItemType.PET_COLLAR;
	}
	
	public boolean isHeroItem()
	{
		return getItem().isHeroItem();
	}
	
	public boolean isQuestItem()
	{
		return getItem().isQuestItem();
	}
	
	/**
	 * Create an {@link ItemInstance} corresponding to the itemId and count, add it to the server and logs the activity.
	 * @param itemId : The itemId of the item to be created.
	 * @param count : The quantity of items to be created for stackable items.
	 * @param actor : The {@link Player} requesting the item creation.
	 * @param reference : The {@link WorldObject} referencing current action like NPC selling item or previous item in transformation.
	 * @return a new ItemInstance corresponding to the itemId and count.
	 */
	public static ItemInstance create(int itemId, int count, Playable actor, WorldObject reference)
	{
		// Create and Init the ItemInstance corresponding to the Item Identifier
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
		// Add the ItemInstance object to _objects of World.
		World.getInstance().addObject(item);
		
		// Set Item parameters
		if (item.isStackable() && count > 1)
			item.setCount(count);
		
		if (Config.LOG_ITEMS)
		{
			final LogRecord record = new LogRecord(Level.INFO, "CREATE");
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				actor,
				item,
				reference
			});
			ITEM_LOG.log(record);
		}
		
		return item;
	}
	
	/**
	 * Destroys this {@link ItemInstance} from server, and release its objectId.
	 * @param process : The identifier of process triggering this action (used by logs).
	 * @param actor : The {@link Player} requesting the item destruction.
	 * @param reference : The {@link WorldObject} referencing current action like NPC selling item or previous item in transformation.
	 */
	public void destroyMe(String process, Playable actor, WorldObject reference)
	{
		setCount(0);
		setOwnerId(0);
		setLocation(ItemLocation.VOID);
		
		World.getInstance().removeObject(this);
		IdFactory.getInstance().releaseId(getObjectId());
		
		if (Config.LOG_ITEMS)
		{
			final LogRecord record = new LogRecord(Level.INFO, "DELETE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				actor,
				this,
				reference
			});
			ITEM_LOG.log(record);
		}
	}
	
	public void setDropperObjectId(int id)
	{
		_dropperObjectId = id;
	}
	
	public List<Quest> getQuestEvents()
	{
		return _item.getQuestEvents();
	}
	
	public void unChargeAllShots()
	{
		_shotsMask = 0;
	}
}