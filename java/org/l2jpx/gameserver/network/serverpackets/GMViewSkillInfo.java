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
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

public class GMViewSkillInfo extends ServerPacket
{
	private final Player _player;
	private final Collection<Skill> _skills;
	
	public GMViewSkillInfo(Player player)
	{
		_player = player;
		_skills = _player.getSkillList();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_SKILL_INFO.writeId(this, buffer);
		buffer.writeString(_player.getName());
		buffer.writeInt(_skills.size());
		final boolean isDisabled = (_player.getClan() != null) && (_player.getClan().getReputationScore() < 0);
		for (Skill skill : _skills)
		{
			buffer.writeInt(skill.isPassive());
			buffer.writeShort(skill.getDisplayLevel());
			buffer.writeShort(skill.getSubLevel());
			buffer.writeInt(skill.getDisplayId());
			buffer.writeInt(0);
			buffer.writeByte(isDisabled && skill.isClanSkill());
			buffer.writeByte(skill.isEnchantable());
		}
	}
}