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
package org.l2jpx.gameserver.network.clientpackets;

import org.l2jpx.gameserver.enums.ShortcutType;
import org.l2jpx.gameserver.model.Shortcut;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.ShortCutRegister;

public class RequestShortCutReg extends ClientPacket
{
	private ShortcutType _type;
	private int _id;
	private int _slot;
	private int _page;
	private int _level;
	private int _subLevel;
	private int _characterType; // 1 - player, 2 - pet
	
	@Override
	protected void readImpl()
	{
		final int typeId = readInt();
		_type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
		final int slot = readInt();
		_slot = slot % 12;
		_page = slot / 12;
		_id = readInt();
		_level = readShort();
		_subLevel = readShort(); // Sublevel
		_characterType = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((_page > 19) || (_page < 0))
		{
			return;
		}
		
		final Shortcut sc = new Shortcut(_slot, _page, _type, _id, _level, _subLevel, _characterType);
		player.registerShortCut(sc);
		player.sendPacket(new ShortCutRegister(sc, player));
		player.sendSkillList();
	}
}
