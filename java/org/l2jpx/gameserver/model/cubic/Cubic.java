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
package org.l2jpx.gameserver.model.cubic;

import java.util.Comparator;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.templates.CubicTemplate;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameManager;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.stats.Stat;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2jpx.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author UnAfraid, Mobius
 */
public class Cubic extends Creature
{
	private final Player _owner;
	private final Player _caster;
	private final CubicTemplate _template;
	private ScheduledFuture<?> _skillUseTask;
	private ScheduledFuture<?> _expireTask;
	
	public Cubic(Player owner, Player caster, CubicTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Cubic);
		_owner = owner;
		_caster = caster == null ? owner : caster;
		_template = template;
		activate();
	}
	
	private void activate()
	{
		_skillUseTask = ThreadPool.scheduleAtFixedRate(this::readyToUseSkill, 0, _template.getDelay() * 1000);
		_expireTask = ThreadPool.schedule(this::deactivate, _template.getDuration() * 1000);
	}
	
	public void deactivate()
	{
		if ((_skillUseTask != null) && !_skillUseTask.isDone())
		{
			_skillUseTask.cancel(true);
		}
		_skillUseTask = null;
		if ((_expireTask != null) && !_expireTask.isDone())
		{
			_expireTask.cancel(true);
		}
		_expireTask = null;
		_owner.getCubics().remove(_template.getId());
		_owner.sendPacket(new ExUserInfoCubic(_owner));
		_owner.broadcastCharInfo();
	}
	
	private void readyToUseSkill()
	{
		switch (_template.getTargetType())
		{
			case TARGET:
			{
				actionToCurrentTarget();
				break;
			}
			case BY_SKILL:
			{
				actionToTargetBySkill();
				break;
			}
			case HEAL:
			{
				actionHeal();
				break;
			}
			case MASTER:
			{
				actionToMaster();
				break;
			}
		}
	}
	
	private CubicSkill chooseSkill()
	{
		final double random = Rnd.nextDouble() * 100;
		double commulativeChance = 0;
		for (CubicSkill cubicSkill : _template.getCubicSkills())
		{
			if ((commulativeChance += cubicSkill.getTriggerRate()) > random)
			{
				return cubicSkill;
			}
		}
		return null;
	}
	
	private void actionToCurrentTarget()
	{
		final CubicSkill skill = chooseSkill();
		final WorldObject target = _owner.getTarget();
		if ((skill != null) && (target != null))
		{
			tryToUseSkill(target, skill);
		}
	}
	
	private void actionToTargetBySkill()
	{
		final CubicSkill skill = chooseSkill();
		if (skill != null)
		{
			switch (skill.getTargetType())
			{
				case TARGET:
				{
					final WorldObject target = _owner.getTarget();
					if (target != null)
					{
						tryToUseSkill(target, skill);
					}
					break;
				}
				case HEAL:
				{
					actionHeal();
					break;
				}
				case MASTER:
				{
					tryToUseSkill(_owner, skill);
					break;
				}
			}
		}
	}
	
	private void actionHeal()
	{
		final double random = Rnd.nextDouble() * 100;
		double commulativeChance = 0;
		for (CubicSkill cubicSkill : _template.getCubicSkills())
		{
			if ((commulativeChance += cubicSkill.getTriggerRate()) > random)
			{
				final Skill skill = cubicSkill.getSkill();
				if ((skill != null) && (Rnd.get(100) < cubicSkill.getSuccessRate()))
				{
					final Party party = _owner.getParty();
					Stream<Creature> stream;
					if (party != null)
					{
						stream = World.getInstance().getVisibleObjectsInRange(_owner, Creature.class, Config.ALT_PARTY_RANGE, c -> (c.getParty() == party) && _template.validateConditions(this, _owner, c) && cubicSkill.validateConditions(this, _owner, c)).stream();
					}
					else
					{
						stream = _owner.getServitorsAndPets().stream().filter(summon -> _template.validateConditions(this, _owner, summon) && cubicSkill.validateConditions(this, _owner, summon)).map(Creature.class::cast);
					}
					
					if (_template.validateConditions(this, _owner, _owner) && cubicSkill.validateConditions(this, _owner, _owner))
					{
						stream = Stream.concat(stream, Stream.of(_owner));
					}
					
					final Creature target = stream.sorted(Comparator.comparingInt(Creature::getCurrentHpPercent)).findFirst().orElse(null);
					if ((target != null) && (!target.isDead())) // Life Cubic should not try to heal dead targets.
					{
						if (Rnd.nextDouble() > (target.getCurrentHp() / target.getMaxHp()))
						{
							activateCubicSkill(skill, target);
						}
						break;
					}
				}
			}
		}
	}
	
	private void actionToMaster()
	{
		final CubicSkill skill = chooseSkill();
		if (skill != null)
		{
			tryToUseSkill(_owner, skill);
		}
	}
	
	private void tryToUseSkill(WorldObject worldObject, CubicSkill cubicSkill)
	{
		WorldObject target = worldObject;
		final Skill skill = cubicSkill.getSkill();
		if ((_template.getTargetType() != CubicTargetType.MASTER) && !((_template.getTargetType() == CubicTargetType.BY_SKILL) && (cubicSkill.getTargetType() == CubicTargetType.MASTER)))
		{
			target = skill.getTarget(_owner, target, false, false, false);
		}
		
		if (target != null)
		{
			if (target.isDoor() && !cubicSkill.canUseOnStaticObjects())
			{
				return;
			}
			
			if (_template.validateConditions(this, _owner, target) && cubicSkill.validateConditions(this, _owner, target) && (Rnd.get(100) < cubicSkill.getSuccessRate()))
			{
				activateCubicSkill(skill, target);
			}
		}
	}
	
	private void activateCubicSkill(Skill skill, WorldObject target)
	{
		if (!_owner.hasSkillReuse(skill.getReuseHashCode()))
		{
			_caster.broadcastPacket(new MagicSkillUse(_owner, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), skill.getReuseDelay()));
			skill.activateSkill(this, target);
			_owner.addTimeStamp(skill, skill.getReuseDelay());
		}
	}
	
	@Override
	public void sendDamageMessage(Creature target, Skill skill, int damage, boolean crit, boolean miss)
	{
		if (miss || (_owner == null))
		{
			return;
		}
		
		if (_owner.isInOlympiadMode() && target.isPlayer() && ((Player) target).isInOlympiadMode() && (((Player) target).getOlympiadGameId() == _owner.getOlympiadGameId()))
		{
			OlympiadGameManager.getInstance().notifyCompetitorDamage(_owner, damage);
		}
		
		if (target.isHpBlocked() && !target.isNpc())
		{
			_owner.sendPacket(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
		}
		else
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
			sm.addString(getName());
			sm.addString(target.getName());
			sm.addInt(damage);
			sm.addPopup(target.getObjectId(), _owner.getObjectId(), (damage * -1));
			_owner.sendPacket(sm);
		}
	}
	
	@Override
	public void sendPacket(ServerPacket packet)
	{
		if (_owner != null)
		{
			_owner.sendPacket(packet);
		}
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	/**
	 * @return the {@link Creature} that casted this cubic
	 */
	public Creature getCaster()
	{
		return _caster;
	}
	
	/**
	 * @return {@code true} if cubic is casted from someone else but the owner, {@code false}
	 */
	public boolean isGivenByOther()
	{
		return _caster != _owner;
	}
	
	/**
	 * @return the owner's name.
	 */
	@Override
	public String getName()
	{
		return _owner.getName();
	}
	
	/**
	 * @return the owner's level.
	 */
	@Override
	public int getLevel()
	{
		return _owner.getLevel();
	}
	
	@Override
	public int getX()
	{
		return _owner.getX();
	}
	
	@Override
	public int getY()
	{
		return _owner.getY();
	}
	
	@Override
	public int getZ()
	{
		return _owner.getZ();
	}
	
	@Override
	public int getHeading()
	{
		return _owner.getHeading();
	}
	
	@Override
	public int getInstanceId()
	{
		return _owner.getInstanceId();
	}
	
	@Override
	public boolean isInInstance()
	{
		return _owner.isInInstance();
	}
	
	@Override
	public Instance getInstanceWorld()
	{
		return _owner.getInstanceWorld();
	}
	
	@Override
	public Location getLocation()
	{
		return _owner.getLocation();
	}
	
	@Override
	public double getRandomDamageMultiplier()
	{
		final int random = (int) _owner.getStat().getValue(Stat.RANDOM_DAMAGE);
		return (1 + ((double) Rnd.get(-random, random) / 100));
	}
	
	@Override
	public int getMagicAccuracy()
	{
		return _owner.getMagicAccuracy();
	}
	
	/**
	 * @return the {@link CubicTemplate} of this cubic
	 */
	@Override
	public CubicTemplate getTemplate()
	{
		return _template;
	}
	
	@Override
	public int getId()
	{
		return _template.getId();
	}
	
	@Override
	public int getPAtk()
	{
		return _template.getBasePAtk();
	}
	
	@Override
	public int getMAtk()
	{
		return _template.getBaseMAtk();
	}
	
	@Override
	public Item getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public Item getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public ItemTemplate getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}
	
	@Override
	public boolean spawnMe()
	{
		return true;
	}
	
	@Override
	public void onSpawn()
	{
	}
	
	@Override
	public boolean deleteMe()
	{
		return true;
	}
	
	@Override
	public boolean decayMe()
	{
		return true;
	}
	
	@Override
	public void onDecay()
	{
	}
	
	@Override
	public synchronized void onTeleported()
	{
	}
	
	@Override
	public void sendInfo(Player player)
	{
	}
	
	@Override
	public boolean isInvul()
	{
		return _owner.isInvul();
	}
	
	@Override
	public boolean isTargetable()
	{
		return false;
	}
	
	@Override
	public boolean isUndying()
	{
		return true;
	}
	
	/**
	 * Considered a player in order to send messages, calculate magic fail formula etc...
	 */
	@Override
	public boolean isPlayer()
	{
		return true;
	}
	
	@Override
	public boolean isCubic()
	{
		return true;
	}
}
