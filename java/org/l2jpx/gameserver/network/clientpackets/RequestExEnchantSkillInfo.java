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

import java.util.Set;

import org.l2jpx.gameserver.data.xml.EnchantSkillGroupsData;
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.enums.CategoryType;
import org.l2jpx.gameserver.enums.SkillEnchantType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.serverpackets.ExEnchantSkillInfo;
import org.l2jpx.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

/**
 * Format (ch) dd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill level
 * @author -Wooden-
 */
public class RequestExEnchantSkillInfo extends ClientPacket
{
	private int _skillId;
	private int _skillLevel;
	private int _skillSubLevel;
	
	@Override
	protected void readImpl()
	{
		_skillId = readInt();
		_skillLevel = readShort();
		_skillSubLevel = readShort();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_skillId <= 0) || (_skillLevel <= 0) || (_skillSubLevel < 0))
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel, _skillSubLevel);
		if ((skill == null) || (skill.getId() != _skillId))
		{
			return;
		}
		final Set<Integer> route = EnchantSkillGroupsData.getInstance().getRouteForSkill(_skillId, _skillLevel);
		if (route.isEmpty())
		{
			return;
		}
		
		final Skill playerSkill = player.getKnownSkill(_skillId);
		if ((playerSkill.getLevel() != _skillLevel) || (playerSkill.getSubLevel() != _skillSubLevel))
		{
			return;
		}
		
		player.sendPacket(new ExEnchantSkillInfo(_skillId, _skillLevel, _skillSubLevel, playerSkill.getSubLevel()));
		// ExEnchantSkillInfoDetail - not really necessary I think
		player.sendPacket(new ExEnchantSkillInfoDetail(SkillEnchantType.NORMAL, _skillId, _skillLevel, _skillSubLevel, player));
	}
}