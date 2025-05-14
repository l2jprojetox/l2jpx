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
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.skill.SkillCastingType;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;

/**
 * MagicSkillUse server packet implementation.
 * @author UnAfraid, NosBit, Mobius
 */
public class MagicSkillUse extends ServerPacket
{
	private final int _skillId;
	private final int _skillLevel;
	private final int _hitTime;
	private final int _reuseGroup;
	private final int _reuseDelay;
	private final int _actionId; // If skill is called from RequestActionUse, use that ID.
	private final SkillCastingType _castingType; // Defines which client bar is going to use.
	private final Creature _creature;
	private final WorldObject _target;
	private final Location _groundLocation;
	
	public MagicSkillUse(Creature creature, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int actionId, SkillCastingType castingType)
	{
		_creature = creature;
		_target = target;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseGroup = reuseGroup;
		_reuseDelay = reuseDelay;
		_actionId = actionId;
		_castingType = castingType;
		_groundLocation = creature.isPlayer() ? creature.getActingPlayer().getCurrentSkillWorldPosition() : null;
	}
	
	public MagicSkillUse(Creature creature, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this(creature, target, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
	}
	
	public MagicSkillUse(Creature creature, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this(creature, creature, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MAGIC_SKILL_USE.writeId(this, buffer);
		buffer.writeInt(_castingType.getClientBarId()); // Casting bar type: 0 - default, 1 - default up, 2 - blue, 3 - green, 4 - red.
		buffer.writeInt(_creature.getObjectId());
		buffer.writeInt(_target.getObjectId());
		buffer.writeInt(_skillId);
		buffer.writeInt(_skillLevel);
		buffer.writeInt(_hitTime);
		buffer.writeInt(_reuseGroup);
		buffer.writeInt(_reuseDelay);
		buffer.writeInt(_creature.getX());
		buffer.writeInt(_creature.getY());
		buffer.writeInt(_creature.getZ());
		buffer.writeShort(0); // isGroundTargetSkill ? 65535 : 0
		if (_groundLocation == null)
		{
			buffer.writeShort(0);
		}
		else
		{
			buffer.writeShort(1);
			buffer.writeInt(_groundLocation.getX());
			buffer.writeInt(_groundLocation.getY());
			buffer.writeInt(_groundLocation.getZ());
		}
		buffer.writeInt(_target.getX());
		buffer.writeInt(_target.getY());
		buffer.writeInt(_target.getZ());
		buffer.writeInt(_actionId >= 0); // 1 when ID from RequestActionUse is used
		buffer.writeInt(_actionId >= 0 ? _actionId : 0); // ID from RequestActionUse. Used to set cooldown on summon skills.
	}
}
