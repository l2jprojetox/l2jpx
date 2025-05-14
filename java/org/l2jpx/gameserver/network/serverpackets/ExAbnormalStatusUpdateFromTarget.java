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

import java.util.ArrayList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class ExAbnormalStatusUpdateFromTarget extends ServerPacket
{
	private final Creature _creature;
	private final List<BuffInfo> _effects = new ArrayList<>();
	
	public ExAbnormalStatusUpdateFromTarget(Creature creature)
	{
		_creature = creature;
		for (BuffInfo info : creature.getEffectList().getEffects())
		{
			if ((info != null) && info.isInUse() && !info.getSkill().isToggle())
			{
				_effects.add(info);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET.writeId(this, buffer);
		buffer.writeInt(_creature.getObjectId());
		buffer.writeShort(_effects.size());
		for (BuffInfo info : _effects)
		{
			buffer.writeInt(info.getSkill().getDisplayId());
			buffer.writeShort(info.getSkill().getDisplayLevel());
			buffer.writeShort(info.getSkill().getSubLevel());
			buffer.writeShort(info.getSkill().getAbnormalType().getClientId());
			writeOptionalInt(info.getSkill().isAura() ? -1 : info.getTime(), buffer);
			buffer.writeInt(info.getEffectorObjectId());
		}
	}
}
