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
package org.l2jpx.gameserver.model.stats;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.model.actor.Creature;

/**
 * @author DS
 */
public enum BaseStat
{
	STR(Stat.STAT_STR),
	INT(Stat.STAT_INT),
	DEX(Stat.STAT_DEX),
	WIT(Stat.STAT_WIT),
	CON(Stat.STAT_CON),
	MEN(Stat.STAT_MEN),
	CHA(Stat.STAT_CHA),
	LUC(Stat.STAT_LUC);
	
	private static final BaseStat[] VALUES = BaseStat.values();
	
	public static final int MAX_STAT_VALUE = 201;
	
	private final double[] _bonus = new double[MAX_STAT_VALUE];
	private final Stat _stat;
	
	BaseStat(Stat stat)
	{
		_stat = stat;
	}
	
	public Stat getStat()
	{
		return _stat;
	}
	
	public int calcValue(Creature creature)
	{
		if ((creature != null) && (_stat != null))
		{
			// return (int) Math.min(_stat.finalize(creature, Optional.empty()), MAX_STAT_VALUE - 1);
			return (int) creature.getStat().getValue(_stat);
		}
		return 0;
	}
	
	public double calcBonus(Creature creature)
	{
		if (creature != null)
		{
			final int value = calcValue(creature);
			if (value < 1)
			{
				return 1;
			}
			return _bonus[value];
		}
		
		return 1;
	}
	
	void setValue(int index, double value)
	{
		_bonus[index] = value;
	}
	
	public double getValue(int index)
	{
		return _bonus[index];
	}
	
	public static BaseStat valueOf(Stat stat)
	{
		for (BaseStat baseStat : VALUES)
		{
			if (baseStat.getStat() == stat)
			{
				return baseStat;
			}
		}
		throw new NoSuchElementException("Unknown base stat '" + stat + "' for enum BaseStats");
	}
	
	static
	{
		new IXmlReader()
		{
			final Logger LOGGER = Logger.getLogger(BaseStat.class.getName());
			
			@Override
			public void load()
			{
				parseDatapackFile("data/stats/statBonus.xml");
			}
			
			@Override
			public void parseDocument(Document doc, File f)
			{
				forEach(doc, "list", listNode -> forEach(listNode, IXmlReader::isNode, statNode ->
				{
					final BaseStat baseStat;
					try
					{
						baseStat = valueOf(statNode.getNodeName());
					}
					catch (Exception e)
					{
						LOGGER.severe("Invalid base stats type: " + statNode.getNodeValue() + ", skipping");
						return;
					}
					
					forEach(statNode, "stat", statValue ->
					{
						final NamedNodeMap attrs = statValue.getAttributes();
						final int val = parseInteger(attrs, "value");
						final double bonus = parseDouble(attrs, "bonus");
						baseStat.setValue(val, bonus);
					});
				}));
			}
		}.load();
	}
}