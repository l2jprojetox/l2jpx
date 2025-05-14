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
package org.l2jpx.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.cache.PaperdollCache;
import org.l2jpx.gameserver.data.xml.AppearanceItemData;
import org.l2jpx.gameserver.data.xml.ArmorSetData;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.enums.ItemLocation;
import org.l2jpx.gameserver.enums.ItemSkillType;
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.enums.SkillFinishType;
import org.l2jpx.gameserver.model.ArmorSet;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerItemUnequip;
import org.l2jpx.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2jpx.gameserver.model.holders.ItemSkillHolder;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.appearance.AppearanceStone;
import org.l2jpx.gameserver.model.item.appearance.AppearanceType;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.EtcItemType;
import org.l2jpx.gameserver.model.item.type.ItemType;
import org.l2jpx.gameserver.model.item.type.WeaponType;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.skill.SkillConditionScope;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2jpx.gameserver.network.serverpackets.SkillCoolTime;

/**
 * This class manages inventory
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer
{
	protected static final Logger LOGGER = Logger.getLogger(Inventory.class.getName());
	
	public interface PaperdollListener
	{
		void notifyEquiped(int slot, Item inst, Inventory inventory);
		
		void notifyUnequiped(int slot, Item inst, Inventory inventory);
	}
	
	// Common Items
	public static final int ADENA_ID = 57;
	public static final int ANCIENT_ADENA_ID = 5575;
	public static final int BEAUTY_TICKET_ID = 36308;
	public static final int AIR_STONE_ID = 39461;
	public static final int TEMPEST_STONE_ID = 39592;
	public static final int ELCYUM_CRYSTAL_ID = 36514;
	
	public static final long MAX_ADENA = Config.MAX_ADENA;
	
	public static final int PAPERDOLL_UNDER = 0;
	public static final int PAPERDOLL_HEAD = 1;
	public static final int PAPERDOLL_HAIR = 2;
	public static final int PAPERDOLL_HAIR2 = 3;
	public static final int PAPERDOLL_NECK = 4;
	public static final int PAPERDOLL_RHAND = 5;
	public static final int PAPERDOLL_CHEST = 6;
	public static final int PAPERDOLL_LHAND = 7;
	public static final int PAPERDOLL_REAR = 8;
	public static final int PAPERDOLL_LEAR = 9;
	public static final int PAPERDOLL_GLOVES = 10;
	public static final int PAPERDOLL_LEGS = 11;
	public static final int PAPERDOLL_FEET = 12;
	public static final int PAPERDOLL_RFINGER = 13;
	public static final int PAPERDOLL_LFINGER = 14;
	public static final int PAPERDOLL_LBRACELET = 15;
	public static final int PAPERDOLL_RBRACELET = 16;
	public static final int PAPERDOLL_DECO1 = 17;
	public static final int PAPERDOLL_DECO2 = 18;
	public static final int PAPERDOLL_DECO3 = 19;
	public static final int PAPERDOLL_DECO4 = 20;
	public static final int PAPERDOLL_DECO5 = 21;
	public static final int PAPERDOLL_DECO6 = 22;
	public static final int PAPERDOLL_CLOAK = 23;
	public static final int PAPERDOLL_BELT = 24;
	public static final int PAPERDOLL_BROOCH = 25;
	public static final int PAPERDOLL_BROOCH_JEWEL1 = 26;
	public static final int PAPERDOLL_BROOCH_JEWEL2 = 27;
	public static final int PAPERDOLL_BROOCH_JEWEL3 = 28;
	public static final int PAPERDOLL_BROOCH_JEWEL4 = 29;
	public static final int PAPERDOLL_BROOCH_JEWEL5 = 30;
	public static final int PAPERDOLL_BROOCH_JEWEL6 = 31;
	public static final int PAPERDOLL_TOTALSLOTS = 32;
	
	// Speed percentage mods
	public static final double MAX_ARMOR_WEIGHT = 12000;
	
	private final Item[] _paperdoll;
	private final List<PaperdollListener> _paperdollListeners;
	private final PaperdollCache _paperdollCache = new PaperdollCache();
	
	// protected to be accessed from child classes only
	protected int _totalWeight;
	
	// used to quickly check for using of items of special type
	private int _wearedMask;
	
	private int _blockedItemSlotsMask;
	
	// Recorder of alterations in inventory
	private static class ChangeRecorder implements PaperdollListener
	{
		private final Inventory _inventory;
		private final List<Item> _changed = new ArrayList<>(1);
		
		/**
		 * Constructor of the ChangeRecorder
		 * @param inventory
		 */
		ChangeRecorder(Inventory inventory)
		{
			_inventory = inventory;
			_inventory.addPaperdollListener(this);
		}
		
		/**
		 * Add alteration in inventory when item equipped
		 * @param slot
		 * @param item
		 * @param inventory
		 */
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
			_changed.add(item);
		}
		
		/**
		 * Add alteration in inventory when item unequipped
		 * @param slot
		 * @param item
		 * @param inventory
		 */
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			_changed.add(item);
		}
		
		/**
		 * Returns alterations in inventory
		 * @return Item[] : array of altered items
		 */
		public List<Item> getChangedItems()
		{
			return _changed;
		}
	}
	
	private static class BowCrossRodListener implements PaperdollListener
	{
		private static BowCrossRodListener instance = new BowCrossRodListener();
		
		public static BowCrossRodListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			if (slot != PAPERDOLL_RHAND)
			{
				return;
			}
			
			if (item.getItemType() == WeaponType.BOW)
			{
				final Item arrow = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (arrow != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
			else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWOHANDCROSSBOW))
			{
				final Item bolts = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (bolts != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
			else if (item.getItemType() == WeaponType.FISHINGROD)
			{
				final Item lure = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (lure != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
		}
		
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
			if (slot != PAPERDOLL_RHAND)
			{
				return;
			}
			
			if (item.getItemType() == WeaponType.BOW)
			{
				final Item arrow = inventory.findArrowForBow(item.getTemplate());
				if (arrow != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, arrow);
				}
			}
			else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWOHANDCROSSBOW))
			{
				final Item bolts = inventory.findBoltForCrossBow(item.getTemplate());
				if (bolts != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, bolts);
				}
			}
		}
	}
	
	private static class StatsListener implements PaperdollListener
	{
		private static StatsListener instance = new StatsListener();
		
		public static StatsListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			inventory.getOwner().getStat().recalculateStats(true);
		}
		
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
			inventory.getOwner().getStat().recalculateStats(true);
		}
	}
	
	private static class ItemSkillsListener implements PaperdollListener
	{
		private static ItemSkillsListener instance = new ItemSkillsListener();
		
		public static ItemSkillsListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final Player player = (Player) inventory.getOwner();
			final ItemTemplate it = item.getTemplate();
			final Map<Integer, Skill> addedSkills = new HashMap<>(1);
			final Map<Integer, Skill> removedSkills = new HashMap<>(1);
			boolean update = false;
			boolean updateTimestamp = false;
			
			// Remove augmentation bonuses on unequip
			if (item.isAugmented())
			{
				item.getAugmentation().removeBonus(player);
			}
			
			// Recalculate all stats
			player.getStat().recalculateStats(true);
			
			// Clear enchant bonus
			item.clearEnchantStats();
			
			if (it.hasSkills())
			{
				final List<ItemSkillHolder> onEnchantSkills = it.getSkills(ItemSkillType.ON_ENCHANT);
				if (onEnchantSkills != null)
				{
					for (ItemSkillHolder holder : onEnchantSkills)
					{
						if (item.getEnchantLevel() < holder.getValue())
						{
							continue;
						}
						
						final Skill skill = holder.getSkill();
						if (skill != null)
						{
							removedSkills.putIfAbsent(skill.getId(), skill);
							update = true;
						}
					}
				}
				
				final List<ItemSkillHolder> normalSkills = it.getSkills(ItemSkillType.NORMAL);
				if (normalSkills != null)
				{
					for (ItemSkillHolder holder : normalSkills)
					{
						final Skill skill = holder.getSkill();
						if (skill != null)
						{
							removedSkills.putIfAbsent(skill.getId(), skill);
							update = true;
						}
					}
				}
				
				if (item.isArmor())
				{
					for (Item itm : inventory.getItems())
					{
						if (!itm.isEquipped() || itm.equals(item))
						{
							continue;
						}
						
						final List<ItemSkillHolder> otherNormalSkills = itm.getTemplate().getSkills(ItemSkillType.NORMAL);
						if (otherNormalSkills == null)
						{
							continue;
						}
						
						for (ItemSkillHolder holder : otherNormalSkills)
						{
							if (player.getSkillLevel(holder.getSkillId()) != 0)
							{
								continue;
							}
							
							final Skill skill = holder.getSkill();
							if (skill == null)
							{
								continue;
							}
							
							final Skill existingSkill = addedSkills.get(skill.getId());
							if (existingSkill != null)
							{
								if (existingSkill.getLevel() < skill.getLevel())
								{
									addedSkills.put(skill.getId(), skill);
								}
							}
							else
							{
								addedSkills.put(skill.getId(), skill);
							}
							
							if (skill.isActive() && !player.hasSkillReuse(skill.getReuseHashCode()))
							{
								final int equipDelay = item.getEquipReuseDelay();
								if (equipDelay > 0)
								{
									player.addTimeStamp(skill, equipDelay);
									player.disableSkill(skill, equipDelay);
								}
								updateTimestamp = true;
							}
							update = true;
						}
					}
				}
			}
			
			// Must check all equipped items for enchant conditions.
			for (Item equipped : inventory.getPaperdollItems())
			{
				if (!equipped.getTemplate().hasSkills())
				{
					continue;
				}
				
				final List<ItemSkillHolder> otherEnchantSkills = equipped.getTemplate().getSkills(ItemSkillType.ON_ENCHANT);
				if (otherEnchantSkills == null)
				{
					continue;
				}
				
				for (ItemSkillHolder holder : otherEnchantSkills)
				{
					if (equipped.getEnchantLevel() < holder.getValue())
					{
						continue;
					}
					
					final Skill skill = holder.getSkill();
					if (skill == null)
					{
						continue;
					}
					
					// Check passive skill conditions.
					if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
					{
						removedSkills.putIfAbsent(skill.getId(), skill);
						update = true;
					}
				}
			}
			
			// Must check for toggle and isRemovedOnUnequipWeapon skill item conditions.
			for (Skill skill : player.getAllSkills())
			{
				if ((skill.isToggle() && player.isAffectedBySkill(skill.getId()) && !skill.checkConditions(SkillConditionScope.GENERAL, player, player)) //
					|| (it.isWeapon() && skill.isRemovedOnUnequipWeapon()))
				{
					player.stopSkillEffects(SkillFinishType.REMOVED, skill.getId());
					update = true;
				}
			}
			
			// Apply skill, if weapon have "skills on unequip"
			it.forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> holder.getSkill().activateSkill(player, player));
			
			if (update)
			{
				for (Skill skill : removedSkills.values())
				{
					player.removeSkill(skill, false, skill.isPassive());
				}
				
				for (Skill skill : addedSkills.values())
				{
					player.addSkill(skill, false);
				}
				
				player.sendSkillList();
			}
			
			if (updateTimestamp)
			{
				player.sendPacket(new SkillCoolTime(player));
			}
			
			if (item.isWeapon())
			{
				player.unchargeAllShots();
			}
		}
		
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final Player player = (Player) inventory.getOwner();
			// Any items equipped that result in expertise penalty do not give any skills at all.
			if (item.getTemplate().getCrystalType().getLevel() > player.getExpertiseLevel())
			{
				return;
			}
			
			final Map<Integer, Skill> addedSkills = new HashMap<>(1);
			boolean updateTimestamp = false;
			
			// Apply augmentation bonuses on equip
			if (item.isAugmented())
			{
				item.getAugmentation().applyBonus(player);
			}
			
			// Recalculate all stats
			player.getStat().recalculateStats(true);
			
			// Apply enchant stats
			item.applyEnchantStats();
			
			if (item.getTemplate().hasSkills())
			{
				final List<ItemSkillHolder> onEnchantSkills = item.getTemplate().getSkills(ItemSkillType.ON_ENCHANT);
				if (onEnchantSkills != null)
				{
					for (ItemSkillHolder holder : onEnchantSkills)
					{
						if (player.getSkillLevel(holder.getSkillId()) >= holder.getSkillLevel())
						{
							continue;
						}
						
						if (item.getEnchantLevel() < holder.getValue())
						{
							continue;
						}
						
						final Skill skill = holder.getSkill();
						if (skill == null)
						{
							continue;
						}
						
						// Check passive skill conditions.
						if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							continue;
						}
						
						final Skill existingSkill = addedSkills.get(skill.getId());
						if (existingSkill != null)
						{
							if (existingSkill.getLevel() < skill.getLevel())
							{
								addedSkills.put(skill.getId(), skill);
							}
						}
						else
						{
							addedSkills.put(skill.getId(), skill);
						}
						
						// Active, non offensive, skills start with reuse on equip.
						if (skill.isActive() && !skill.isBad() && !skill.isTransformation() && (Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE > 0) && player.hasEnteredWorld())
						{
							player.addTimeStamp(skill, skill.getReuseDelay() > 0 ? skill.getReuseDelay() : Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE);
							updateTimestamp = true;
						}
					}
				}
				
				final List<ItemSkillHolder> normalSkills = item.getTemplate().getSkills(ItemSkillType.NORMAL);
				if (normalSkills != null)
				{
					for (ItemSkillHolder holder : normalSkills)
					{
						if (player.getSkillLevel(holder.getSkillId()) >= holder.getSkillLevel())
						{
							continue;
						}
						
						final Skill skill = holder.getSkill();
						if (skill == null)
						{
							continue;
						}
						
						// Check passive skill conditions.
						if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							continue;
						}
						
						final Skill existingSkill = addedSkills.get(skill.getId());
						if (existingSkill != null)
						{
							if (existingSkill.getLevel() < skill.getLevel())
							{
								addedSkills.put(skill.getId(), skill);
							}
						}
						else
						{
							addedSkills.put(skill.getId(), skill);
						}
						
						if (skill.isActive())
						{
							if (!player.hasSkillReuse(skill.getReuseHashCode()))
							{
								final int equipDelay = item.getEquipReuseDelay();
								if (equipDelay > 0)
								{
									player.addTimeStamp(skill, equipDelay);
									player.disableSkill(skill, equipDelay);
								}
							}
							
							// Active, non offensive, skills start with reuse on equip.
							if (!skill.isBad() && !skill.isTransformation() && (Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE > 0) && player.hasEnteredWorld())
							{
								player.addTimeStamp(skill, skill.getReuseDelay() > 0 ? skill.getReuseDelay() : Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE);
							}
							
							updateTimestamp = true;
						}
					}
				}
			}
			
			// Must check all equipped items for enchant conditions.
			for (Item equipped : inventory.getPaperdollItems())
			{
				if (!equipped.getTemplate().hasSkills())
				{
					continue;
				}
				
				final List<ItemSkillHolder> otherEnchantSkills = equipped.getTemplate().getSkills(ItemSkillType.ON_ENCHANT);
				if (otherEnchantSkills == null)
				{
					continue;
				}
				
				for (ItemSkillHolder holder : otherEnchantSkills)
				{
					if (player.getSkillLevel(holder.getSkillId()) >= holder.getSkillLevel())
					{
						continue;
					}
					
					if (equipped.getEnchantLevel() < holder.getValue())
					{
						continue;
					}
					
					final Skill skill = holder.getSkill();
					if (skill == null)
					{
						continue;
					}
					
					// Check passive skill conditions.
					if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
					{
						continue;
					}
					
					final Skill existingSkill = addedSkills.get(skill.getId());
					if (existingSkill != null)
					{
						if (existingSkill.getLevel() < skill.getLevel())
						{
							addedSkills.put(skill.getId(), skill);
						}
					}
					else
					{
						addedSkills.put(skill.getId(), skill);
					}
					
					// Active, non offensive, skills start with reuse on equip.
					if (skill.isActive() && !skill.isBad() && !skill.isTransformation() && (Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE > 0) && player.hasEnteredWorld())
					{
						player.addTimeStamp(skill, skill.getReuseDelay() > 0 ? skill.getReuseDelay() : Config.ITEM_EQUIP_ACTIVE_SKILL_REUSE);
						updateTimestamp = true;
					}
				}
			}
			
			// Apply skill, if weapon have "skills on equip"
			item.getTemplate().forEachSkill(ItemSkillType.ON_EQUIP, holder -> holder.getSkill().activateSkill(player, player));
			
			if (!addedSkills.isEmpty())
			{
				for (Skill skill : addedSkills.values())
				{
					player.addSkill(skill, false);
				}
				
				player.sendSkillList();
			}
			
			if (updateTimestamp)
			{
				player.sendPacket(new SkillCoolTime(player));
			}
		}
	}
	
	private static class ArmorSetListener implements PaperdollListener
	{
		private static ArmorSetListener instance = new ArmorSetListener();
		
		public static ArmorSetListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final Player player = (Player) inventory.getOwner();
			boolean update = false;
			
			// Verify and apply normal set
			if (verifyAndApply(player, item, Item::getId))
			{
				update = true;
			}
			
			// Verify and apply visual set
			final int itemVisualId = item.getVisualId();
			if (itemVisualId > 0)
			{
				final int appearanceStoneId = item.getAppearanceStoneId();
				final AppearanceStone stone = AppearanceItemData.getInstance().getStone(appearanceStoneId > 0 ? appearanceStoneId : itemVisualId);
				if ((stone != null) && (stone.getType() == AppearanceType.FIXED) && verifyAndApply(player, item, Item::getVisualId))
				{
					update = true;
				}
			}
			
			if (update)
			{
				player.sendSkillList();
			}
			
			if ((item.getTemplate().getBodyPart() == ItemTemplate.SLOT_BROOCH_JEWEL) || (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_BROOCH))
			{
				player.updateActiveBroochJewel();
			}
		}
		
		private static boolean applySkills(Player player, Item item, ArmorSet armorSet, Function<Item, Integer> idProvider)
		{
			final long piecesCount = armorSet.getPieceCount(player, idProvider);
			if (piecesCount >= armorSet.getMinimumPieces())
			{
				// Applying all skills that matching the conditions
				boolean updateTimeStamp = false;
				boolean update = false;
				for (ArmorsetSkillHolder holder : armorSet.getSkills())
				{
					if (player.getSkillLevel(holder.getSkillId()) >= holder.getSkillLevel())
					{
						continue;
					}
					
					if (holder.validateConditions(player, armorSet, idProvider))
					{
						final Skill itemSkill = holder.getSkill();
						if (itemSkill == null)
						{
							LOGGER.warning("Inventory.ArmorSetListener.addSkills: Incorrect skill: " + holder);
							continue;
						}
						
						if (itemSkill.isPassive() && !itemSkill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							continue;
						}
						
						player.addSkill(itemSkill, false);
						if (itemSkill.isActive())
						{
							if ((item != null) && !player.hasSkillReuse(itemSkill.getReuseHashCode()))
							{
								final int equipDelay = item.getEquipReuseDelay();
								if (equipDelay > 0)
								{
									player.addTimeStamp(itemSkill, equipDelay);
									player.disableSkill(itemSkill, equipDelay);
								}
							}
							
							// Active, non offensive, skills start with reuse on equip.
							if (!itemSkill.isBad() && !itemSkill.isTransformation() && (Config.ARMOR_SET_EQUIP_ACTIVE_SKILL_REUSE > 0) && player.hasEnteredWorld())
							{
								player.addTimeStamp(itemSkill, itemSkill.getReuseDelay() > 0 ? itemSkill.getReuseDelay() : Config.ARMOR_SET_EQUIP_ACTIVE_SKILL_REUSE);
							}
							
							updateTimeStamp = true;
						}
						
						update = true;
					}
				}
				if (updateTimeStamp)
				{
					player.sendPacket(new SkillCoolTime(player));
				}
				return update;
			}
			return false;
		}
		
		private static boolean verifyAndApply(Player player, Item item, Function<Item, Integer> idProvider)
		{
			boolean update = false;
			final List<ArmorSet> armorSets = ArmorSetData.getInstance().getSets(idProvider.apply(item));
			for (ArmorSet armorSet : armorSets)
			{
				if (applySkills(player, item, armorSet, idProvider))
				{
					update = true;
				}
			}
			return update;
		}
		
		private static boolean verifyAndRemove(Player player, Item item, Function<Item, Integer> idProvider)
		{
			boolean update = false;
			final List<ArmorSet> armorSets = ArmorSetData.getInstance().getSets(idProvider.apply(item));
			for (ArmorSet armorSet : armorSets)
			{
				// Remove all skills that doesn't matches the conditions
				for (ArmorsetSkillHolder holder : armorSet.getSkills())
				{
					if (!holder.validateConditions(player, armorSet, idProvider))
					{
						final Skill itemSkill = holder.getSkill();
						if (itemSkill == null)
						{
							LOGGER.warning("Inventory.ArmorSetListener.removeSkills: Incorrect skill: " + holder);
							continue;
						}
						
						// Update if a skill has been removed.
						if (player.removeSkill(itemSkill, false, itemSkill.isPassive()) != null)
						{
							update = true;
						}
					}
				}
				
				// Attempt to apply lower level skills if possible
				if (applySkills(player, item, armorSet, idProvider))
				{
					update = true;
				}
			}
			
			return update;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final Player player = (Player) inventory.getOwner();
			boolean remove = false;
			
			// Verify and remove normal set bonus
			if (verifyAndRemove(player, item, Item::getId))
			{
				remove = true;
			}
			
			// Verify and remove visual set bonus
			final int itemVisualId = item.getVisualId();
			if (itemVisualId > 0)
			{
				final int appearanceStoneId = item.getAppearanceStoneId();
				final AppearanceStone stone = AppearanceItemData.getInstance().getStone(appearanceStoneId > 0 ? appearanceStoneId : itemVisualId);
				if ((stone != null) && (stone.getType() == AppearanceType.FIXED) && verifyAndRemove(player, item, Item::getVisualId))
				{
					remove = true;
				}
			}
			
			if (remove)
			{
				player.checkItemRestriction();
				player.sendSkillList();
			}
			
			if ((item.getTemplate().getBodyPart() == ItemTemplate.SLOT_BROOCH_JEWEL) || (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_BROOCH))
			{
				player.updateActiveBroochJewel();
			}
		}
	}
	
	private static class BraceletListener implements PaperdollListener
	{
		private static BraceletListener instance = new BraceletListener();
		
		public static BraceletListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			final Player player = item.getActingPlayer();
			if ((player != null) && player.isChangingClass())
			{
				return;
			}
			
			if (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_R_BRACELET)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_DECO1);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO2);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO3);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO4);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO5);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO6);
			}
		}
		
		// Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
		}
	}
	
	private static class BroochListener implements PaperdollListener
	{
		private static BroochListener instance = new BroochListener();
		
		public static BroochListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, Item item, Inventory inventory)
		{
			final Player player = item.getActingPlayer();
			if ((player != null) && player.isChangingClass())
			{
				return;
			}
			
			if (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_BROOCH)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL1);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL2);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL3);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL4);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL5);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL6);
			}
		}
		
		// Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
		@Override
		public void notifyEquiped(int slot, Item item, Inventory inventory)
		{
		}
	}
	
	/**
	 * Constructor of the inventory
	 */
	protected Inventory()
	{
		_paperdoll = new Item[PAPERDOLL_TOTALSLOTS];
		_paperdollListeners = new ArrayList<>();
		if (this instanceof PlayerInventory)
		{
			addPaperdollListener(ArmorSetListener.getInstance());
			addPaperdollListener(BowCrossRodListener.getInstance());
			addPaperdollListener(ItemSkillsListener.getInstance());
			addPaperdollListener(BraceletListener.getInstance());
			addPaperdollListener(BroochListener.getInstance());
		}
		
		// common
		addPaperdollListener(StatsListener.getInstance());
	}
	
	protected abstract ItemLocation getEquipLocation();
	
	/**
	 * Returns the instance of new ChangeRecorder
	 * @return ChangeRecorder
	 */
	private ChangeRecorder newRecorder()
	{
		return new ChangeRecorder(this);
	}
	
	/**
	 * Drop item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param item : Item to be dropped
	 * @param actor : Player Player requesting the item drop
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return Item corresponding to the destroyed item or the updated item in inventory
	 */
	public Item dropItem(String process, Item item, Player actor, Object reference)
	{
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!_items.contains(item))
			{
				return null;
			}
			
			removeItem(item);
			item.setOwnerId(process, 0, actor, reference);
			item.setItemLocation(ItemLocation.VOID);
			item.setLastChange(Item.REMOVED);
			
			item.updateDatabase();
			refreshWeight();
		}
		return item;
	}
	
	/**
	 * Drop item from inventory by using its <b>objectID</b> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be dropped
	 * @param count : int Quantity of items to be dropped
	 * @param actor : Player Player requesting the item drop
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return Item corresponding to the destroyed item or the updated item in inventory
	 */
	public Item dropItem(String process, int objectId, long count, Player actor, Object reference)
	{
		Item item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!_items.contains(item))
			{
				return null;
			}
			
			// Adjust item quantity and create new instance to drop
			// Directly drop entire item
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(Item.MODIFIED);
				item.updateDatabase();
				
				final Item newItem = ItemData.getInstance().createItem(process, item.getId(), count, actor, reference);
				newItem.updateDatabase();
				refreshWeight();
				return newItem;
			}
		}
		
		return dropItem(process, item, actor, reference);
	}
	
	/**
	 * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)
	 * @param item : Item to be added from inventory
	 */
	@Override
	protected void addItem(Item item)
	{
		super.addItem(item);
		if (item.isEquipped())
		{
			equipItem(item);
		}
	}
	
	/**
	 * Removes item from inventory for further adjustments.
	 * @param item : Item to be removed from inventory
	 */
	@Override
	protected boolean removeItem(Item item)
	{
		// Unequip item if equiped
		for (int i = 0; i < _paperdoll.length; i++)
		{
			if (_paperdoll[i] == item)
			{
				unEquipItemInSlot(i);
			}
		}
		return super.removeItem(item);
	}
	
	/**
	 * @param slot the slot.
	 * @return the item in the paperdoll slot
	 */
	public Item getPaperdollItem(int slot)
	{
		return _paperdoll[slot];
	}
	
	/**
	 * @param slot the slot.
	 * @return {@code true} if specified paperdoll slot is empty, {@code false} otherwise
	 */
	public boolean isPaperdollSlotEmpty(int slot)
	{
		return _paperdoll[slot] == null;
	}
	
	public boolean isPaperdollSlotNotEmpty(int slot)
	{
		return _paperdoll[slot] != null;
	}
	
	public boolean isItemEquipped(int itemId)
	{
		for (Item item : _paperdoll)
		{
			if ((item != null) && (item.getId() == itemId))
			{
				return true;
			}
		}
		return false;
	}
	
	public static int getPaperdollIndex(int slot)
	{
		switch (slot)
		{
			case ItemTemplate.SLOT_UNDERWEAR:
			{
				return PAPERDOLL_UNDER;
			}
			case ItemTemplate.SLOT_LR_EAR:
			case ItemTemplate.SLOT_R_EAR:
			{
				return PAPERDOLL_REAR;
			}
			case ItemTemplate.SLOT_L_EAR:
			{
				return PAPERDOLL_LEAR;
			}
			case ItemTemplate.SLOT_NECK:
			{
				return PAPERDOLL_NECK;
			}
			case ItemTemplate.SLOT_LR_FINGER:
			case ItemTemplate.SLOT_R_FINGER:
			{
				return PAPERDOLL_RFINGER;
			}
			case ItemTemplate.SLOT_L_FINGER:
			{
				return PAPERDOLL_LFINGER;
			}
			case ItemTemplate.SLOT_HEAD:
			{
				return PAPERDOLL_HEAD;
			}
			case ItemTemplate.SLOT_R_HAND:
			case ItemTemplate.SLOT_LR_HAND:
			{
				return PAPERDOLL_RHAND;
			}
			case ItemTemplate.SLOT_L_HAND:
			{
				return PAPERDOLL_LHAND;
			}
			case ItemTemplate.SLOT_GLOVES:
			{
				return PAPERDOLL_GLOVES;
			}
			case ItemTemplate.SLOT_CHEST:
			case ItemTemplate.SLOT_FULL_ARMOR:
			case ItemTemplate.SLOT_ALLDRESS:
			{
				return PAPERDOLL_CHEST;
			}
			case ItemTemplate.SLOT_LEGS:
			{
				return PAPERDOLL_LEGS;
			}
			case ItemTemplate.SLOT_FEET:
			{
				return PAPERDOLL_FEET;
			}
			case ItemTemplate.SLOT_BACK:
			{
				return PAPERDOLL_CLOAK;
			}
			case ItemTemplate.SLOT_HAIR:
			case ItemTemplate.SLOT_HAIRALL:
			{
				return PAPERDOLL_HAIR;
			}
			case ItemTemplate.SLOT_HAIR2:
			{
				return PAPERDOLL_HAIR2;
			}
			case ItemTemplate.SLOT_R_BRACELET:
			{
				return PAPERDOLL_RBRACELET;
			}
			case ItemTemplate.SLOT_L_BRACELET:
			{
				return PAPERDOLL_LBRACELET;
			}
			case ItemTemplate.SLOT_DECO:
			{
				return PAPERDOLL_DECO1; // return first we deal with it later
			}
			case ItemTemplate.SLOT_BELT:
			{
				return PAPERDOLL_BELT;
			}
			case ItemTemplate.SLOT_BROOCH:
			{
				return PAPERDOLL_BROOCH;
			}
			case ItemTemplate.SLOT_BROOCH_JEWEL:
			{
				return PAPERDOLL_BROOCH_JEWEL1;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the item in the paperdoll Item slot
	 * @param slot identifier
	 * @return Item
	 */
	public Item getPaperdollItemByItemId(int slot)
	{
		final int index = getPaperdollIndex(slot);
		if (index == -1)
		{
			return null;
		}
		return _paperdoll[index];
	}
	
	/**
	 * Returns the ID of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemId(int slot)
	{
		final Item item = _paperdoll[slot];
		if (item != null)
		{
			if (Config.ENABLE_TRANSMOG)
			{
				final int transmogId = item.getTransmogId();
				if (transmogId > 0)
				{
					return transmogId;
				}
			}
			
			return item.getId();
		}
		
		return 0;
	}
	
	/**
	 * Returns the ID of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemDisplayId(int slot)
	{
		final Item item = _paperdoll[slot];
		if (item != null)
		{
			if (Config.ENABLE_TRANSMOG)
			{
				final int transmogId = item.getTransmogId();
				if (transmogId > 0)
				{
					return transmogId;
				}
			}
			
			return item.getDisplayId();
		}
		
		return 0;
	}
	
	/**
	 * Returns the visual id of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemVisualId(int slot)
	{
		final Item item = _paperdoll[slot];
		return (item != null) ? item.getVisualId() : 0;
	}
	
	public VariationInstance getPaperdollAugmentation(int slot)
	{
		final Item item = _paperdoll[slot];
		return (item != null) ? item.getAugmentation() : null;
	}
	
	/**
	 * Returns the objectID associated to the item in the paperdoll slot
	 * @param slot : int pointing out the slot
	 * @return int designating the objectID
	 */
	public int getPaperdollObjectId(int slot)
	{
		final Item item = _paperdoll[slot];
		return (item != null) ? item.getObjectId() : 0;
	}
	
	/**
	 * Adds new inventory's paperdoll listener.
	 * @param listener the new listener
	 */
	public synchronized void addPaperdollListener(PaperdollListener listener)
	{
		if (!_paperdollListeners.contains(listener))
		{
			_paperdollListeners.add(listener);
		}
	}
	
	/**
	 * Removes a paperdoll listener.
	 * @param listener the listener to be deleted
	 */
	public synchronized void removePaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.remove(listener);
	}
	
	/**
	 * Equips an item in the given slot of the paperdoll.<br>
	 * <u><i>Remark :</i></u> The item <b>must be</b> in the inventory already.
	 * @param slot : int pointing out the slot of the paperdoll
	 * @param item : Item pointing out the item to add in slot
	 * @return Item designating the item placed in the slot before
	 */
	public synchronized Item setPaperdollItem(int slot, Item item)
	{
		final Creature owner = getOwner();
		final Item old = _paperdoll[slot];
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot] = null;
				_paperdollCache.getPaperdollItems().remove(old);
				
				// Put old item from paperdoll slot to base location
				old.setItemLocation(getBaseLocation());
				old.setLastChange(Item.MODIFIED);
				
				// Get the mask for paperdoll
				int mask = 0;
				for (int i = 0; i < PAPERDOLL_TOTALSLOTS; i++)
				{
					final Item pi = _paperdoll[i];
					if (pi != null)
					{
						mask |= pi.getTemplate().getItemMask();
					}
				}
				_wearedMask = mask;
				
				// Notify all paperdoll listener in order to unequip old item in slot
				for (PaperdollListener listener : _paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					
					listener.notifyUnequiped(slot, old, this);
				}
				old.updateDatabase();
			}
			
			// Add new item in slot of paperdoll
			if (item != null)
			{
				_paperdoll[slot] = item;
				_paperdollCache.getPaperdollItems().add(item);
				
				// Put item to equip location
				item.setItemLocation(getEquipLocation(), slot);
				item.setLastChange(Item.MODIFIED);
				
				// Notify all paperdoll listener in order to equip item in slot
				_wearedMask |= item.getTemplate().getItemMask();
				for (PaperdollListener listener : _paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					
					listener.notifyEquiped(slot, item, this);
				}
				item.updateDatabase();
			}
			
			_paperdollCache.clearCachedStats();
			owner.getStat().recalculateStats(!owner.isPlayer());
			
			if (owner.isPlayer())
			{
				owner.sendPacket(new ExUserInfoEquipSlot(owner.getActingPlayer()));
			}
		}
		
		if (old != null)
		{
			if ((owner != null) && owner.isPlayer())
			{
				// Proper talisman display on login.
				final Player player = owner.getActingPlayer();
				if ((slot == PAPERDOLL_RBRACELET) && !player.hasEnteredWorld())
				{
					for (ItemSkillHolder skill : old.getTemplate().getAllSkills())
					{
						player.addSkill(skill.getSkill(), false);
					}
				}
				
				// Notify to scripts.
				if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_ITEM_UNEQUIP, old.getTemplate()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemUnequip(player, old), old.getTemplate());
				}
			}
		}
		
		return old;
	}
	
	/**
	 * @return the mask of wore item
	 */
	public int getWearedMask()
	{
		return _wearedMask;
	}
	
	public int getSlotFromItem(Item item)
	{
		int slot = -1;
		final int location = item.getLocationSlot();
		switch (location)
		{
			case PAPERDOLL_UNDER:
			{
				slot = ItemTemplate.SLOT_UNDERWEAR;
				break;
			}
			case PAPERDOLL_LEAR:
			{
				slot = ItemTemplate.SLOT_L_EAR;
				break;
			}
			case PAPERDOLL_REAR:
			{
				slot = ItemTemplate.SLOT_R_EAR;
				break;
			}
			case PAPERDOLL_NECK:
			{
				slot = ItemTemplate.SLOT_NECK;
				break;
			}
			case PAPERDOLL_RFINGER:
			{
				slot = ItemTemplate.SLOT_R_FINGER;
				break;
			}
			case PAPERDOLL_LFINGER:
			{
				slot = ItemTemplate.SLOT_L_FINGER;
				break;
			}
			case PAPERDOLL_HAIR:
			{
				slot = ItemTemplate.SLOT_HAIR;
				break;
			}
			case PAPERDOLL_HAIR2:
			{
				slot = ItemTemplate.SLOT_HAIR2;
				break;
			}
			case PAPERDOLL_HEAD:
			{
				slot = ItemTemplate.SLOT_HEAD;
				break;
			}
			case PAPERDOLL_RHAND:
			{
				slot = ItemTemplate.SLOT_R_HAND;
				break;
			}
			case PAPERDOLL_LHAND:
			{
				slot = ItemTemplate.SLOT_L_HAND;
				break;
			}
			case PAPERDOLL_GLOVES:
			{
				slot = ItemTemplate.SLOT_GLOVES;
				break;
			}
			case PAPERDOLL_CHEST:
			{
				slot = item.getTemplate().getBodyPart();
				break;
			}
			case PAPERDOLL_LEGS:
			{
				slot = ItemTemplate.SLOT_LEGS;
				break;
			}
			case PAPERDOLL_CLOAK:
			{
				slot = ItemTemplate.SLOT_BACK;
				break;
			}
			case PAPERDOLL_FEET:
			{
				slot = ItemTemplate.SLOT_FEET;
				break;
			}
			case PAPERDOLL_LBRACELET:
			{
				slot = ItemTemplate.SLOT_L_BRACELET;
				break;
			}
			case PAPERDOLL_RBRACELET:
			{
				slot = ItemTemplate.SLOT_R_BRACELET;
				break;
			}
			case PAPERDOLL_DECO1:
			case PAPERDOLL_DECO2:
			case PAPERDOLL_DECO3:
			case PAPERDOLL_DECO4:
			case PAPERDOLL_DECO5:
			case PAPERDOLL_DECO6:
			{
				slot = ItemTemplate.SLOT_DECO;
				break;
			}
			case PAPERDOLL_BELT:
			{
				slot = ItemTemplate.SLOT_BELT;
				break;
			}
			case PAPERDOLL_BROOCH:
			{
				slot = ItemTemplate.SLOT_BROOCH;
				break;
			}
			case PAPERDOLL_BROOCH_JEWEL1:
			case PAPERDOLL_BROOCH_JEWEL2:
			case PAPERDOLL_BROOCH_JEWEL3:
			case PAPERDOLL_BROOCH_JEWEL4:
			case PAPERDOLL_BROOCH_JEWEL5:
			case PAPERDOLL_BROOCH_JEWEL6:
			{
				slot = ItemTemplate.SLOT_BROOCH_JEWEL;
				break;
			}
		}
		return slot;
	}
	
	/**
	 * Unequips item in body slot and returns alterations.<br>
	 * <b>If you do not need return value use {@link Inventory#unEquipItemInBodySlot(int)} instead</b>
	 * @param slot : int designating the slot of the paperdoll
	 * @return List<Item> : List of changes
	 */
	public List<Item> unEquipItemInBodySlotAndRecord(int slot)
	{
		final ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInBodySlot(slot);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Sets item in slot of the paperdoll to null value
	 * @param pdollSlot : int designating the slot
	 * @return Item designating the item in slot before change
	 */
	public Item unEquipItemInSlot(int pdollSlot)
	{
		return setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Unequips item in slot and returns alterations<br>
	 * <b>If you do not need return value use {@link Inventory#unEquipItemInSlot(int)} instead</b>
	 * @param slot : int designating the slot
	 * @return List<Item> : List of items altered
	 */
	public List<Item> unEquipItemInSlotAndRecord(int slot)
	{
		final ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInSlot(slot);
			if (getOwner().isPlayer())
			{
				((Player) getOwner()).refreshExpertisePenalty();
			}
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequips item in slot (i.e. equips with default value)
	 * @param slot : int designating the slot
	 * @return {@link Item} designating the item placed in the slot
	 */
	public Item unEquipItemInBodySlot(int slot)
	{
		int pdollSlot = -1;
		
		switch (slot)
		{
			case ItemTemplate.SLOT_L_EAR:
			{
				pdollSlot = PAPERDOLL_LEAR;
				break;
			}
			case ItemTemplate.SLOT_R_EAR:
			{
				pdollSlot = PAPERDOLL_REAR;
				break;
			}
			case ItemTemplate.SLOT_NECK:
			{
				pdollSlot = PAPERDOLL_NECK;
				break;
			}
			case ItemTemplate.SLOT_R_FINGER:
			{
				pdollSlot = PAPERDOLL_RFINGER;
				break;
			}
			case ItemTemplate.SLOT_L_FINGER:
			{
				pdollSlot = PAPERDOLL_LFINGER;
				break;
			}
			case ItemTemplate.SLOT_HAIR:
			{
				pdollSlot = PAPERDOLL_HAIR;
				break;
			}
			case ItemTemplate.SLOT_HAIR2:
			{
				pdollSlot = PAPERDOLL_HAIR2;
				break;
			}
			case ItemTemplate.SLOT_HAIRALL:
			{
				setPaperdollItem(PAPERDOLL_HAIR, null);
				pdollSlot = PAPERDOLL_HAIR;
				break;
			}
			case ItemTemplate.SLOT_HEAD:
			{
				pdollSlot = PAPERDOLL_HEAD;
				break;
			}
			case ItemTemplate.SLOT_R_HAND:
			case ItemTemplate.SLOT_LR_HAND:
			{
				pdollSlot = PAPERDOLL_RHAND;
				break;
			}
			case ItemTemplate.SLOT_L_HAND:
			{
				pdollSlot = PAPERDOLL_LHAND;
				break;
			}
			case ItemTemplate.SLOT_GLOVES:
			{
				pdollSlot = PAPERDOLL_GLOVES;
				break;
			}
			case ItemTemplate.SLOT_CHEST:
			case ItemTemplate.SLOT_ALLDRESS:
			case ItemTemplate.SLOT_FULL_ARMOR:
			{
				pdollSlot = PAPERDOLL_CHEST;
				break;
			}
			case ItemTemplate.SLOT_LEGS:
			{
				pdollSlot = PAPERDOLL_LEGS;
				break;
			}
			case ItemTemplate.SLOT_BACK:
			{
				pdollSlot = PAPERDOLL_CLOAK;
				break;
			}
			case ItemTemplate.SLOT_FEET:
			{
				pdollSlot = PAPERDOLL_FEET;
				break;
			}
			case ItemTemplate.SLOT_UNDERWEAR:
			{
				pdollSlot = PAPERDOLL_UNDER;
				break;
			}
			case ItemTemplate.SLOT_L_BRACELET:
			{
				pdollSlot = PAPERDOLL_LBRACELET;
				break;
			}
			case ItemTemplate.SLOT_R_BRACELET:
			{
				pdollSlot = PAPERDOLL_RBRACELET;
				break;
			}
			case ItemTemplate.SLOT_DECO:
			{
				pdollSlot = PAPERDOLL_DECO1;
				break;
			}
			case ItemTemplate.SLOT_BELT:
			{
				pdollSlot = PAPERDOLL_BELT;
				break;
			}
			case ItemTemplate.SLOT_BROOCH:
			{
				pdollSlot = PAPERDOLL_BROOCH;
				break;
			}
			case ItemTemplate.SLOT_BROOCH_JEWEL:
			{
				pdollSlot = PAPERDOLL_BROOCH_JEWEL1;
				break;
			}
			default:
			{
				LOGGER.info("Unhandled slot type: " + slot);
				LOGGER.info(CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
			}
		}
		if (pdollSlot >= 0)
		{
			final Item old = setPaperdollItem(pdollSlot, null);
			if ((old != null) && getOwner().isPlayer())
			{
				((Player) getOwner()).refreshExpertisePenalty();
			}
			return old;
		}
		return null;
	}
	
	/**
	 * Equips item and returns list of alterations<br>
	 * <b>If you don't need return value use {@link Inventory#equipItem(Item)} instead</b>
	 * @param item : Item corresponding to the item
	 * @return List<Item> : List of alterations
	 */
	public List<Item> equipItemAndRecord(Item item)
	{
		final ChangeRecorder recorder = newRecorder();
		try
		{
			equipItem(item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Equips item in slot of paperdoll.
	 * @param item : Item designating the item and slot used.
	 */
	public void equipItem(Item item)
	{
		if (getOwner().isPlayer())
		{
			if (((Player) getOwner()).isInStoreMode())
			{
				return;
			}
			
			// Equip only identical grade arrows.
			final EtcItem etcItem = item.getEtcItem();
			if (etcItem != null)
			{
				final Item weapon = getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				if (weapon != null)
				{
					final EtcItemType itemType = etcItem.getItemType();
					final ItemType weaponItemType = weapon.getItemType();
					if ((((weaponItemType == WeaponType.BOW) && (itemType == EtcItemType.ARROW)) //
						|| (((weaponItemType == WeaponType.CROSSBOW) || (weaponItemType == WeaponType.TWOHANDCROSSBOW)) && (itemType == EtcItemType.BOLT))) //
						&& (weapon.getTemplate().getCrystalTypePlus() != item.getTemplate().getCrystalTypePlus()))
					{
						return;
					}
				}
			}
			
			final Player player = (Player) getOwner();
			if (!player.canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem())
			{
				return;
			}
		}
		
		final int targetSlot = item.getTemplate().getBodyPart();
		
		// Check if player is using Formal Wear and item isn't Wedding Bouquet.
		final Item formal = getPaperdollItem(PAPERDOLL_CHEST);
		if ((item.getId() != 21163) && (formal != null) && (formal.getTemplate().getBodyPart() == ItemTemplate.SLOT_ALLDRESS))
		{
			// only chest target can pass this
			switch (targetSlot)
			{
				case ItemTemplate.SLOT_LR_HAND:
				case ItemTemplate.SLOT_L_HAND:
				case ItemTemplate.SLOT_R_HAND:
				case ItemTemplate.SLOT_LEGS:
				case ItemTemplate.SLOT_FEET:
				case ItemTemplate.SLOT_GLOVES:
				case ItemTemplate.SLOT_HEAD:
				{
					return;
				}
			}
		}
		
		switch (targetSlot)
		{
			case ItemTemplate.SLOT_LR_HAND:
			{
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}
			case ItemTemplate.SLOT_L_HAND:
			{
				final Item rh = getPaperdollItem(PAPERDOLL_RHAND);
				if ((rh != null) && (rh.getTemplate().getBodyPart() == ItemTemplate.SLOT_LR_HAND) && !(((rh.getItemType() == WeaponType.BOW) && (item.getItemType() == EtcItemType.ARROW)) || (((rh.getItemType() == WeaponType.CROSSBOW) || (rh.getItemType() == WeaponType.TWOHANDCROSSBOW)) && (item.getItemType() == EtcItemType.BOLT)) || ((rh.getItemType() == WeaponType.FISHINGROD) && (item.getItemType() == EtcItemType.LURE))))
				{
					setPaperdollItem(PAPERDOLL_RHAND, null);
				}
				setPaperdollItem(PAPERDOLL_LHAND, item);
				break;
			}
			case ItemTemplate.SLOT_R_HAND:
			{
				// don't care about arrows, listener will unequip them (hopefully)
				setPaperdollItem(PAPERDOLL_RHAND, item);
				break;
			}
			case ItemTemplate.SLOT_L_EAR:
			case ItemTemplate.SLOT_R_EAR:
			case ItemTemplate.SLOT_LR_EAR:
			{
				if (_paperdoll[PAPERDOLL_LEAR] == null)
				{
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				else if (_paperdoll[PAPERDOLL_REAR] == null)
				{
					setPaperdollItem(PAPERDOLL_REAR, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LEAR, item);
				}
				break;
			}
			case ItemTemplate.SLOT_L_FINGER:
			case ItemTemplate.SLOT_R_FINGER:
			case ItemTemplate.SLOT_LR_FINGER:
			{
				if (_paperdoll[PAPERDOLL_LFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				else if (_paperdoll[PAPERDOLL_RFINGER] == null)
				{
					setPaperdollItem(PAPERDOLL_RFINGER, item);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_LFINGER, item);
				}
				break;
			}
			case ItemTemplate.SLOT_NECK:
			{
				setPaperdollItem(PAPERDOLL_NECK, item);
				break;
			}
			case ItemTemplate.SLOT_FULL_ARMOR:
			{
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			}
			case ItemTemplate.SLOT_CHEST:
			{
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			}
			case ItemTemplate.SLOT_LEGS:
			{
				// handle full armor
				final Item chest = getPaperdollItem(PAPERDOLL_CHEST);
				if ((chest != null) && (chest.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR))
				{
					setPaperdollItem(PAPERDOLL_CHEST, null);
				}
				setPaperdollItem(PAPERDOLL_LEGS, item);
				break;
			}
			case ItemTemplate.SLOT_FEET:
			{
				setPaperdollItem(PAPERDOLL_FEET, item);
				break;
			}
			case ItemTemplate.SLOT_GLOVES:
			{
				setPaperdollItem(PAPERDOLL_GLOVES, item);
				break;
			}
			case ItemTemplate.SLOT_HEAD:
			{
				setPaperdollItem(PAPERDOLL_HEAD, item);
				break;
			}
			case ItemTemplate.SLOT_HAIR:
			{
				final Item hair = getPaperdollItem(PAPERDOLL_HAIR);
				if ((hair != null) && (hair.getTemplate().getBodyPart() == ItemTemplate.SLOT_HAIRALL))
				{
					setPaperdollItem(PAPERDOLL_HAIR2, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
				}
				setPaperdollItem(PAPERDOLL_HAIR, item);
				break;
			}
			case ItemTemplate.SLOT_HAIR2:
			{
				final Item hair2 = getPaperdollItem(PAPERDOLL_HAIR);
				if ((hair2 != null) && (hair2.getTemplate().getBodyPart() == ItemTemplate.SLOT_HAIRALL))
				{
					setPaperdollItem(PAPERDOLL_HAIR, null);
				}
				else
				{
					setPaperdollItem(PAPERDOLL_HAIR2, null);
				}
				setPaperdollItem(PAPERDOLL_HAIR2, item);
				break;
			}
			case ItemTemplate.SLOT_HAIRALL:
			{
				setPaperdollItem(PAPERDOLL_HAIR2, null);
				setPaperdollItem(PAPERDOLL_HAIR, item);
				break;
			}
			case ItemTemplate.SLOT_UNDERWEAR:
			{
				setPaperdollItem(PAPERDOLL_UNDER, item);
				break;
			}
			case ItemTemplate.SLOT_BACK:
			{
				setPaperdollItem(PAPERDOLL_CLOAK, item);
				break;
			}
			case ItemTemplate.SLOT_L_BRACELET:
			{
				setPaperdollItem(PAPERDOLL_LBRACELET, item);
				break;
			}
			case ItemTemplate.SLOT_R_BRACELET:
			{
				setPaperdollItem(PAPERDOLL_RBRACELET, item);
				break;
			}
			case ItemTemplate.SLOT_DECO:
			{
				equipTalisman(item);
				break;
			}
			case ItemTemplate.SLOT_BELT:
			{
				setPaperdollItem(PAPERDOLL_BELT, item);
				break;
			}
			case ItemTemplate.SLOT_ALLDRESS:
			{
				// formal dress
				setPaperdollItem(PAPERDOLL_LEGS, null);
				setPaperdollItem(PAPERDOLL_LHAND, null);
				setPaperdollItem(PAPERDOLL_RHAND, null);
				setPaperdollItem(PAPERDOLL_HEAD, null);
				setPaperdollItem(PAPERDOLL_FEET, null);
				setPaperdollItem(PAPERDOLL_GLOVES, null);
				setPaperdollItem(PAPERDOLL_CHEST, item);
				break;
			}
			case ItemTemplate.SLOT_BROOCH:
			{
				setPaperdollItem(PAPERDOLL_BROOCH, item);
				break;
			}
			case ItemTemplate.SLOT_BROOCH_JEWEL:
			{
				equipBroochJewel(item);
				break;
			}
			default:
			{
				LOGGER.warning("Unknown body slot " + targetSlot + " for Item ID: " + item.getId());
			}
		}
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	protected void refreshWeight()
	{
		long weight = 0;
		for (Item item : _items)
		{
			if ((item != null) && (item.getTemplate() != null))
			{
				weight += item.getTemplate().getWeight() * item.getCount();
			}
		}
		_totalWeight = (int) Math.min(weight, Integer.MAX_VALUE);
	}
	
	/**
	 * @return the totalWeight.
	 */
	public int getTotalWeight()
	{
		return _totalWeight;
	}
	
	/**
	 * Return the Item of the arrows needed for this bow.
	 * @param bow : Item designating the bow
	 * @return Item pointing out arrows for bow
	 */
	public Item findArrowForBow(ItemTemplate bow)
	{
		if (bow == null)
		{
			return null;
		}
		
		Item arrow = null;
		for (Item item : _items)
		{
			if (item.isEtcItem() && (item.getEtcItem().getItemType() == EtcItemType.ARROW) && (item.getTemplate().getCrystalTypePlus() == bow.getCrystalTypePlus()))
			{
				arrow = item;
				break;
			}
		}
		
		// Get the Item corresponding to the item identifier and return it
		return arrow;
	}
	
	/**
	 * Return the Item of the bolts needed for this crossbow.
	 * @param crossbow : Item designating the crossbow
	 * @return Item pointing out bolts for crossbow
	 */
	public Item findBoltForCrossBow(ItemTemplate crossbow)
	{
		Item bolt = null;
		for (Item item : _items)
		{
			if (item.isEtcItem() && (item.getEtcItem().getItemType() == EtcItemType.BOLT) && (item.getTemplate().getCrystalTypePlus() == crossbow.getCrystalTypePlus()))
			{
				bolt = item;
				break;
			}
		}
		
		// Get the Item corresponding to the item identifier and return it
		return bolt;
	}
	
	/**
	 * Get back items in inventory from database
	 */
	@Override
	public void restore()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY loc_data"))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			ps.setString(3, getEquipLocation().name());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					try
					{
						final Item item = new Item(rs);
						if (getOwner().isPlayer())
						{
							final Player player = (Player) getOwner();
							if (!player.canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem())
							{
								item.setItemLocation(ItemLocation.INVENTORY);
							}
						}
						
						World.getInstance().addObject(item);
						
						// If stackable item is found in inventory just add to current quantity
						if (item.isStackable() && (getItemByItemId(item.getId()) != null))
						{
							addItem("Restore", item, getOwner().getActingPlayer(), null);
						}
						else
						{
							addItem(item);
						}
					}
					catch (Exception e)
					{
						LOGGER.warning("Could not restore item " + rs.getInt("item_id") + " for " + getOwner());
					}
				}
			}
			refreshWeight();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore inventory: " + e.getMessage(), e);
		}
	}
	
	public int getTalismanSlots()
	{
		return getOwner().getActingPlayer().getStat().getTalismanSlots();
	}
	
	private void equipTalisman(Item item)
	{
		if (getTalismanSlots() == 0)
		{
			return;
		}
		
		// find same (or incompatible) talisman type
		for (int i = PAPERDOLL_DECO1; i < (PAPERDOLL_DECO1 + getTalismanSlots()); i++)
		{
			if ((_paperdoll[i] != null) && (getPaperdollItemId(i) == item.getId()))
			{
				// overwrite
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slot found - put on first free
		for (int i = PAPERDOLL_DECO1; i < (PAPERDOLL_DECO1 + getTalismanSlots()); i++)
		{
			if (_paperdoll[i] == null)
			{
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slots - put on first
		setPaperdollItem(PAPERDOLL_DECO1, item);
	}
	
	public int getBroochJewelSlots()
	{
		return getOwner().getActingPlayer().getStat().getBroochJewelSlots();
	}
	
	private void equipBroochJewel(Item item)
	{
		if (getBroochJewelSlots() == 0)
		{
			return;
		}
		
		// find same (or incompatible) brooch jewel type
		for (int i = PAPERDOLL_BROOCH_JEWEL1; i < (PAPERDOLL_BROOCH_JEWEL1 + getBroochJewelSlots()); i++)
		{
			if ((_paperdoll[i] != null) && (getPaperdollItemId(i) == item.getId()))
			{
				// overwtite
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slot found - put on first free
		for (int i = PAPERDOLL_BROOCH_JEWEL1; i < (PAPERDOLL_BROOCH_JEWEL1 + getBroochJewelSlots()); i++)
		{
			if (_paperdoll[i] == null)
			{
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slots - put on first
		setPaperdollItem(PAPERDOLL_BROOCH_JEWEL1, item);
	}
	
	public boolean canEquipCloak()
	{
		return getOwner().getActingPlayer().getStat().canEquipCloak();
	}
	
	/**
	 * Re-notify to paperdoll listeners every equipped item.<br>
	 * Only used by player ClassId set methods.
	 */
	public void reloadEquippedItems()
	{
		int slot;
		for (Item item : _paperdoll)
		{
			if (item == null)
			{
				continue;
			}
			
			slot = item.getLocationSlot();
			for (PaperdollListener listener : _paperdollListeners)
			{
				if (listener == null)
				{
					continue;
				}
				
				listener.notifyUnequiped(slot, item, this);
				listener.notifyEquiped(slot, item, this);
			}
		}
		
		if (getOwner().isPlayer())
		{
			getOwner().sendPacket(new ExUserInfoEquipSlot(getOwner().getActingPlayer()));
		}
	}
	
	public int getArmorSetEnchant()
	{
		final Creature creature = getOwner();
		if ((creature == null) || !creature.isPlayer())
		{
			return 0;
		}
		
		return _paperdollCache.getArmorSetEnchant(creature.getActingPlayer());
	}
	
	public int getWeaponEnchant()
	{
		final Item item = getPaperdollItem(PAPERDOLL_RHAND);
		return item != null ? item.getEnchantLevel() : 0;
	}
	
	/**
	 * Blocks the given item slot from being equipped.
	 * @param itemSlot mask from Item
	 */
	public void blockItemSlot(int itemSlot)
	{
		_blockedItemSlotsMask |= itemSlot;
	}
	
	/**
	 * Unblocks the given item slot so it can be equipped.
	 * @param itemSlot mask from Item
	 */
	public void unblockItemSlot(int itemSlot)
	{
		_blockedItemSlotsMask &= ~itemSlot;
	}
	
	/**
	 * @param itemSlot mask from Item
	 * @return if the given item slot is blocked or not.
	 */
	public boolean isItemSlotBlocked(int itemSlot)
	{
		return (_blockedItemSlotsMask & itemSlot) == itemSlot;
	}
	
	/**
	 * @param itemSlotsMask use 0 to unset all blocked item slots.
	 */
	public void setBlockedItemSlotsMask(int itemSlotsMask)
	{
		_blockedItemSlotsMask = itemSlotsMask;
	}
	
	/**
	 * Reduce the arrow number of the Creature.<br>
	 * <br>
	 * <b><u>Overridden in</u>:</b>
	 * <li>Player</li><br>
	 * @param type
	 */
	public void reduceArrowCount(EtcItemType type)
	{
		// default is to do nothing
	}
	
	/**
	 * Gets the items in paperdoll slots filtered by filter.
	 * @param filters multiple filters
	 * @return the filtered items in inventory
	 */
	@SafeVarargs
	public final Collection<Item> getPaperdollItems(Predicate<Item>... filters)
	{
		if (filters.length == 0)
		{
			return _paperdollCache.getPaperdollItems();
		}
		
		Predicate<Item> filter = Objects::nonNull;
		for (Predicate<Item> additionalFilter : filters)
		{
			filter = filter.and(additionalFilter);
		}
		
		final List<Item> items = new LinkedList<>();
		for (Item item : _paperdoll)
		{
			if (filter.test(item))
			{
				items.add(item);
			}
		}
		return items;
	}
	
	@SafeVarargs
	public final int getPaperdollItemCount(Predicate<Item>... filters)
	{
		if (filters.length == 0)
		{
			return _paperdollCache.getPaperdollItems().size();
		}
		
		Predicate<Item> filter = Objects::nonNull;
		for (Predicate<Item> additionalFilter : filters)
		{
			filter = filter.and(additionalFilter);
		}
		
		int count = 0;
		for (Item item : _paperdoll)
		{
			if (filter.test(item))
			{
				count++;
			}
		}
		return count;
	}
	
	public PaperdollCache getPaperdollCache()
	{
		return _paperdollCache;
	}
}
