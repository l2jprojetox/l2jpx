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
package org.l2jpx.gameserver.network.serverpackets.luckygame;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.LuckyGameItemType;
import org.l2jpx.gameserver.enums.LuckyGameResultType;
import org.l2jpx.gameserver.enums.LuckyGameType;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExBettingLuckyGameResult extends ServerPacket
{
	public static final ExBettingLuckyGameResult NORMAL_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.NORMAL);
	public static final ExBettingLuckyGameResult LUXURY_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.LUXURY);
	public static final ExBettingLuckyGameResult NORMAL_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.NORMAL);
	public static final ExBettingLuckyGameResult LUXURY_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.LUXURY);
	
	private final LuckyGameResultType _result;
	private final LuckyGameType _type;
	private final Map<LuckyGameItemType, List<ItemHolder>> _rewards;
	private final int _ticketCount;
	private final int _size;
	
	public ExBettingLuckyGameResult(LuckyGameResultType result, LuckyGameType type)
	{
		_result = result;
		_type = type;
		_rewards = new EnumMap<>(LuckyGameItemType.class);
		_ticketCount = 0;
		_size = 0;
	}
	
	public ExBettingLuckyGameResult(LuckyGameResultType result, LuckyGameType type, Map<LuckyGameItemType, List<ItemHolder>> rewards, int ticketCount)
	{
		_result = result;
		_type = type;
		_rewards = rewards;
		_ticketCount = ticketCount;
		_size = (int) rewards.values().stream().mapToLong(i -> i.stream().count()).sum();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_BETTING_LUCKY_GAME_RESULT.writeId(this, buffer);
		buffer.writeInt(_result.getClientId());
		buffer.writeInt(_type.ordinal());
		buffer.writeInt(_ticketCount);
		buffer.writeInt(_size);
		for (Entry<LuckyGameItemType, List<ItemHolder>> reward : _rewards.entrySet())
		{
			for (ItemHolder item : reward.getValue())
			{
				buffer.writeInt(reward.getKey().getClientId());
				buffer.writeInt(item.getId());
				buffer.writeInt((int) item.getCount());
			}
		}
	}
}
