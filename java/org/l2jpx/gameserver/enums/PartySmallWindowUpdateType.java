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
package org.l2jpx.gameserver.enums;

import org.l2jpx.gameserver.model.interfaces.IUpdateTypeComponent;

/**
 * @author UnAfraid
 */
public enum PartySmallWindowUpdateType implements IUpdateTypeComponent
{
	CURRENT_CP(1),
	MAX_CP(2),
	CURRENT_HP(4),
	MAX_HP(8),
	CURRENT_MP(16),
	MAX_MP(32),
	LEVEL(64),
	CLASS_ID(128),
	PARTY_SUBSTITUTE(256),
	VITALITY_POINTS(512);
	
	private final int _mask;
	
	private PartySmallWindowUpdateType(int mask)
	{
		_mask = mask;
	}
	
	@Override
	public int getMask()
	{
		return _mask;
	}
}
