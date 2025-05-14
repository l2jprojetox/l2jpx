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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jpx.Config;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.holders.RangeChanceHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.enchant.EnchantItemGroup;
import org.l2jpx.gameserver.model.item.enchant.EnchantRateItem;
import org.l2jpx.gameserver.model.item.enchant.EnchantScrollGroup;
import org.l2jpx.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class EnchantItemGroupsData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(EnchantItemGroupsData.class.getName());
	
	private final Map<String, EnchantItemGroup> _itemGroups = new HashMap<>();
	private final Map<Integer, EnchantScrollGroup> _scrollGroups = new HashMap<>();
	private int _maxWeaponEnchant = 0;
	private int _maxArmorEnchant = 0;
	private int _maxAccessoryEnchant = 0;
	
	protected EnchantItemGroupsData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_itemGroups.clear();
		_scrollGroups.clear();
		parseDatapackFile("data/EnchantItemGroups.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _itemGroups.size() + " item group templates.");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _scrollGroups.size() + " scroll group templates.");
		
		if (Config.OVER_ENCHANT_PROTECTION)
		{
			LOGGER.info(getClass().getSimpleName() + ": Max weapon enchant is set to " + _maxWeaponEnchant + ".");
			LOGGER.info(getClass().getSimpleName() + ": Max armor enchant is set to " + _maxArmorEnchant + ".");
			LOGGER.info(getClass().getSimpleName() + ": Max accessory enchant is set to " + _maxAccessoryEnchant + ".");
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("enchantRateGroup".equalsIgnoreCase(d.getNodeName()))
					{
						final String name = parseString(d.getAttributes(), "name");
						final EnchantItemGroup group = new EnchantItemGroup(name);
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("current".equalsIgnoreCase(cd.getNodeName()))
							{
								final String range = parseString(cd.getAttributes(), "enchant");
								final double chance = parseDouble(cd.getAttributes(), "chance");
								int min = -1;
								int max = 0;
								if (range.contains("-"))
								{
									final String[] split = range.split("-");
									if ((split.length == 2) && Util.isDigit(split[0]) && Util.isDigit(split[1]))
									{
										min = Integer.parseInt(split[0]);
										max = Integer.parseInt(split[1]);
									}
								}
								else if (Util.isDigit(range))
								{
									min = Integer.parseInt(range);
									max = min;
								}
								if ((min > -1) && (max > -1))
								{
									group.addChance(new RangeChanceHolder(min, max, chance));
								}
								
								// Try to get a generic max value.
								if (chance > 0)
								{
									if (name.contains("WEAPON"))
									{
										if (_maxWeaponEnchant < max)
										{
											_maxWeaponEnchant = max;
										}
									}
									else if (name.contains("ACCESSORIES") || name.contains("RING") || name.contains("EARRING") || name.contains("NECK"))
									{
										if (_maxAccessoryEnchant < max)
										{
											_maxAccessoryEnchant = max;
										}
									}
									else if (_maxArmorEnchant < max)
									{
										_maxArmorEnchant = max;
									}
								}
							}
						}
						_itemGroups.put(name, group);
					}
					else if ("enchantScrollGroup".equals(d.getNodeName()))
					{
						final int id = parseInteger(d.getAttributes(), "id");
						final EnchantScrollGroup group = new EnchantScrollGroup(id);
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("enchantRate".equalsIgnoreCase(cd.getNodeName()))
							{
								final EnchantRateItem rateGroup = new EnchantRateItem(parseString(cd.getAttributes(), "group"));
								for (Node z = cd.getFirstChild(); z != null; z = z.getNextSibling())
								{
									if ("item".equals(z.getNodeName()))
									{
										final NamedNodeMap attrs = z.getAttributes();
										if (attrs.getNamedItem("slot") != null)
										{
											rateGroup.addSlot(ItemData.SLOTS.get(parseString(attrs, "slot")));
										}
										if (attrs.getNamedItem("magicWeapon") != null)
										{
											rateGroup.setMagicWeapon(parseBoolean(attrs, "magicWeapon"));
										}
										if (attrs.getNamedItem("itemId") != null)
										{
											rateGroup.addItemId(parseInteger(attrs, "itemId"));
										}
									}
								}
								group.addRateGroup(rateGroup);
							}
						}
						_scrollGroups.put(id, group);
					}
				}
			}
		}
		
		// In case there is no accessories group set.
		if (_maxAccessoryEnchant == 0)
		{
			_maxAccessoryEnchant = _maxArmorEnchant;
		}
		
		// Max enchant values are set to current max enchant + 1.
		_maxWeaponEnchant += 1;
		_maxArmorEnchant += 1;
		_maxAccessoryEnchant += 1;
	}
	
	public EnchantItemGroup getItemGroup(ItemTemplate item, int scrollGroup)
	{
		final EnchantScrollGroup group = _scrollGroups.get(scrollGroup);
		final EnchantRateItem rateGroup = group.getRateGroup(item);
		return rateGroup != null ? _itemGroups.get(rateGroup.getName()) : null;
	}
	
	public EnchantItemGroup getItemGroup(String name)
	{
		return _itemGroups.get(name);
	}
	
	public EnchantScrollGroup getScrollGroup(int id)
	{
		return _scrollGroups.get(id);
	}
	
	public int getMaxWeaponEnchant()
	{
		return _maxWeaponEnchant;
	}
	
	public int getMaxArmorEnchant()
	{
		return _maxArmorEnchant;
	}
	
	public int getMaxAccessoryEnchant()
	{
		return _maxAccessoryEnchant;
	}
	
	public static EnchantItemGroupsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantItemGroupsData INSTANCE = new EnchantItemGroupsData();
	}
}
