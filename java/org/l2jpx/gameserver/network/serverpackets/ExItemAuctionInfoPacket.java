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
import org.l2jpx.gameserver.model.itemauction.ItemAuction;
import org.l2jpx.gameserver.model.itemauction.ItemAuctionBid;
import org.l2jpx.gameserver.model.itemauction.ItemAuctionState;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Forsaiken
 */
public class ExItemAuctionInfoPacket extends AbstractItemPacket
{
	private final boolean _refresh;
	private final int _timeRemaining;
	private final ItemAuction _currentAuction;
	private final ItemAuction _nextAuction;
	
	public ExItemAuctionInfoPacket(boolean refresh, ItemAuction currentAuction, ItemAuction nextAuction)
	{
		if (currentAuction == null)
		{
			throw new NullPointerException();
		}
		if (currentAuction.getAuctionState() != ItemAuctionState.STARTED)
		{
			_timeRemaining = 0;
		}
		else
		{
			_timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds
		}
		_refresh = refresh;
		_currentAuction = currentAuction;
		_nextAuction = nextAuction;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ITEM_AUCTION_INFO.writeId(this, buffer);
		buffer.writeByte(!_refresh);
		buffer.writeInt(_currentAuction.getInstanceId());
		final ItemAuctionBid highestBid = _currentAuction.getHighestBid();
		buffer.writeLong(highestBid != null ? highestBid.getLastBid() : _currentAuction.getAuctionInitBid());
		buffer.writeInt(_timeRemaining);
		writeItem(_currentAuction.getItemInfo(), buffer);
		if (_nextAuction != null)
		{
			buffer.writeLong(_nextAuction.getAuctionInitBid());
			buffer.writeInt((int) (_nextAuction.getStartingTime() / 1000)); // unix time in seconds
			writeItem(_nextAuction.getItemInfo(), buffer);
		}
	}
}
