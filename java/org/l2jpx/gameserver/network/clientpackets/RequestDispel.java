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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.enums.SkillFinishType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.skill.AbnormalType;
import org.l2jpx.gameserver.model.skill.Skill;

/**
 * @author KenM
 */
public class RequestDispel extends ClientPacket
{
	private int _objectId;
	private int _skillId;
	private int _skillLevel;
	private int _skillSubLevel;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_skillId = readInt();
		_skillLevel = readShort();
		_skillSubLevel = readShort();
	}
	
	@Override
	protected void runImpl()
	{
		if ((_skillId <= 0) || (_skillLevel <= 0))
		{
			return;
		}
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel, _skillSubLevel);
		if (skill == null)
		{
			return;
		}
		if (!skill.canBeDispelled() || skill.isDebuff())
		{
			return;
		}
		if (skill.getAbnormalType() == AbnormalType.TRANSFORM)
		{
			return;
		}
		if (skill.isDance() && !Config.DANCE_CANCEL_BUFF)
		{
			return;
		}
		if (player.getObjectId() == _objectId)
		{
			player.stopSkillEffects(SkillFinishType.REMOVED, _skillId);
		}
		else
		{
			final Summon pet = player.getPet();
			if ((pet != null) && (pet.getObjectId() == _objectId))
			{
				pet.stopSkillEffects(SkillFinishType.REMOVED, _skillId);
			}
			
			final Summon servitor = player.getServitor(_objectId);
			if (servitor != null)
			{
				servitor.stopSkillEffects(SkillFinishType.REMOVED, _skillId);
			}
		}
	}
}
