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
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.actor.instance.Servitor;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @version $Revision: 1.5.2.3.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusUpdate extends ServerPacket
{
	private final Summon _summon;
	private int _maxFed;
	private int _curFed;
	
	public PetStatusUpdate(Summon summon)
	{
		_summon = summon;
		if (_summon.isPet())
		{
			final Pet pet = (Pet) _summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); // max fed it can be
		}
		else if (_summon.isServitor())
		{
			final Servitor sum = (Servitor) _summon;
			_curFed = sum.getLifeTimeRemaining();
			_maxFed = sum.getLifeTime();
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PET_STATUS_UPDATE.writeId(this, buffer);
		buffer.writeInt(_summon.getSummonType());
		buffer.writeInt(_summon.getObjectId());
		buffer.writeInt(_summon.getX());
		buffer.writeInt(_summon.getY());
		buffer.writeInt(_summon.getZ());
		buffer.writeString(_summon.getTitle());
		buffer.writeInt(_curFed);
		buffer.writeInt(_maxFed);
		buffer.writeInt((int) _summon.getCurrentHp());
		buffer.writeInt(_summon.getMaxHp());
		buffer.writeInt((int) _summon.getCurrentMp());
		buffer.writeInt(_summon.getMaxMp());
		buffer.writeInt(_summon.getLevel());
		buffer.writeLong(_summon.getStat().getExp());
		buffer.writeLong(_summon.getExpForThisLevel()); // 0% absolute value
		buffer.writeLong(_summon.getExpForNextLevel()); // 100% absolute value
		buffer.writeInt(1); // TODO: Find me!
	}
}
