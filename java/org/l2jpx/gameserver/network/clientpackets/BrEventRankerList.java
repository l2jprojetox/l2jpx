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

import org.l2jpx.gameserver.network.serverpackets.ExBrLoadEventTopRankers;

/**
 * Halloween rank list client  Format: (ch)ddd
 */
public class BrEventRankerList extends ClientPacket
{
	private int _eventId;
	private int _day;
	@SuppressWarnings("unused")
	private int _ranking;
	
	@Override
	protected void readImpl()
	{
		_eventId = readInt();
		_day = readInt(); // 0 - current, 1 - previous
		_ranking = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		// TODO count, bestScore, myScore
		final int count = 0;
		final int bestScore = 0;
		final int myScore = 0;
		getClient().sendPacket(new ExBrLoadEventTopRankers(_eventId, _day, count, bestScore, myScore));
	}
}
