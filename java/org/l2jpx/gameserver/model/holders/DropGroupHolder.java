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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mobius
 */
public class DropGroupHolder
{
	private final List<DropHolder> _dropList = new ArrayList<>();
	private final double _chance;
	
	public DropGroupHolder(double chance)
	{
		_chance = chance;
	}
	
	public List<DropHolder> getDropList()
	{
		return _dropList;
	}
	
	public void addDrop(DropHolder holder)
	{
		_dropList.add(holder);
	}
	
	public double getChance()
	{
		return _chance;
	}
}
