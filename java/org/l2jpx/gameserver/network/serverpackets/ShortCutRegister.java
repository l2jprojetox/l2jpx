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
package org.l2jpx.gameserver.network.serverpackets;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Shortcut;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ShortCutRegister extends ServerPacket
{
	private final Player _player;
	private final Shortcut _shortcut;
	
	/**
	 * Register new skill shortcut
	 * @param shortcut
	 * @param player
	 */
	public ShortCutRegister(Shortcut shortcut, Player player)
	{
		_player = player;
		_shortcut = shortcut;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SHORT_CUT_REGISTER.writeId(this, buffer);
		buffer.writeInt(_shortcut.getType().ordinal());
		buffer.writeInt(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
		switch (_shortcut.getType())
		{
			case ITEM:
			{
				buffer.writeInt(_shortcut.getId());
				buffer.writeInt(_shortcut.getCharacterType());
				buffer.writeInt(_shortcut.getSharedReuseGroup());
				buffer.writeInt(0); // unknown
				buffer.writeInt(0); // unknown
				final Item item = _player.getInventory().getItemByObjectId(_shortcut.getId());
				if (item != null)
				{
					final VariationInstance augment = item.getAugmentation();
					if (augment != null)
					{
						buffer.writeInt(augment.getOption1Id());
						buffer.writeInt(augment.getOption2Id());
					}
					else
					{
						buffer.writeInt(0);
						buffer.writeInt(0);
					}
					buffer.writeInt(item.getVisualId());
				}
				else
				{
					buffer.writeInt(0);
					buffer.writeInt(0);
					buffer.writeInt(0);
				}
				
				break;
			}
			case SKILL:
			{
				buffer.writeInt(_shortcut.getId());
				buffer.writeShort(_shortcut.getLevel());
				buffer.writeShort(_shortcut.getSubLevel());
				buffer.writeInt(_shortcut.getSharedReuseGroup());
				buffer.writeByte(0); // C5
				buffer.writeInt(_shortcut.getCharacterType());
				buffer.writeInt(0); // if 1 - cannot use
				buffer.writeInt(0); // reuse delay ?
				break;
			}
			case ACTION:
			case MACRO:
			case RECIPE:
			case BOOKMARK:
			{
				buffer.writeInt(_shortcut.getId());
				buffer.writeInt(_shortcut.getCharacterType());
			}
		}
	}
}
