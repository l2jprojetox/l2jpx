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

import java.util.Collections;
import java.util.List;

import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.model.item.ItemTemplate;

/**
 * @author Nik, Mobius
 */
public class MultisellEntryHolder
{
	private final boolean _stackable;
	private final List<ItemChanceHolder> _ingredients;
	private final List<ItemChanceHolder> _products;
	
	public MultisellEntryHolder(List<ItemChanceHolder> ingredients, List<ItemChanceHolder> products)
	{
		_ingredients = Collections.unmodifiableList(ingredients);
		_products = Collections.unmodifiableList(products);
		
		for (ItemChanceHolder product : products)
		{
			final ItemTemplate item = ItemData.getInstance().getTemplate(product.getId());
			if ((item == null) || !item.isStackable())
			{
				_stackable = false;
				return;
			}
		}
		_stackable = true;
	}
	
	public List<ItemChanceHolder> getIngredients()
	{
		return _ingredients;
	}
	
	public List<ItemChanceHolder> getProducts()
	{
		return _products;
	}
	
	public boolean isStackable()
	{
		return _stackable;
	}
}