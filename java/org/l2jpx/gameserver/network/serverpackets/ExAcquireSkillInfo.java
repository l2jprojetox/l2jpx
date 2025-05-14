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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.SkillLearn;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * @author UnAfraid
 */
public class ExAcquireSkillInfo extends ServerPacket
{
	private final int _id;
	private final int _level;
	private final int _dualClassLevel;
	private final long _spCost;
	private final int _minLevel;
	private final Set<ItemHolder> _itemReq;
	private final List<Skill> _skillRem = new LinkedList<>();
	
	/**
	 * Special constructor for Alternate Skill Learning system.<br>
	 * Sets a custom amount of SP.
	 * @param player
	 * @param skillLearn the skill learn.
	 */
	public ExAcquireSkillInfo(Player player, SkillLearn skillLearn)
	{
		_id = skillLearn.getSkillId();
		_level = skillLearn.getSkillLevel();
		_dualClassLevel = skillLearn.getDualClassLevel();
		_spCost = skillLearn.getLevelUpSp();
		_minLevel = skillLearn.getGetLevel();
		_itemReq = skillLearn.getRequiredItems();
		for (int id : skillLearn.getRemoveSkills())
		{
			final Skill removeSkill = player.getKnownSkill(id);
			if (removeSkill != null)
			{
				_skillRem.add(removeSkill);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ACQUIRE_SKILL_INFO.writeId(this, buffer);
		buffer.writeInt(_id);
		buffer.writeInt(_level);
		buffer.writeLong(_spCost);
		buffer.writeShort(_minLevel);
		buffer.writeShort(_dualClassLevel);
		buffer.writeInt(_itemReq.size());
		for (ItemHolder holder : _itemReq)
		{
			buffer.writeInt(holder.getId());
			buffer.writeLong(holder.getCount());
		}
		buffer.writeInt(_skillRem.size());
		for (Skill skill : _skillRem)
		{
			buffer.writeInt(skill.getId());
			buffer.writeInt(skill.getLevel());
		}
	}
}
