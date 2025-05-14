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
import org.l2jpx.gameserver.model.actor.instance.Door;
import org.l2jpx.gameserver.model.actor.instance.StaticObject;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class StaticObjectInfo extends ServerPacket
{
	private final int _staticObjectId;
	private final int _objectId;
	private final int _type;
	private final boolean _isTargetable;
	private final int _meshIndex;
	private final boolean _isClosed;
	private final boolean _isEnemy;
	private final int _maxHp;
	private final int _currentHp;
	private final boolean _showHp;
	private final int _damageGrade;
	
	public StaticObjectInfo(StaticObject staticObject)
	{
		_staticObjectId = staticObject.getId();
		_objectId = staticObject.getObjectId();
		_type = 0;
		_isTargetable = true;
		_meshIndex = staticObject.getMeshIndex();
		_isClosed = false;
		_isEnemy = false;
		_maxHp = 0;
		_currentHp = 0;
		_showHp = false;
		_damageGrade = 0;
	}
	
	public StaticObjectInfo(Door door, boolean targetable)
	{
		_staticObjectId = door.getId();
		_objectId = door.getObjectId();
		_type = 1;
		_isTargetable = door.isTargetable() || targetable;
		_meshIndex = door.getMeshIndex();
		_isClosed = !door.isOpen();
		_isEnemy = door.isEnemy();
		_maxHp = door.getMaxHp();
		_currentHp = (int) door.getCurrentHp();
		_showHp = door.isShowHp();
		_damageGrade = door.getDamage();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.STATIC_OBJECT.writeId(this, buffer);
		buffer.writeInt(_staticObjectId);
		buffer.writeInt(_objectId);
		buffer.writeInt(_type);
		buffer.writeInt(_isTargetable);
		buffer.writeInt(_meshIndex);
		buffer.writeInt(_isClosed);
		buffer.writeInt(_isEnemy);
		buffer.writeInt(_currentHp);
		buffer.writeInt(_maxHp);
		buffer.writeInt(_showHp);
		buffer.writeInt(_damageGrade);
	}
}
