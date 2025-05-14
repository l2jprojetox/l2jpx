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

import org.l2jpx.gameserver.enums.AttributeType;
import org.l2jpx.gameserver.enums.ElementalItemType;

/**
 * @author Mobius
 */
public class ElementalItemHolder
{
	private final int _itemId;
	private final AttributeType _element;
	private final ElementalItemType _type;
	private final int _power;
	
	public ElementalItemHolder(int itemId, AttributeType element, ElementalItemType type, int power)
	{
		_itemId = itemId;
		_element = element;
		_type = type;
		_power = power;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public AttributeType getElement()
	{
		return _element;
	}
	
	public ElementalItemType getType()
	{
		return _type;
	}
	
	public int getPower()
	{
		return _power;
	}
}
