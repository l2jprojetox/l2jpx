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
import org.w3c.dom.Node;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.options.EnchantOptions;
import org.l2jpx.gameserver.util.Util;

/**
 * @author UnAfraid, Mobius
 */
public class EnchantItemOptionsData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(EnchantItemOptionsData.class.getName());
	
	private final Map<Integer, Map<Integer, EnchantOptions>> _data = new HashMap<>();
	
	protected EnchantItemOptionsData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_data.clear();
		parseDatapackFile("data/EnchantItemOptions.xml");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		int counter = 0;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				ITEM: for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("item".equalsIgnoreCase(d.getNodeName()))
					{
						final int itemId = parseInteger(d.getAttributes(), "id");
						final ItemTemplate template = ItemData.getInstance().getTemplate(itemId);
						if (template == null)
						{
							LOGGER.warning(getClass().getSimpleName() + ": Could not find item template for id " + itemId);
							continue ITEM;
						}
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("options".equalsIgnoreCase(cd.getNodeName()))
							{
								final EnchantOptions op = new EnchantOptions(parseInteger(cd.getAttributes(), "level"));
								for (byte i = 0; i < 3; i++)
								{
									final Node att = cd.getAttributes().getNamedItem("option" + (i + 1));
									if ((att != null) && Util.isDigit(att.getNodeValue()))
									{
										final int id = parseInteger(att);
										if (OptionData.getInstance().getOptions(id) == null)
										{
											LOGGER.warning(getClass().getSimpleName() + ": Could not find option " + id + " for item " + template);
											continue ITEM;
										}
										
										Map<Integer, EnchantOptions> data = _data.get(itemId);
										if (data == null)
										{
											data = new HashMap<>();
											_data.put(itemId, data);
										}
										if (!data.containsKey(op.getLevel()))
										{
											data.put(op.getLevel(), op);
										}
										
										op.setOption(i, id);
									}
								}
								counter++;
							}
						}
					}
				}
			}
		}
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _data.size() + " items and " + counter + " options.");
	}
	
	/**
	 * @param itemId
	 * @return if specified item id has available enchant effects.
	 */
	public boolean hasOptions(int itemId)
	{
		return _data.containsKey(itemId);
	}
	
	/**
	 * @param itemId
	 * @param enchantLevel
	 * @return enchant effects information.
	 */
	public EnchantOptions getOptions(int itemId, int enchantLevel)
	{
		if (!_data.containsKey(itemId) || !_data.get(itemId).containsKey(enchantLevel))
		{
			return null;
		}
		return _data.get(itemId).get(enchantLevel);
	}
	
	/**
	 * @param item
	 * @return enchant effects information.
	 */
	public EnchantOptions getOptions(Item item)
	{
		return item != null ? getOptions(item.getId(), item.getEnchantLevel()) : null;
	}
	
	/**
	 * Gets the single instance of EnchantOptionsData.
	 * @return single instance of EnchantOptionsData
	 */
	public static EnchantItemOptionsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantItemOptionsData INSTANCE = new EnchantItemOptionsData();
	}
}
