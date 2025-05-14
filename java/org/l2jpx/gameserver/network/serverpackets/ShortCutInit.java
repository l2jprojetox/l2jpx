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

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.Shortcut;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ShortCutInit extends ServerPacket
{
	private final Player _player;
	private final Collection<Shortcut> _shortCuts;
	
	public ShortCutInit(Player player)
	{
		_player = player;
		_shortCuts = player.getAllShortCuts();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SHORT_CUT_INIT.writeId(this, buffer);
		buffer.writeInt(_shortCuts.size());
		for (Shortcut sc : _shortCuts)
		{
			buffer.writeInt(sc.getType().ordinal());
			buffer.writeInt(sc.getSlot() + (sc.getPage() * 12));
			switch (sc.getType())
			{
				case ITEM:
				{
					buffer.writeInt(sc.getId());
					buffer.writeInt(1); // Enabled or not
					buffer.writeInt(sc.getSharedReuseGroup());
					buffer.writeInt(0);
					buffer.writeInt(0);
					
					final Item item = _player.getInventory().getItemByObjectId(sc.getId());
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
					buffer.writeInt(sc.getId());
					buffer.writeShort(sc.getLevel());
					buffer.writeShort(sc.getSubLevel());
					buffer.writeInt(sc.getSharedReuseGroup());
					buffer.writeByte(0); // C5
					buffer.writeInt(1); // C6
					break;
				}
				case ACTION:
				case MACRO:
				case RECIPE:
				case BOOKMARK:
				{
					buffer.writeInt(sc.getId());
					buffer.writeInt(1); // C6
				}
			}
		}
	}
}
