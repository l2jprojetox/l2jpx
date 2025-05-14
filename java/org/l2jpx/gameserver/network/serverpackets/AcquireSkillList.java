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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.data.xml.SkillTreeData;
import org.l2jpx.gameserver.model.SkillLearn;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author Sdw, Mobius
 */
public class AcquireSkillList extends ServerPacket
{
	private Player _player;
	private Collection<SkillLearn> _learnable;
	
	public AcquireSkillList(Player player)
	{
		if (!player.isSubclassLocked()) // Changing class.
		{
			_player = player;
			
			if (player.isTransformed())
			{
				_learnable = Collections.emptyList();
			}
			else
			{
				_learnable = SkillTreeData.getInstance().getAvailableSkills(player, player.getClassId(), false, false);
				_learnable.addAll(SkillTreeData.getInstance().getNextAvailableSkills(player, player.getClassId(), false, false));
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (_player == null)
		{
			return;
		}
		
		ServerPackets.ACQUIRE_SKILL_LIST.writeId(this, buffer);
		buffer.writeShort(_learnable.size());
		for (SkillLearn skill : _learnable)
		{
			buffer.writeInt(skill.getSkillId());
			buffer.writeShort(skill.getSkillLevel()); // Main writeD, Classic writeH.
			buffer.writeLong(skill.getLevelUpSp());
			buffer.writeByte(skill.getGetLevel());
			buffer.writeByte(0); // Skill dual class level.
			
			buffer.writeByte(skill.getRequiredItems().size());
			for (ItemHolder item : skill.getRequiredItems())
			{
				buffer.writeInt(item.getId());
				buffer.writeLong(item.getCount());
			}
			
			final List<Skill> removeSkills = new LinkedList<>();
			for (int id : skill.getRemoveSkills())
			{
				final Skill removeSkill = _player.getKnownSkill(id);
				if (removeSkill != null)
				{
					removeSkills.add(removeSkill);
				}
			}
			
			buffer.writeByte(removeSkills.size());
			for (Skill removed : removeSkills)
			{
				buffer.writeInt(removed.getId());
				buffer.writeShort(removed.getLevel()); // Main writeD, Classic writeH.
			}
		}
	}
}
