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

import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.olympiad.OlympiadInfo;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult extends ServerPacket
{
	private final boolean _tie;
	private int _winTeam; // 1,2
	private int _loseTeam = 2;
	private final List<OlympiadInfo> _winnerList;
	private final List<OlympiadInfo> _loserList;
	
	public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList)
	{
		_tie = tie;
		_winTeam = winTeam;
		_winnerList = winnerList;
		_loserList = loserList;
		if (_winTeam == 2)
		{
			_loseTeam = 1;
		}
		else if (_winTeam == 0)
		{
			_winTeam = 1;
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_RECEIVE_OLYMPIAD.writeId(this, buffer);
		buffer.writeInt(1); // Type 0 = Match List, 1 = Match Result
		buffer.writeInt(_tie); // 0 - win, 1 - tie
		buffer.writeString(_winnerList.get(0).getName());
		buffer.writeInt(_winTeam);
		buffer.writeInt(_winnerList.size());
		for (OlympiadInfo info : _winnerList)
		{
			buffer.writeString(info.getName());
			buffer.writeString(info.getClanName());
			buffer.writeInt(info.getClanId());
			buffer.writeInt(info.getClassId());
			buffer.writeInt(info.getDamage());
			buffer.writeInt(info.getCurrentPoints());
			buffer.writeInt(info.getDiffPoints());
			buffer.writeInt(0); // Helios
		}
		buffer.writeInt(_loseTeam);
		buffer.writeInt(_loserList.size());
		for (OlympiadInfo info : _loserList)
		{
			buffer.writeString(info.getName());
			buffer.writeString(info.getClanName());
			buffer.writeInt(info.getClanId());
			buffer.writeInt(info.getClassId());
			buffer.writeInt(info.getDamage());
			buffer.writeInt(info.getCurrentPoints());
			buffer.writeInt(info.getDiffPoints());
			buffer.writeInt(0); // Helios
		}
	}
}
