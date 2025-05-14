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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.l2jpx.Config;
import org.l2jpx.commons.database.DatabaseFactory;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.commons.util.file.filter.NumericNameFilter;
import org.l2jpx.gameserver.model.buylist.Product;
import org.l2jpx.gameserver.model.buylist.ProductList;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.type.CrystalType;

/**
 * Loads buy lists for NPCs.
 * @author NosBit
 */
public class BuyListData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(BuyListData.class.getName());
	
	private final Map<Integer, ProductList> _buyLists = new ConcurrentHashMap<>();
	private static final FileFilter NUMERIC_FILTER = new NumericNameFilter();
	
	protected BuyListData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_buyLists.clear();
		parseDatapackDirectory("data/buylists", false);
		if (Config.CUSTOM_BUYLIST_LOAD)
		{
			parseDatapackDirectory("data/buylists/custom", false);
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _buyLists.size() + " buyLists.");
		
		try (Connection con = DatabaseFactory.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM `buylists`"))
		{
			while (rs.next())
			{
				final int buyListId = rs.getInt("buylist_id");
				final int itemId = rs.getInt("item_id");
				final long count = rs.getLong("count");
				final long nextRestockTime = rs.getLong("next_restock_time");
				final ProductList buyList = getBuyList(buyListId);
				if (buyList == null)
				{
					LOGGER.warning("BuyList found in database but not loaded from xml! BuyListId: " + buyListId);
					continue;
				}
				final Product product = buyList.getProductByItemId(itemId);
				if (product == null)
				{
					LOGGER.warning("ItemId found in database but not loaded from xml! BuyListId: " + buyListId + " ItemId: " + itemId);
					continue;
				}
				if (count < product.getMaxCount())
				{
					product.setCount(count);
					product.restartRestockTask(nextRestockTime);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Failed to load buyList data from database.", e);
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		try
		{
			final int buyListId = Integer.parseInt(f.getName().replaceAll(".xml", ""));
			forEach(doc, "list", list ->
			{
				final int defaultBaseTax = parseInteger(list.getAttributes(), "baseTax", 0);
				final ProductList buyList = new ProductList(buyListId);
				forEach(list, node ->
				{
					switch (node.getNodeName())
					{
						case "item":
						{
							final NamedNodeMap attrs = node.getAttributes();
							final int itemId = parseInteger(attrs, "id");
							final ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
							if (item != null)
							{
								final long price = parseLong(attrs, "price", -1L);
								final long restockDelay = parseLong(attrs, "restock_delay", -1L);
								final long count = parseLong(attrs, "count", -1L);
								final int baseTax = parseInteger(attrs, "baseTax", defaultBaseTax);
								
								// Max equipable item grade configuration.
								final int itemCrystalLevel = item.getCrystalType().getLevel();
								if ((itemCrystalLevel > Config.MAX_EQUIPABLE_ITEM_GRADE.getLevel()) && (itemCrystalLevel < CrystalType.EVENT.getLevel()))
								{
									break;
								}
								
								final int sellPrice = item.getReferencePrice() / 2;
								if (Config.CORRECT_PRICES && (price > -1) && (sellPrice > price) && (buyList.getNpcsAllowed() != null))
								{
									LOGGER.warning("Buy price " + price + " is less than sell price " + sellPrice + " for ItemID:" + itemId + " of buylist " + buyList.getListId() + ".");
									buyList.addProduct(new Product(buyListId, item, sellPrice, restockDelay, count, baseTax));
								}
								else
								{
									buyList.addProduct(new Product(buyListId, item, price, restockDelay, count, baseTax));
								}
							}
							else
							{
								LOGGER.warning("Item not found. BuyList:" + buyListId + " ItemID:" + itemId + " File:" + f);
							}
							break;
						}
						case "npcs":
						{
							forEach(node, "npc", npcNode -> buyList.addAllowedNpc(Integer.parseInt(npcNode.getTextContent())));
							break;
						}
					}
				});
				_buyLists.put(buyListId, buyList);
			});
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Failed to load buyList data from xml File:" + f.getName(), e);
		}
	}
	
	@Override
	public FileFilter getCurrentFileFilter()
	{
		return NUMERIC_FILTER;
	}
	
	public ProductList getBuyList(int listId)
	{
		return _buyLists.get(listId);
	}
	
	public static BuyListData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BuyListData INSTANCE = new BuyListData();
	}
}
