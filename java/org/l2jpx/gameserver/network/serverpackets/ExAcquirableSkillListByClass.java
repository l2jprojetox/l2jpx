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

import java.util.Collection;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.enums.AcquireSkillType;
import org.l2jpx.gameserver.model.SkillLearn;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author UnAfraid
 */
public class ExAcquirableSkillListByClass extends ServerPacket
{
	private final Collection<SkillLearn> _learnable;
	private final AcquireSkillType _type;
	
	public ExAcquirableSkillListByClass(Collection<SkillLearn> learnable, AcquireSkillType type)
	{
		_learnable = learnable;
		_type = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS.writeId(this, buffer);
		buffer.writeShort(_type.getId());
		buffer.writeShort(_learnable.size());
		for (SkillLearn skill : _learnable)
		{
			buffer.writeInt(skill.getSkillId());
			buffer.writeShort(skill.getSkillLevel());
			buffer.writeShort(skill.getSkillLevel());
			buffer.writeByte(skill.getGetLevel());
			buffer.writeLong(skill.getLevelUpSp());
			buffer.writeByte(skill.getRequiredItems().size());
			if (_type == AcquireSkillType.SUBPLEDGE)
			{
				buffer.writeShort(0);
			}
		}
	}
}
