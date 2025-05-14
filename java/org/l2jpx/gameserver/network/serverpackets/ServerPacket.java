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
import org.l2jpx.commons.network.WritablePacket;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.network.ConnectionState;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.PacketLogger;

/**
 * @author Mobius
 */
public abstract class ServerPacket extends WritablePacket<GameClient>
{
	private static final int[] PAPERDOLL_ORDER =
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_REAR,
		Inventory.PAPERDOLL_LEAR,
		Inventory.PAPERDOLL_NECK,
		Inventory.PAPERDOLL_RFINGER,
		Inventory.PAPERDOLL_LFINGER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_CLOAK,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2,
		Inventory.PAPERDOLL_RBRACELET,
		Inventory.PAPERDOLL_LBRACELET,
		Inventory.PAPERDOLL_DECO1,
		Inventory.PAPERDOLL_DECO2,
		Inventory.PAPERDOLL_DECO3,
		Inventory.PAPERDOLL_DECO4,
		Inventory.PAPERDOLL_DECO5,
		Inventory.PAPERDOLL_DECO6,
		Inventory.PAPERDOLL_BELT,
		Inventory.PAPERDOLL_BROOCH,
		Inventory.PAPERDOLL_BROOCH_JEWEL1,
		Inventory.PAPERDOLL_BROOCH_JEWEL2,
		Inventory.PAPERDOLL_BROOCH_JEWEL3,
		Inventory.PAPERDOLL_BROOCH_JEWEL4,
		Inventory.PAPERDOLL_BROOCH_JEWEL5,
		Inventory.PAPERDOLL_BROOCH_JEWEL6
	};
	private static final int[] PAPERDOLL_ORDER_AUGMENT =
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_RHAND
	};
	private static final int[] PAPERDOLL_ORDER_VISUAL_ID =
	{
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2
	};
	
	protected int[] getPaperdollOrder()
	{
		return PAPERDOLL_ORDER;
	}
	
	protected int[] getPaperdollOrderAugument()
	{
		return PAPERDOLL_ORDER_AUGMENT;
	}
	
	protected int[] getPaperdollOrderVisualId()
	{
		return PAPERDOLL_ORDER_VISUAL_ID;
	}
	
	protected void writeOptionalInt(int value, WritableBuffer buffer)
	{
		if (value >= Short.MAX_VALUE)
		{
			buffer.writeShort(Short.MAX_VALUE);
			buffer.writeInt(value);
		}
		else
		{
			buffer.writeShort(value);
		}
	}
	
	@Override
	protected boolean write(GameClient client, WritableBuffer buffer)
	{
		final GameClient c = client;
		if ((c == null) || c.isDetached() || (c.getConnectionState() == ConnectionState.DISCONNECTED))
		{
			return true; // Disconnected client.
		}
		
		try
		{
			writeImpl(c, buffer);
			return true;
		}
		catch (Exception e)
		{
			PacketLogger.warning("Error writing packet " + this + " to client (" + e.getMessage() + ") " + c + "]]");
			PacketLogger.warning(CommonUtil.getStackTrace(e));
		}
		return false;
	}
	
	public void runImpl(Player player)
	{
	}
	
	protected abstract void writeImpl(GameClient client, WritableBuffer buffer) throws Exception;
}
