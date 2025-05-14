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

import org.l2jpx.gameserver.data.xml.SkillData;
import org.l2jpx.gameserver.data.xml.SkillTreeData;
import org.l2jpx.gameserver.enums.AcquireSkillType;
import org.l2jpx.gameserver.enums.CategoryType;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.model.SkillLearn;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.clan.ClanPrivilege;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.serverpackets.AcquireSkillInfo;
import org.l2jpx.gameserver.network.serverpackets.ExAcquireSkillInfo;

/**
 * Request Acquire Skill Info client packet implementation.
 * @author Zoey76
 */
public class RequestAcquireSkillInfo extends ClientPacket
{
	private int _id;
	private int _level;
	private AcquireSkillType _skillType;
	
	@Override
	protected void readImpl()
	{
		_id = readInt();
		_level = readInt();
		_skillType = AcquireSkillType.getAcquireSkillType(readInt());
	}
	
	@Override
	protected void runImpl()
	{
		if ((_id <= 0) || (_level <= 0))
		{
			PacketLogger.warning(RequestAcquireSkillInfo.class.getSimpleName() + ": Invalid Id: " + _id + " or level: " + _level + "!");
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Npc trainer = player.getLastFolkNPC();
		if ((_skillType != AcquireSkillType.CLASS) && ((trainer == null) || !trainer.isNpc() || (!trainer.canInteract(player) && !player.isGM())))
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_id, _level);
		if (skill == null)
		{
			PacketLogger.warning("Skill Id: " + _id + " level: " + _level + " is undefined. " + RequestAcquireSkillInfo.class.getName() + " failed.");
			return;
		}
		
		final SkillLearn s = SkillTreeData.getInstance().getSkillLearn(_skillType, _id, _level, player);
		if (s == null)
		{
			return;
		}
		
		switch (_skillType)
		{
			case TRANSFORM:
			case FISHING:
			case SUBCLASS:
			case COLLECT:
			case TRANSFER:
			case DUALCLASS:
			{
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case CLASS:
			{
				player.sendPacket(new ExAcquireSkillInfo(player, s));
				break;
			}
			case PLEDGE:
			{
				if (!player.isClanLeader())
				{
					return;
				}
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case SUBPLEDGE:
			{
				if (!player.isClanLeader() || !player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
				{
					return;
				}
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case ALCHEMY:
			{
				if (player.getRace() != Race.ERTHEIA)
				{
					return;
				}
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case REVELATION:
			{
				if ((player.getLevel() < 85) || !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					return;
				}
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case REVELATION_DUALCLASS:
			{
				if (!player.isSubClassActive() || !player.isDualClassActive())
				{
					return;
				}
				player.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
		}
	}
}
