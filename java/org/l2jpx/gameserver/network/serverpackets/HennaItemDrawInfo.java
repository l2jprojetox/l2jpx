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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.Henna;
import org.l2jpx.gameserver.model.stats.BaseStat;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Zoey76
 */
public class HennaItemDrawInfo extends ServerPacket
{
	private final Player _player;
	private final Henna _henna;
	
	public HennaItemDrawInfo(Henna henna, Player player)
	{
		_henna = henna;
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.HENNA_ITEM_INFO.writeId(this, buffer);
		buffer.writeInt(_henna.getDyeId()); // symbol Id
		buffer.writeInt(_henna.getDyeItemId()); // item id of dye
		buffer.writeLong(_henna.getWearCount()); // total amount of dye require
		buffer.writeLong(_henna.getWearFee()); // total amount of Adena require to draw symbol
		buffer.writeInt(_henna.isAllowedClass(_player.getClassId())); // able to draw or not 0 is false and 1 is true
		buffer.writeLong(_player.getAdena());
		buffer.writeInt(_player.getINT()); // current INT
		buffer.writeShort(_player.getINT() + _player.getHennaValue(BaseStat.INT)); // equip INT
		buffer.writeInt(_player.getSTR()); // current STR
		buffer.writeShort(_player.getSTR() + _player.getHennaValue(BaseStat.STR)); // equip STR
		buffer.writeInt(_player.getCON()); // current CON
		buffer.writeShort(_player.getCON() + _player.getHennaValue(BaseStat.CON)); // equip CON
		buffer.writeInt(_player.getMEN()); // current MEN
		buffer.writeShort(_player.getMEN() + _player.getHennaValue(BaseStat.MEN)); // equip MEN
		buffer.writeInt(_player.getDEX()); // current DEX
		buffer.writeShort(_player.getDEX() + _player.getHennaValue(BaseStat.DEX)); // equip DEX
		buffer.writeInt(_player.getWIT()); // current WIT
		buffer.writeShort(_player.getWIT() + _player.getHennaValue(BaseStat.WIT)); // equip WIT
		buffer.writeInt(0); // TODO: Find me!
	}
}
