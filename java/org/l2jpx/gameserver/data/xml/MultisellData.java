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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jpx.Config;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.commons.util.file.filter.NumericNameFilter;
import org.l2jpx.gameserver.enums.SpecialItemType;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemChanceHolder;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.holders.MultisellEntryHolder;
import org.l2jpx.gameserver.model.holders.MultisellListHolder;
import org.l2jpx.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.enchant.EnchantItemGroup;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.network.serverpackets.MultiSellList;

public class MultisellData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(MultisellData.class.getName());
	
	public static final int PAGE_SIZE = 40;
	private static final FileFilter NUMERIC_FILTER = new NumericNameFilter();
	
	private final Map<Integer, MultisellListHolder> _multisells = new ConcurrentHashMap<>();
	
	protected MultisellData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_multisells.clear();
		parseDatapackDirectory("data/multisell", false);
		if (Config.CUSTOM_MULTISELL_LOAD)
		{
			parseDatapackDirectory("data/multisell/custom", false);
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _multisells.size() + " multisell lists.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		final EnchantItemGroup magicWeaponGroup = EnchantItemGroupsData.getInstance().getItemGroup("MAGE_WEAPON_GROUP");
		final int magicWeaponGroupMax = magicWeaponGroup != null ? magicWeaponGroup.getMaximumEnchant() : -2;
		final EnchantItemGroup weapongroup = EnchantItemGroupsData.getInstance().getItemGroup("FIGHTER_WEAPON_GROUP");
		final int weaponGroupMax = weapongroup != null ? weapongroup.getMaximumEnchant() : -2;
		final EnchantItemGroup fullArmorGroup = EnchantItemGroupsData.getInstance().getItemGroup("FULL_ARMOR_GROUP");
		final int fullArmorGroupMax = fullArmorGroup != null ? fullArmorGroup.getMaximumEnchant() : -2;
		final EnchantItemGroup armorGroup = EnchantItemGroupsData.getInstance().getItemGroup("ARMOR_GROUP");
		final int armorGroupMax = armorGroup != null ? armorGroup.getMaximumEnchant() : -2;
		
		try
		{
			forEach(doc, "list", listNode ->
			{
				final StatSet set = new StatSet(parseAttributes(listNode));
				final int listId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
				final List<MultisellEntryHolder> entries = new ArrayList<>(listNode.getChildNodes().getLength());
				final AtomicInteger entryCounter = new AtomicInteger();
				
				forEach(listNode, itemNode ->
				{
					if ("item".equalsIgnoreCase(itemNode.getNodeName()))
					{
						long totalPrice = 0;
						int lastIngredientId = 0;
						long lastIngredientCount = 0;
						entryCounter.incrementAndGet();
						
						final List<ItemChanceHolder> ingredients = new ArrayList<>(1);
						final List<ItemChanceHolder> products = new ArrayList<>(1);
						final MultisellEntryHolder entry = new MultisellEntryHolder(ingredients, products);
						for (Node d = itemNode.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("ingredient".equalsIgnoreCase(d.getNodeName()))
							{
								final int id = parseInteger(d.getAttributes(), "id");
								final long count = parseLong(d.getAttributes(), "count");
								final byte enchantmentLevel = parseByte(d.getAttributes(), "enchantmentLevel", (byte) 0);
								final Boolean maintainIngredient = parseBoolean(d.getAttributes(), "maintainIngredient", false);
								final ItemChanceHolder ingredient = new ItemChanceHolder(id, 0, count, enchantmentLevel, maintainIngredient);
								if (itemExists(ingredient))
								{
									ingredients.add(ingredient);
									
									lastIngredientId = id;
									lastIngredientCount = count;
								}
								else
								{
									LOGGER.warning("Invalid ingredient id or count for itemId: " + ingredient.getId() + ", count: " + ingredient.getCount() + " in list: " + listId);
									continue;
								}
							}
							else if ("production".equalsIgnoreCase(d.getNodeName()))
							{
								final int id = parseInteger(d.getAttributes(), "id");
								final long count = parseLong(d.getAttributes(), "count");
								final double chance = parseDouble(d.getAttributes(), "chance", Double.NaN);
								byte enchantmentLevel = parseByte(d.getAttributes(), "enchantmentLevel", (byte) 0);
								if (enchantmentLevel > 0)
								{
									final ItemTemplate item = ItemData.getInstance().getTemplate(id);
									if (item != null)
									{
										if (item.isWeapon())
										{
											enchantmentLevel = (byte) Math.min(enchantmentLevel, item.isMagicWeapon() ? magicWeaponGroupMax > -2 ? magicWeaponGroupMax : enchantmentLevel : weaponGroupMax > -2 ? weaponGroupMax : enchantmentLevel);
										}
										else if (item.isArmor())
										{
											enchantmentLevel = (byte) Math.min(enchantmentLevel, item.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? fullArmorGroupMax > -2 ? fullArmorGroupMax : enchantmentLevel : armorGroupMax > -2 ? armorGroupMax : enchantmentLevel);
										}
									}
								}
								
								final ItemChanceHolder product = new ItemChanceHolder(id, chance, count, enchantmentLevel);
								if (itemExists(product))
								{
									// Check chance only of items that have set chance. Items without chance (NaN) are used for displaying purposes.
									if ((!Double.isNaN(chance) && (chance < 0)) || (chance > 100))
									{
										LOGGER.warning("Invalid chance for itemId: " + product.getId() + ", count: " + product.getCount() + ", chance: " + chance + " in list: " + listId);
										continue;
									}
									
									// Max equipable item grade configuration.
									final ItemTemplate item = ItemData.getInstance().getTemplate(id);
									if (item != null)
									{
										final int itemCrystalLevel = item.getCrystalType().getLevel();
										if ((itemCrystalLevel > Config.MAX_EQUIPABLE_ITEM_GRADE.getLevel()) && (itemCrystalLevel < CrystalType.EVENT.getLevel()))
										{
											continue;
										}
										
										if (chance > 0)
										{
											totalPrice += ((item.getReferencePrice() / 2) * count) * (chance / 100);
										}
										else
										{
											totalPrice += ((item.getReferencePrice() / 2) * count);
										}
									}
									
									products.add(product);
								}
								else
								{
									LOGGER.warning("Invalid product id or count for itemId: " + product.getId() + ", count: " + product.getCount() + " in list: " + listId);
									continue;
								}
							}
						}
						
						final double totalChance = products.stream().filter(i -> !Double.isNaN(i.getChance())).mapToDouble(ItemChanceHolder::getChance).sum();
						if (totalChance > 100)
						{
							LOGGER.warning("Products' total chance of " + totalChance + "% exceeds 100% for list: " + listId + " at entry " + entries.size() + 1 + ".");
						}
						
						// Check if buy price is lower than sell price.
						// Only applies when there is only one ingredient and it is adena.
						if (Config.CORRECT_PRICES && (ingredients.size() == 1) && (lastIngredientId == 57) && (lastIngredientCount < totalPrice))
						{
							LOGGER.warning("Buy price " + lastIngredientCount + " is less than sell price " + totalPrice + " at entry " + entryCounter.intValue() + " of multisell " + listId + ".");
							// Adjust price.
							final ItemChanceHolder ingredient = new ItemChanceHolder(57, 0, totalPrice, (byte) 0, ingredients.get(0).isMaintainIngredient());
							ingredients.clear();
							ingredients.add(ingredient);
						}
						
						entries.add(entry);
					}
					else if ("npcs".equalsIgnoreCase(itemNode.getNodeName()))
					{
						// Initialize NPCs with the size of child nodes.
						final Set<Integer> allowNpc = new HashSet<>(itemNode.getChildNodes().getLength());
						forEach(itemNode, n -> "npc".equalsIgnoreCase(n.getNodeName()), n -> allowNpc.add(Integer.parseInt(n.getTextContent())));
						
						// Add npcs to stats set.
						set.set("allowNpc", allowNpc);
					}
				});
				
				set.set("listId", listId);
				set.set("entries", entries);
				_multisells.put(listId, new MultisellListHolder(set));
			});
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error in file " + f, e);
		}
	}
	
	@Override
	public FileFilter getCurrentFileFilter()
	{
		return NUMERIC_FILTER;
	}
	
	/**
	 * This will generate the multisell list for the items.<br>
	 * There exist various parameters in multisells that affect the way they will appear:
	 * <ol>
	 * <li>Inventory only:
	 * <ul>
	 * <li>If true, only show items of the multisell for which the "primary" ingredients are already in the player's inventory. By "primary" ingredients we mean weapon and armor.</li>
	 * <li>If false, show the entire list.</li>
	 * </ul>
	 * </li>
	 * <li>Maintain enchantment: presumably, only lists with "inventory only" set to true should sometimes have this as true. This makes no sense otherwise...
	 * <ul>
	 * <li>If true, then the product will match the enchantment level of the ingredient.<br>
	 * If the player has multiple items that match the ingredient list but the enchantment levels differ, then the entries need to be duplicated to show the products and ingredients for each enchantment level.<br>
	 * For example: If the player has a crystal staff +1 and a crystal staff +3 and goes to exchange it at the mammon, the list should have all exchange possibilities for the +1 staff, followed by all possibilities for the +3 staff.</li>
	 * <li>If false, then any level ingredient will be considered equal and product will always be at +0</li>
	 * </ul>
	 * </li>
	 * <li>Apply taxes: Uses the "taxIngredient" entry in order to add a certain amount of adena to the ingredients.
	 * <li>
	 * <li>Additional product and ingredient multipliers.</li>
	 * </ol>
	 * @param listId
	 * @param player
	 * @param npc
	 * @param inventoryOnly
	 * @param ingredientMultiplierValue
	 * @param productMultiplierValue
	 */
	public void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly, double ingredientMultiplierValue, double productMultiplierValue)
	{
		final MultisellListHolder template = _multisells.get(listId);
		if (template == null)
		{
			LOGGER.warning("Can't find list id: " + listId + " requested by player: " + player.getName() + ", npcId: " + (npc != null ? npc.getId() : 0));
			return;
		}
		
		if (!template.isNpcAllowed(-1) && (((npc != null) && !template.isNpcAllowed(npc.getId())) || ((npc == null) && template.isNpcOnly())))
		{
			if (player.isGM())
			{
				player.sendMessage("Multisell " + listId + " is restricted. Under current conditions cannot be used. Only GMs are allowed to use it.");
			}
			else
			{
				LOGGER.warning(getClass().getSimpleName() + ": " + player + " attempted to open multisell " + listId + " from npc " + npc + " which is not allowed!");
				return;
			}
		}
		
		// Check if ingredient/product multipliers are set, if not, set them to the template value.
		final double ingredientMultiplier = (Double.isNaN(ingredientMultiplierValue) ? template.getIngredientMultiplier() : ingredientMultiplierValue);
		final double productMultiplier = (Double.isNaN(productMultiplierValue) ? template.getProductMultiplier() : productMultiplierValue);
		final PreparedMultisellListHolder list = new PreparedMultisellListHolder(template, inventoryOnly, player.getInventory(), npc, ingredientMultiplier, productMultiplier);
		int index = 0;
		do
		{
			// send list at least once even if size = 0
			player.sendPacket(new MultiSellList(player, list, index));
			index += PAGE_SIZE;
		}
		while (index < list.getEntries().size());
		
		player.setMultiSell(list);
	}
	
	public void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly)
	{
		separateAndSend(listId, player, npc, inventoryOnly, Double.NaN, Double.NaN);
	}
	
	private final boolean itemExists(ItemHolder holder)
	{
		final SpecialItemType specialItem = SpecialItemType.getByClientId(holder.getId());
		if (specialItem != null)
		{
			return true;
		}
		
		final ItemTemplate template = ItemData.getInstance().getTemplate(holder.getId());
		return (template != null) && (template.isStackable() ? (holder.getCount() >= 1) : (holder.getCount() == 1));
	}
	
	public static MultisellData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MultisellData INSTANCE = new MultisellData();
	}
}
