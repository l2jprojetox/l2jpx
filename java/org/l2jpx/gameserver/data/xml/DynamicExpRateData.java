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
import java.util.Arrays;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jpx.Config;
import org.l2jpx.commons.util.IXmlReader;

/**
 * @author MrNiceGuy
 */
public class DynamicExpRateData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(DynamicExpRateData.class.getName());
	
	private static float[] _expRates = new float[Config.PLAYER_MAXIMUM_LEVEL + 1];
	private static float[] _spRates = new float[Config.PLAYER_MAXIMUM_LEVEL + 1];
	private static boolean _enabled = false;
	
	protected DynamicExpRateData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("config/DynamicExpRates.xml");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		int count = 0;
		Arrays.fill(_expRates, 1f);
		Arrays.fill(_spRates, 1f);
		
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("dynamic".equals(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final int level = parseInteger(attrs, "level");
						final float exp = parseFloat(attrs, "exp");
						final float sp = parseFloat(attrs, "sp");
						if ((exp != 1) || (sp != 1))
						{
							_expRates[level] = exp;
							_spRates[level] = sp;
							count++;
						}
					}
				}
			}
		}
		
		_enabled = count > 0;
		if (_enabled)
		{
			LOGGER.info(getClass().getSimpleName() + ": Loaded dynamic rates for " + count + " levels.");
		}
	}
	
	/**
	 * @param level
	 * @return the dynamic EXP rate for specified level.
	 */
	public float getDynamicExpRate(int level)
	{
		return _expRates[level];
	}
	
	/**
	 * @param level
	 * @return the dynamic SP rate for specified level.
	 */
	public float getDynamicSpRate(int level)
	{
		return _spRates[level];
	}
	
	/**
	 * @return if dynamic rates are enabled.
	 */
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	/**
	 * Gets the single instance of DynamicExpRates.
	 * @return single instance of DynamicExpRates
	 */
	public static DynamicExpRateData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DynamicExpRateData INSTANCE = new DynamicExpRateData();
	}
}
