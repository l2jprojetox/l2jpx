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
package org.l2jpx.gameserver.model.holders;

import org.l2jpx.gameserver.model.StatSet;

/**
 * @author Index, Mobius
 */
public class ItemEnchantHolder extends ItemHolder
{
	private final int _enchantLevel;
	
	public ItemEnchantHolder(StatSet set)
	{
		super(set);
		_enchantLevel = 0;
	}
	
	public ItemEnchantHolder(int id, long count)
	{
		super(id, count);
		_enchantLevel = 0;
	}
	
	public ItemEnchantHolder(int id, long count, int enchantLevel)
	{
		super(id, count);
		_enchantLevel = enchantLevel;
	}
	
	/**
	 * @return enchant level of items contained in this object
	 */
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ItemEnchantHolder objInstance))
		{
			return false;
		}
		else if (obj == this)
		{
			return true;
		}
		return (getId() == objInstance.getId()) && ((getCount() == objInstance.getCount()) && (_enchantLevel == objInstance.getEnchantLevel()));
	}
	
	@Override
	public String toString()
	{
		return "[" + getClass().getSimpleName() + "] ID: " + getId() + ", count: " + getCount() + ", enchant level: " + _enchantLevel;
	}
}
