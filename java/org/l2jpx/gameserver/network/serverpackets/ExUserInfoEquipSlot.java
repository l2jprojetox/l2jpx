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
import org.l2jpx.gameserver.enums.InventorySlot;
import org.l2jpx.gameserver.model.VariationInstance;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw
 */
public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot>
{
	private final Player _player;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};
	
	public ExUserInfoEquipSlot(Player player)
	{
		this(player, true);
	}
	
	public ExUserInfoEquipSlot(Player player, boolean addAll)
	{
		_player = player;
		if (addAll)
		{
			addComponentType(InventorySlot.values());
		}
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_USER_INFO_EQUIP_SLOT.writeId(this, buffer);
		buffer.writeInt(_player.getObjectId());
		buffer.writeShort(InventorySlot.values().length);
		buffer.writeBytes(_masks);
		final PlayerInventory inventory = _player.getInventory();
		for (InventorySlot slot : InventorySlot.values())
		{
			if (containsMask(slot))
			{
				final VariationInstance augment = inventory.getPaperdollAugmentation(slot.getSlot());
				buffer.writeShort(22); // 10 + 4 * 3
				buffer.writeInt(inventory.getPaperdollObjectId(slot.getSlot()));
				buffer.writeInt(inventory.getPaperdollItemId(slot.getSlot()));
				buffer.writeInt(augment != null ? augment.getOption1Id() : 0);
				buffer.writeInt(augment != null ? augment.getOption2Id() : 0);
				buffer.writeInt(inventory.getPaperdollItemVisualId(slot.getSlot()));
			}
		}
	}
}