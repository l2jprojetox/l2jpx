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

import org.w3c.dom.Document;

import org.l2jpx.Config;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.enums.ElementalItemType;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.holders.ElementalItemHolder;
import org.l2jpx.gameserver.model.item.instance.Item;

/**
 * @author Mobius
 */
public class ElementalAttributeData implements IXmlReader
{
	private static final Map<Integer, ElementalItemHolder> ELEMENTAL_ITEMS = new HashMap<>();
	
	public static final int FIRST_WEAPON_BONUS = 20;
	public static final int NEXT_WEAPON_BONUS = 5;
	public static final int ARMOR_BONUS = 6;
	
	public static final int[] WEAPON_VALUES =
	{
		0, // Level 1
		25, // Level 2
		75, // Level 3
		150, // Level 4
		175, // Level 5
		225, // Level 6
		300, // Level 7
		325, // Level 8
		375, // Level 9
		450, // Level 10
		475, // Level 11
		525, // Level 12
		600, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	public static final int[] ARMOR_VALUES =
	{
		0, // Level 1
		12, // Level 2
		30, // Level 3
		60, // Level 4
		72, // Level 5
		90, // Level 6
		120, // Level 7
		132, // Level 8
		150, // Level 9
		180, // Level 10
		192, // Level 11
		210, // Level 12
		240, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	/* @formatter:off */
	private static final int[][] CHANCE_TABLE =
	{
		{Config.S_WEAPON_STONE,		Config.S_ARMOR_STONE,		Config.S_WEAPON_CRYSTAL,	Config.S_ARMOR_CRYSTAL,		Config.S_WEAPON_STONE_SUPER,	Config.S_ARMOR_STONE_SUPER,		Config.S_WEAPON_CRYSTAL_SUPER,		Config.S_ARMOR_CRYSTAL_SUPER,		Config.S_WEAPON_JEWEL,		Config.S_ARMOR_JEWEL},
		{Config.S80_WEAPON_STONE,	Config.S80_ARMOR_STONE,		Config.S80_WEAPON_CRYSTAL,	Config.S80_ARMOR_CRYSTAL,	Config.S80_WEAPON_STONE_SUPER,	Config.S80_ARMOR_STONE_SUPER,	Config.S80_WEAPON_CRYSTAL_SUPER,	Config.S80_ARMOR_CRYSTAL_SUPER,		Config.S80_WEAPON_JEWEL,	Config.S80_ARMOR_JEWEL},
		{Config.S84_WEAPON_STONE,	Config.S84_ARMOR_STONE,		Config.S84_WEAPON_CRYSTAL,	Config.S84_ARMOR_CRYSTAL,	Config.S84_WEAPON_STONE_SUPER,	Config.S84_ARMOR_STONE_SUPER,	Config.S84_WEAPON_CRYSTAL_SUPER,	Config.S84_ARMOR_CRYSTAL_SUPER,		Config.S84_WEAPON_JEWEL,	Config.S84_ARMOR_JEWEL},
		{Config.R_WEAPON_STONE,		Config.R_ARMOR_STONE,		Config.R_WEAPON_CRYSTAL,	Config.R_ARMOR_CRYSTAL,		Config.R_WEAPON_STONE_SUPER,	Config.R_ARMOR_STONE_SUPER,		Config.R_WEAPON_CRYSTAL_SUPER,		Config.R_ARMOR_CRYSTAL_SUPER,		Config.R_WEAPON_JEWEL,		Config.R_ARMOR_JEWEL},
		{Config.R95_WEAPON_STONE,	Config.R95_ARMOR_STONE,		Config.R95_WEAPON_CRYSTAL,	Config.R95_ARMOR_CRYSTAL,	Config.R95_WEAPON_STONE_SUPER,	Config.R95_ARMOR_STONE_SUPER,	Config.R95_WEAPON_CRYSTAL_SUPER,	Config.R95_ARMOR_CRYSTAL_SUPER,		Config.R95_WEAPON_JEWEL,	Config.R95_ARMOR_JEWEL},
		{Config.R99_WEAPON_STONE,	Config.R99_ARMOR_STONE,		Config.R99_WEAPON_CRYSTAL,	Config.R99_ARMOR_CRYSTAL,	Config.R99_WEAPON_STONE_SUPER,	Config.R99_ARMOR_STONE_SUPER,	Config.R99_WEAPON_CRYSTAL_SUPER,	Config.R99_ARMOR_CRYSTAL_SUPER,		Config.R99_WEAPON_JEWEL,	Config.R99_ARMOR_JEWEL},
	};	
	/* @formatter:on */
	
	protected ElementalAttributeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		ELEMENTAL_ITEMS.clear();
		parseDatapackFile("data/ElementalAttributeData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + ELEMENTAL_ITEMS.size() + " elemental attribute items.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode ->
		{
			final StatSet set = new StatSet(parseAttributes(itemNode));
			
			final int id = set.getInt("id");
			if (ItemData.getInstance().getTemplate(id) == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + id + ".");
				return;
			}
			
			ELEMENTAL_ITEMS.put(id, new ElementalItemHolder(id, set.getEnum("elemental", AttributeType.class), set.getEnum("type", ElementalItemType.class), set.getInt("power", 0)));
		}));
	}
	
	public AttributeType getItemElement(int itemId)
	{
		final ElementalItemHolder item = ELEMENTAL_ITEMS.get(itemId);
		if (item != null)
		{
			return item.getElement();
		}
		return AttributeType.NONE;
	}
	
	public ElementalItemHolder getItemElemental(int itemId)
	{
		return ELEMENTAL_ITEMS.get(itemId);
	}
	
	public int getMaxElementLevel(int itemId)
	{
		final ElementalItemHolder item = ELEMENTAL_ITEMS.get(itemId);
		if (item != null)
		{
			return item.getType().getMaxLevel();
		}
		return -1;
	}
	
	public boolean isSuccess(Item item, int stoneId)
	{
		int row = -1;
		int column = -1;
		switch (item.getTemplate().getCrystalType())
		{
			case S:
			{
				row = 0;
				break;
			}
			case S80:
			{
				row = 1;
				break;
			}
			case S84:
			{
				row = 2;
				break;
			}
			case R:
			{
				row = 3;
				break;
			}
			case R95:
			{
				row = 4;
				break;
			}
			case R99:
			{
				row = 5;
				break;
			}
		}
		
		switch (ELEMENTAL_ITEMS.get(stoneId).getType())
		{
			case STONE:
			{
				column = item.isWeapon() ? 0 : 1;
				break;
			}
			case CRYSTAL:
			{
				column = item.isWeapon() ? 2 : 3;
				break;
			}
			case STONE_SUPER:
			{
				column = item.isWeapon() ? 4 : 5;
				break;
			}
			case CRYSTAL_SUPER:
			{
				column = item.isWeapon() ? 6 : 7;
				break;
			}
			case JEWEL:
			{
				column = item.isWeapon() ? 8 : 9;
				break;
			}
		}
		if ((row != -1) && (column != -1))
		{
			return Rnd.get(100) < CHANCE_TABLE[row][column];
		}
		return true;
	}
	
	public static ElementalAttributeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ElementalAttributeData INSTANCE = new ElementalAttributeData();
	}
}
