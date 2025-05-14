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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.ArmorSet;
import org.l2jpx.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.stats.BaseStat;

/**
 * Loads armor set bonuses.
 * @author godson, Luno, UnAfraid
 */
public class ArmorSetData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ArmorSetData.class.getName());
	
	private ArmorSet[] _armorSets;
	private final Map<Integer, ArmorSet> _armorSetMap = new HashMap<>();
	private List<ArmorSet>[] _itemSets;
	private final Map<Integer, List<ArmorSet>> _armorSetItems = new HashMap<>();
	
	protected ArmorSetData()
	{
		load();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void load()
	{
		parseDatapackDirectory("data/stats/armorsets", false);
		
		_armorSets = new ArmorSet[Collections.max(_armorSetMap.keySet()) + 1];
		for (Entry<Integer, ArmorSet> armorSet : _armorSetMap.entrySet())
		{
			_armorSets[armorSet.getKey()] = armorSet.getValue();
		}
		
		_itemSets = new ArrayList[Collections.max(_armorSetItems.keySet()) + 1];
		for (Entry<Integer, List<ArmorSet>> armorSet : _armorSetItems.entrySet())
		{
			_itemSets[armorSet.getKey()] = armorSet.getValue();
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _armorSetMap.size() + " armor sets.");
		_armorSetMap.clear();
		_armorSetItems.clear();
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node setNode = n.getFirstChild(); setNode != null; setNode = setNode.getNextSibling())
				{
					if ("set".equalsIgnoreCase(setNode.getNodeName()))
					{
						final int id = parseInteger(setNode.getAttributes(), "id");
						final int minimumPieces = parseInteger(setNode.getAttributes(), "minimumPieces", 0);
						final boolean isVisual = parseBoolean(setNode.getAttributes(), "visual", false);
						final Set<Integer> requiredItems = new LinkedHashSet<>();
						final Set<Integer> optionalItems = new LinkedHashSet<>();
						final List<ArmorsetSkillHolder> skills = new ArrayList<>();
						final Map<BaseStat, Double> stats = new LinkedHashMap<>();
						for (Node innerSetNode = setNode.getFirstChild(); innerSetNode != null; innerSetNode = innerSetNode.getNextSibling())
						{
							switch (innerSetNode.getNodeName())
							{
								case "requiredItems":
								{
									forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node ->
									{
										final NamedNodeMap attrs = node.getAttributes();
										final int itemId = parseInteger(attrs, "id");
										final ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
										if (item == null)
										{
											LOGGER.warning("Attempting to register non existing required item: " + itemId + " to a set: " + f.getName());
										}
										else if (!requiredItems.add(itemId))
										{
											LOGGER.warning("Attempting to register duplicate required item " + item + " to a set: " + f.getName());
										}
									});
									break;
								}
								case "optionalItems":
								{
									forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node ->
									{
										final NamedNodeMap attrs = node.getAttributes();
										final int itemId = parseInteger(attrs, "id");
										final ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
										if (item == null)
										{
											LOGGER.warning("Attempting to register non existing optional item: " + itemId + " to a set: " + f.getName());
										}
										else if (!optionalItems.add(itemId))
										{
											LOGGER.warning("Attempting to register duplicate optional item " + item + " to a set: " + f.getName());
										}
									});
									break;
								}
								case "skills":
								{
									forEach(innerSetNode, b -> "skill".equals(b.getNodeName()), node ->
									{
										final NamedNodeMap attrs = node.getAttributes();
										final int skillId = parseInteger(attrs, "id");
										final int skillLevel = parseInteger(attrs, "level");
										final int minPieces = parseInteger(attrs, "minimumPieces", minimumPieces);
										final int minEnchant = parseInteger(attrs, "minimumEnchant", 0);
										final boolean isOptional = parseBoolean(attrs, "optional", false);
										skills.add(new ArmorsetSkillHolder(skillId, skillLevel, minPieces, minEnchant, isOptional));
									});
									break;
								}
								case "stats":
								{
									forEach(innerSetNode, b -> "stat".equals(b.getNodeName()), node ->
									{
										final NamedNodeMap attrs = node.getAttributes();
										stats.put(parseEnum(attrs, BaseStat.class, "type"), parseDouble(attrs, "val"));
									});
									break;
								}
							}
						}
						
						final ArmorSet set = new ArmorSet(id, minimumPieces, isVisual, requiredItems, optionalItems, skills, stats);
						if (_armorSetMap.putIfAbsent(id, set) != null)
						{
							LOGGER.warning("Duplicate set entry with id: " + id + " in file: " + f.getName());
						}
						
						Stream.concat(Arrays.stream(set.getRequiredItems()).boxed(), Arrays.stream(set.getOptionalItems()).boxed()).forEach(itemHolder -> _armorSetItems.computeIfAbsent(itemHolder, key -> new ArrayList<>()).add(set));
					}
				}
			}
		}
	}
	
	/**
	 * @param setId the set id that is attached to a set
	 * @return the armor set associated to the given item id
	 */
	public ArmorSet getSet(int setId)
	{
		if (_armorSets.length > setId)
		{
			return _armorSets[setId];
		}
		return null;
	}
	
	/**
	 * @param itemId the item id that is attached to a set
	 * @return the armor set associated to the given item id
	 */
	public List<ArmorSet> getSets(int itemId)
	{
		if (_itemSets.length > itemId)
		{
			final List<ArmorSet> sets = _itemSets[itemId];
			if (sets != null)
			{
				return sets;
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * Gets the single instance of ArmorSetsData
	 * @return single instance of ArmorSetsData
	 */
	public static ArmorSetData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ArmorSetData INSTANCE = new ArmorSetData();
	}
}
