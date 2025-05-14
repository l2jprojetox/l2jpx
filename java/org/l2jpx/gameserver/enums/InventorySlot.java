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
import org.l2jpx.gameserver.model.itemcontainer.Inventory;

/**
 * @author UnAfraid
 */
public enum InventorySlot implements IUpdateTypeComponent
{
	UNDER(Inventory.PAPERDOLL_UNDER),
	REAR(Inventory.PAPERDOLL_REAR),
	LEAR(Inventory.PAPERDOLL_LEAR),
	NECK(Inventory.PAPERDOLL_NECK),
	RFINGER(Inventory.PAPERDOLL_RFINGER),
	LFINGER(Inventory.PAPERDOLL_LFINGER),
	HEAD(Inventory.PAPERDOLL_HEAD),
	RHAND(Inventory.PAPERDOLL_RHAND),
	LHAND(Inventory.PAPERDOLL_LHAND),
	GLOVES(Inventory.PAPERDOLL_GLOVES),
	CHEST(Inventory.PAPERDOLL_CHEST),
	LEGS(Inventory.PAPERDOLL_LEGS),
	FEET(Inventory.PAPERDOLL_FEET),
	CLOAK(Inventory.PAPERDOLL_CLOAK),
	LRHAND(Inventory.PAPERDOLL_RHAND),
	HAIR(Inventory.PAPERDOLL_HAIR),
	HAIR2(Inventory.PAPERDOLL_HAIR2),
	RBRACELET(Inventory.PAPERDOLL_RBRACELET),
	LBRACELET(Inventory.PAPERDOLL_LBRACELET),
	DECO1(Inventory.PAPERDOLL_DECO1),
	DECO2(Inventory.PAPERDOLL_DECO2),
	DECO3(Inventory.PAPERDOLL_DECO3),
	DECO4(Inventory.PAPERDOLL_DECO4),
	DECO5(Inventory.PAPERDOLL_DECO5),
	DECO6(Inventory.PAPERDOLL_DECO6),
	BELT(Inventory.PAPERDOLL_BELT),
	BROOCH(Inventory.PAPERDOLL_BROOCH),
	BROOCH_JEWEL(Inventory.PAPERDOLL_BROOCH_JEWEL1),
	BROOCH_JEWEL2(Inventory.PAPERDOLL_BROOCH_JEWEL2),
	BROOCH_JEWEL3(Inventory.PAPERDOLL_BROOCH_JEWEL3),
	BROOCH_JEWEL4(Inventory.PAPERDOLL_BROOCH_JEWEL4),
	BROOCH_JEWEL5(Inventory.PAPERDOLL_BROOCH_JEWEL5),
	BROOCH_JEWEL6(Inventory.PAPERDOLL_BROOCH_JEWEL6);
	
	private final int _paperdollSlot;
	
	private InventorySlot(int paperdollSlot)
	{
		_paperdollSlot = paperdollSlot;
	}
	
	public int getSlot()
	{
		return _paperdollSlot;
	}
	
	@Override
	public int getMask()
	{
		return ordinal();
	}
}
