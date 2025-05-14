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
import org.l2jpx.gameserver.model.olympiad.Participant;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author godson
 */
public class ExOlympiadUserInfo extends ServerPacket
{
	private final Player _player;
	private Participant _par = null;
	private int _curHp;
	private int _maxHp;
	private int _curCp;
	private int _maxCp;
	
	public ExOlympiadUserInfo(Player player)
	{
		_player = player;
		if (_player != null)
		{
			_curHp = (int) _player.getCurrentHp();
			_maxHp = _player.getMaxHp();
			_curCp = (int) _player.getCurrentCp();
			_maxCp = _player.getMaxCp();
		}
		else
		{
			_curHp = 0;
			_maxHp = 100;
			_curCp = 0;
			_maxCp = 100;
		}
	}
	
	public ExOlympiadUserInfo(Participant par)
	{
		_par = par;
		_player = par.getPlayer();
		if (_player != null)
		{
			_curHp = (int) _player.getCurrentHp();
			_maxHp = _player.getMaxHp();
			_curCp = (int) _player.getCurrentCp();
			_maxCp = _player.getMaxCp();
		}
		else
		{
			_curHp = 0;
			_maxHp = 100;
			_curCp = 0;
			_maxCp = 100;
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_OLYMPIAD_USER_INFO.writeId(this, buffer);
		if (_player != null)
		{
			buffer.writeByte(_player.getOlympiadSide());
			buffer.writeInt(_player.getObjectId());
			buffer.writeString(_player.getName());
			buffer.writeInt(_player.getClassId().getId());
		}
		else
		{
			buffer.writeByte(_par.getSide());
			buffer.writeInt(_par.getObjectId());
			buffer.writeString(_par.getName());
			buffer.writeInt(_par.getBaseClass());
		}
		buffer.writeInt(_curHp);
		buffer.writeInt(_maxHp);
		buffer.writeInt(_curCp);
		buffer.writeInt(_maxCp);
	}
}
