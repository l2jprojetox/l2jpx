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

import java.util.Set;

import org.l2jpx.commons.network.WritableBuffer;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.actor.instance.Pet;
import org.l2jpx.gameserver.model.actor.instance.Servitor;
import org.l2jpx.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jpx.gameserver.network.GameClient;
import org.l2jpx.gameserver.network.ServerPackets;
import org.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

public class PetSummonInfo extends ServerPacket
{
	private final Summon _summon;
	private final int _value;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flRunSpd = 0;
	private final int _flWalkSpd = 0;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private int _maxFed;
	private int _curFed;
	private int _statusMask = 0;
	
	public PetSummonInfo(Summon summon, int value)
	{
		_summon = summon;
		_moveMultiplier = summon.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(summon.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(summon.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(summon.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(summon.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = summon.isFlying() ? _runSpd : 0;
		_flyWalkSpd = summon.isFlying() ? _walkSpd : 0;
		_value = value;
		if (summon.isPet())
		{
			final Pet pet = (Pet) _summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); // max fed it can be
		}
		else if (summon.isServitor())
		{
			final Servitor sum = (Servitor) _summon;
			_curFed = sum.getLifeTimeRemaining();
			_maxFed = sum.getLifeTime();
		}
		if (summon.isBetrayed())
		{
			_statusMask |= 0x01; // Auto attackable status
		}
		_statusMask |= 0x02; // can be chatted with
		if (summon.isRunning())
		{
			_statusMask |= 0x04;
		}
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(summon))
		{
			_statusMask |= 0x08;
		}
		if (summon.isDead())
		{
			_statusMask |= 0x10;
		}
		if (summon.isMountable())
		{
			_statusMask |= 0x20;
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PET_INFO.writeId(this, buffer);
		buffer.writeByte(_summon.getSummonType());
		buffer.writeInt(_summon.getObjectId());
		buffer.writeInt(_summon.getTemplate().getDisplayId() + 1000000);
		buffer.writeInt(_summon.getX());
		buffer.writeInt(_summon.getY());
		buffer.writeInt(_summon.getZ());
		buffer.writeInt(_summon.getHeading());
		buffer.writeInt(_summon.getStat().getMAtkSpd());
		buffer.writeInt(_summon.getStat().getPAtkSpd());
		buffer.writeShort(_runSpd);
		buffer.writeShort(_walkSpd);
		buffer.writeShort(_swimRunSpd);
		buffer.writeShort(_swimWalkSpd);
		buffer.writeShort(_flRunSpd);
		buffer.writeShort(_flWalkSpd);
		buffer.writeShort(_flyRunSpd);
		buffer.writeShort(_flyWalkSpd);
		buffer.writeDouble(_moveMultiplier);
		buffer.writeDouble(_summon.getAttackSpeedMultiplier()); // attack speed multiplier
		buffer.writeDouble(_summon.getTemplate().getFCollisionRadius());
		buffer.writeDouble(_summon.getTemplate().getFCollisionHeight());
		buffer.writeInt(_summon.getWeapon()); // right hand weapon
		buffer.writeInt(_summon.getArmor()); // body armor
		buffer.writeInt(0); // left hand weapon
		buffer.writeByte(_summon.isShowSummonAnimation() ? 2 : _value); // 0=teleported 1=default 2=summoned
		buffer.writeInt(-1); // High Five NPCString ID
		if (_summon.isPet())
		{
			buffer.writeString(_summon.getName()); // Pet name.
		}
		else
		{
			buffer.writeString(_summon.getTemplate().isUsingServerSideName() ? _summon.getName() : ""); // Summon name.
		}
		buffer.writeInt(-1); // High Five NPCString ID
		buffer.writeString(_summon.getTitle()); // owner name
		buffer.writeByte(_summon.getPvpFlag()); // confirmed
		buffer.writeInt(_summon.getReputation()); // confirmed
		buffer.writeInt(_curFed); // how fed it is
		buffer.writeInt(_maxFed); // max fed it can be
		buffer.writeInt((int) _summon.getCurrentHp()); // current hp
		buffer.writeInt(_summon.getMaxHp()); // max hp
		buffer.writeInt((int) _summon.getCurrentMp()); // current mp
		buffer.writeInt(_summon.getMaxMp()); // max mp
		buffer.writeLong(_summon.getStat().getSp()); // sp
		buffer.writeByte(_summon.getLevel()); // level
		buffer.writeLong(_summon.getStat().getExp());
		if (_summon.getExpForThisLevel() > _summon.getStat().getExp())
		{
			buffer.writeLong(_summon.getStat().getExp()); // 0% absolute value
		}
		else
		{
			buffer.writeLong(_summon.getExpForThisLevel()); // 0% absolute value
		}
		buffer.writeLong(_summon.getExpForNextLevel()); // 100% absoulte value
		buffer.writeInt(_summon.isPet() ? _summon.getInventory().getTotalWeight() : 0); // weight
		buffer.writeInt(_summon.getMaxLoad()); // max weight it can carry
		buffer.writeInt(_summon.getPAtk()); // patk
		buffer.writeInt(_summon.getPDef()); // pdef
		buffer.writeInt(_summon.getAccuracy()); // accuracy
		buffer.writeInt(_summon.getEvasionRate()); // evasion
		buffer.writeInt(_summon.getCriticalHit()); // critical
		buffer.writeInt(_summon.getMAtk()); // matk
		buffer.writeInt(_summon.getMDef()); // mdef
		buffer.writeInt(_summon.getMagicAccuracy()); // magic accuracy
		buffer.writeInt(_summon.getMagicEvasionRate()); // magic evasion
		buffer.writeInt(_summon.getMCriticalHit()); // mcritical
		buffer.writeInt((int) _summon.getStat().getMoveSpeed()); // speed
		buffer.writeInt(_summon.getPAtkSpd()); // atkspeed
		buffer.writeInt(_summon.getMAtkSpd()); // casting speed
		buffer.writeByte(0); // TODO: Check me, might be ride status
		buffer.writeByte(_summon.getTeam().getId()); // Confirmed
		buffer.writeByte(_summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit - Confirmed
		buffer.writeByte(_summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit - - Confirmed
		buffer.writeInt(0); // TODO: Find me
		buffer.writeInt(0); // "Transformation ID - Confirmed" - Used to bug Fenrir after 64 level.
		buffer.writeByte(_summon.getOwner().getSummonPoints()); // Used Summon Points
		buffer.writeByte(_summon.getOwner().getMaxSummonPoints()); // Maximum Summon Points
		final Set<AbnormalVisualEffect> aves = _summon.getEffectList().getCurrentAbnormalVisualEffects();
		buffer.writeShort(aves.size()); // Confirmed
		for (AbnormalVisualEffect ave : aves)
		{
			buffer.writeShort(ave.getClientId()); // Confirmed
		}
		buffer.writeByte(_statusMask);
	}
}
