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
package org.l2jpx.gameserver.model.actor;

import java.util.concurrent.ScheduledFuture;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.ai.CreatureAI;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.ai.SummonAI;
import org.l2jpx.gameserver.data.sql.CharSummonTable;
import org.l2jpx.gameserver.data.xml.ExperienceData;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.NpcInfoType;
import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.enums.Team;
import org.l2jpx.gameserver.geoengine.GeoEngine;
import org.l2jpx.gameserver.handler.IItemHandler;
import org.l2jpx.gameserver.handler.ItemHandler;
import org.l2jpx.gameserver.instancemanager.ZoneManager;
import org.l2jpx.gameserver.model.AggroInfo;
import org.l2jpx.gameserver.model.Location;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.stat.SummonStat;
import org.l2jpx.gameserver.model.actor.status.SummonStatus;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.effects.EffectFlag;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerSummonSpawn;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.Weapon;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.ActionType;
import org.l2jpx.gameserver.model.itemcontainer.PetInventory;
import org.l2jpx.gameserver.model.olympiad.OlympiadGameManager;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.skill.SkillCaster;
import org.l2jpx.gameserver.model.skill.targets.TargetType;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.model.zone.ZoneRegion;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.AbstractMaskPacket;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.ExPartyPetWindowAdd;
import org.l2jpx.gameserver.network.serverpackets.ExPartyPetWindowDelete;
import org.l2jpx.gameserver.network.serverpackets.ExPartyPetWindowUpdate;
import org.l2jpx.gameserver.network.serverpackets.ExPetInfo;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.PetDelete;
import org.l2jpx.gameserver.network.serverpackets.PetItemList;
import org.l2jpx.gameserver.network.serverpackets.PetStatusUpdate;
import org.l2jpx.gameserver.network.serverpackets.PetSummonInfo;
import org.l2jpx.gameserver.network.serverpackets.RelationChanged;
import org.l2jpx.gameserver.network.serverpackets.ServerPacket;
import org.l2jpx.gameserver.network.serverpackets.SummonInfo;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.TeleportToLocation;
import org.l2jpx.gameserver.taskmanager.DecayTaskManager;

public abstract class Summon extends Playable
{
	private Player _owner;
	private int _attackRange = 36; // Melee range
	private boolean _follow = true;
	private boolean _previousFollowStatus = true;
	protected boolean _restoreSummon = true;
	private int _summonPoints = 0;
	private ScheduledFuture<?> _abnormalEffectTask;
	
	// @formatter:off
	private static final int[] PASSIVE_SUMMONS =
	{
		12564, 12621, 14702, 14703, 14704, 14705, 14706, 14707, 14708, 14709, 14710, 14711,
		14712, 14713, 14714, 14715, 14716, 14717, 14718, 14719, 14720, 14721, 14722, 14723,
		14724, 14725, 14726, 14727, 14728, 14729, 14730, 14731, 14732, 14733, 14734, 14735, 14736
	};
	// @formatter:on
	
	public Summon(NpcTemplate template, Player owner)
	{
		super(template);
		setInstanceType(InstanceType.Summon);
		setInstance(owner.getInstanceWorld()); // set instance to same as owner
		setShowSummonAnimation(true);
		_owner = owner;
		getAI();
		
		// Make sure summon does not spawn in a wall.
		final int x = owner.getX();
		final int y = owner.getY();
		final int z = owner.getZ();
		final Location location = GeoEngine.getInstance().getValidLocation(x, y, z, x + Rnd.get(-100, 100), y + Rnd.get(-100, 100), z, getInstanceWorld());
		setXYZInvisible(location.getX(), location.getY(), location.getZ());
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (Config.SUMMON_STORE_SKILL_COOLTIME && !isTeleporting())
		{
			restoreEffects();
		}
		
		setFollowStatus(true);
		updateAndBroadcastStatus(0);
		sendPacket(new RelationChanged(this, _owner.getRelation(_owner), false));
		World.getInstance().forEachVisibleObject(getOwner(), Player.class, player -> player.sendPacket(new RelationChanged(this, _owner.getRelation(player), isAutoAttackable(player))));
		final Party party = _owner.getParty();
		if (party != null)
		{
			party.broadcastToPartyMembers(_owner, new ExPartyPetWindowAdd(this));
		}
		setShowSummonAnimation(false); // addVisibleObject created the info packets with summon animation
		// if someone comes into range now, the animation shouldn't show any more
		_restoreSummon = false;
		rechargeShots(true, true, false);
		
		// Notify to scripts
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_SUMMON_SPAWN, this))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonSpawn(this), this);
		}
	}
	
	@Override
	public SummonStat getStat()
	{
		return (SummonStat) super.getStat();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new SummonStat(this));
	}
	
	@Override
	public SummonStatus getStatus()
	{
		return (SummonStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new SummonStatus(this));
	}
	
	@Override
	protected CreatureAI initAI()
	{
		return new SummonAI(this);
	}
	
	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getSummonType();
	
	@Override
	public void stopAllEffects()
	{
		super.stopAllEffects();
		updateAndBroadcastStatus(1);
	}
	
	@Override
	public void stopAllEffectsExceptThoseThatLastThroughDeath()
	{
		super.stopAllEffectsExceptThoseThatLastThroughDeath();
		updateAndBroadcastStatus(1);
	}
	
	@Override
	public void updateAbnormalVisualEffects()
	{
		if (_abnormalEffectTask == null)
		{
			_abnormalEffectTask = ThreadPool.schedule(() ->
			{
				if (isSpawned())
				{
					World.getInstance().forEachVisibleObject(this, Player.class, player ->
					{
						if (player == _owner)
						{
							player.sendPacket(new PetSummonInfo(this, 1));
							return;
						}
						
						final AbstractMaskPacket<NpcInfoType> packet;
						if (isPet())
						{
							packet = new ExPetInfo(this, player, 1);
						}
						else
						{
							packet = new SummonInfo(this, player, 1);
						}
						packet.addComponentType(NpcInfoType.ABNORMALS);
						player.sendPacket(packet);
					});
				}
				_abnormalEffectTask = null;
			}, 50);
		}
	}
	
	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}
	
	public long getExpForThisLevel()
	{
		if (getLevel() >= ExperienceData.getInstance().getMaxPetLevel())
		{
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getLevel());
	}
	
	public long getExpForNextLevel()
	{
		if (getLevel() >= (ExperienceData.getInstance().getMaxPetLevel() - 1))
		{
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getLevel() + 1);
	}
	
	@Override
	public int getReputation()
	{
		return _owner != null ? _owner.getReputation() : 0;
	}
	
	@Override
	public byte getPvpFlag()
	{
		return _owner != null ? _owner.getPvpFlag() : 0;
	}
	
	@Override
	public Team getTeam()
	{
		return _owner != null ? _owner.getTeam() : Team.NONE;
	}
	
	public Player getOwner()
	{
		return _owner;
	}
	
	/**
	 * Gets the summon ID.
	 * @return the summon ID
	 */
	@Override
	public int getId()
	{
		return getTemplate().getId();
	}
	
	public short getSoulShotsPerHit()
	{
		if (getTemplate().getSoulShot() > 0)
		{
			return (short) getTemplate().getSoulShot();
		}
		return 1;
	}
	
	public short getSpiritShotsPerHit()
	{
		if (getTemplate().getSpiritShot() > 0)
		{
			return (short) getTemplate().getSpiritShot();
		}
		return 1;
	}
	
	public void followOwner()
	{
		setFollowStatus(true);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (isNoblesseBlessedAffected())
		{
			stopEffects(EffectFlag.NOBLESS_BLESSING);
			storeEffect(true);
		}
		else
		{
			storeEffect(false);
		}
		
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (_owner != null)
		{
			World.getInstance().forEachVisibleObject(this, Attackable.class, target ->
			{
				if (target.isDead())
				{
					return;
				}
				
				final AggroInfo info = target.getAggroList().get(this);
				if (info != null)
				{
					target.addDamageHate(_owner, info.getDamage(), info.getHate());
				}
			});
		}
		
		DecayTaskManager.getInstance().add(this);
		return true;
	}
	
	public boolean doDie(Creature killer, boolean decayed)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		if (!decayed)
		{
			DecayTaskManager.getInstance().add(this);
		}
		return true;
	}
	
	public void stopDecay()
	{
		DecayTaskManager.getInstance().cancel(this);
	}
	
	@Override
	public void onDecay()
	{
		unSummon(_owner);
		deleteMe(_owner);
	}
	
	@Override
	public void broadcastStatusUpdate(Creature caster)
	{
		super.broadcastStatusUpdate(caster);
		updateAndBroadcastStatus(1);
	}
	
	public void deleteMe(Player owner)
	{
		super.deleteMe();
		
		if (owner != null)
		{
			owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
			final Party party = owner.getParty();
			if (party != null)
			{
				party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
			}
			
			if (isPet())
			{
				owner.setPet(null);
			}
			else
			{
				owner.removeServitor(getObjectId());
			}
		}
		
		// Pet will be deleted along with all his items.
		if (getInventory() != null)
		{
			getInventory().destroyAllItems("pet deleted", _owner, this);
		}
		
		decayMe();
		
		if (!isPet())
		{
			CharSummonTable.getInstance().removeServitor(_owner, getObjectId());
		}
	}
	
	public void unSummon(Player owner)
	{
		if (isSpawned())
		{
			if (isDead())
			{
				stopDecay();
			}
			
			// Prevent adding effects while unsummoning.
			setInvul(true);
			
			abortAttack();
			abortCast();
			storeMe();
			storeEffect(true);
			
			// Stop AI tasks
			if (hasAI())
			{
				getAI().stopAITask(); // Calls stopFollow as well.
			}
			
			// Cancel running skill casters.
			abortAllSkillCasters();
			
			stopAllEffects();
			stopHpMpRegeneration();
			
			if (owner != null)
			{
				if (isPet())
				{
					owner.setPet(null);
				}
				else
				{
					owner.removeServitor(getObjectId());
				}
				
				owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
				final Party party = owner.getParty();
				if (party != null)
				{
					party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
				}
				
				if ((getInventory() != null) && (getInventory().getSize() > 0))
				{
					_owner.setPetInvItems(true);
					sendPacket(SystemMessageId.THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELL_TRADE_DROP_PET_SUMMONING_ITEMS_PLEASE_EMPTY_YOUR_PET_INVENTORY);
				}
				else
				{
					_owner.setPetInvItems(false);
				}
			}
			
			final ZoneRegion oldRegion = ZoneManager.getInstance().getRegion(this);
			decayMe();
			oldRegion.removeFromZones(this);
			
			setTarget(null);
			if (owner != null)
			{
				for (int itemId : owner.getAutoSoulShot())
				{
					final String handler = ((EtcItem) ItemData.getInstance().getTemplate(itemId)).getHandlerName();
					if ((handler != null) && handler.contains("Beast"))
					{
						owner.disableAutoShot(itemId);
					}
				}
			}
		}
	}
	
	public int getAttackRange()
	{
		return _attackRange;
	}
	
	public void setAttackRange(int range)
	{
		_attackRange = (range < 36) ? 36 : range;
	}
	
	public void setFollowStatus(boolean value)
	{
		_follow = value;
		if (_follow)
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _owner);
		}
		else
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
	}
	
	public boolean getFollowStatus()
	{
		return _follow;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return (_owner != null) && _owner.isAutoAttackable(attacker);
	}
	
	public int getControlObjectId()
	{
		return 0;
	}
	
	public Weapon getActiveWeapon()
	{
		return null;
	}
	
	@Override
	public PetInventory getInventory()
	{
		return null;
	}
	
	public void setRestoreSummon(boolean value)
	{
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
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	/**
	 * Return True if the Summon is invulnerable or if the summoner is in spawn protection.
	 */
	@Override
	public boolean isInvul()
	{
		return super.isInvul() || _owner.isSpawnProtected();
	}
	
	/**
	 * Return the Party object of its Player owner or null.
	 */
	@Override
	public Party getParty()
	{
		if (_owner == null)
		{
			return null;
		}
		return _owner.getParty();
	}
	
	/**
	 * Return True if the Creature has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		return (_owner != null) && _owner.isInParty();
	}
	
	/**
	 * Check if the active Skill can be casted.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Check if the target is correct</li>
	 * <li>Check if the target is in the skill cast range</li>
	 * <li>Check if the summon owns enough HP and MP to cast the skill</li>
	 * <li>Check if all skills are enabled and this skill is enabled</li>
	 * <li>Check if the skill is active</li>
	 * <li>Notify the AI with AI_INTENTION_CAST and target</li>
	 * </ul>
	 * @param skill The Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	@Override
	public boolean useMagic(Skill skill, Item item, boolean forceUse, boolean dontMove)
	{
		// Null skill, dead summon or null owner are reasons to prevent casting.
		if ((skill == null) || isDead() || (_owner == null))
		{
			return false;
		}
		
		// Check if the skill is active
		if (skill.isPassive())
		{
			// just ignore the passive skill request. why does the client send it anyway ??
			return false;
		}
		
		// If a skill is currently being used
		if (isCastingNow(SkillCaster::isAnyNormalType))
		{
			return false;
		}
		
		// Get the target for the skill
		final WorldObject target;
		if (skill.getTargetType() == TargetType.OWNER_PET)
		{
			target = _owner;
		}
		else
		{
			final WorldObject currentTarget = _owner.getTarget();
			if (currentTarget != null)
			{
				target = skill.getTarget(this, forceUse && (!currentTarget.isPlayable() || !currentTarget.isInsideZone(ZoneId.PEACE) || !currentTarget.isInsideZone(ZoneId.NO_PVP)), dontMove, false);
				final Player currentTargetPlayer = currentTarget.getActingPlayer();
				if (!forceUse && (currentTargetPlayer != null) && !currentTargetPlayer.isAutoAttackable(_owner))
				{
					sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
			}
			else
			{
				target = skill.getTarget(this, forceUse, dontMove, false);
			}
		}
		
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
			return false;
		}
		
		// Check if this skill is enabled (e.g. reuse time)
		if (isSkillDisabled(skill))
		{
			sendPacket(SystemMessageId.THAT_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING);
			return false;
		}
		
		// Check if the summon has enough MP
		if (getCurrentMp() < (getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill)))
		{
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_MP);
			return false;
		}
		
		// Check if the summon has enough HP
		if (getCurrentHp() <= skill.getHpConsume())
		{
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_HP);
			return false;
		}
		
		// Check if all casting conditions are completed
		if (!skill.checkCondition(this, target))
		{
			// Send a Server->Client packet ActionFailed to the Player
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check if this is bad magic skill and if Player is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
		if (skill.isBad() && _owner.isInOlympiadMode() && !_owner.isOlympiadStart())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Notify the AI with AI_INTENTION_CAST and target
		getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		return true;
	}
	
	@Override
	public void setImmobilized(boolean value)
	{
		super.setImmobilized(value);
		
		if (value)
		{
			_previousFollowStatus = _follow;
			// if immobilized temporarily disable follow mode
			if (_previousFollowStatus)
			{
				setFollowStatus(false);
			}
		}
		else
		{
			// if not more immobilized restore previous follow mode
			setFollowStatus(_previousFollowStatus);
		}
	}
	
	public void setOwner(Player newOwner)
	{
		_owner = newOwner;
	}
	
	@Override
	public void sendDamageMessage(Creature target, Skill skill, int damage, boolean crit, boolean miss)
	{
		if (miss || (_owner == null))
		{
			return;
		}
		
		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != _owner.getObjectId())
		{
			if (crit)
			{
				if (isServitor())
				{
					sendPacket(SystemMessageId.SUMMONED_MONSTER_S_CRITICAL_HIT);
				}
				else
				{
					sendPacket(SystemMessageId.PET_S_CRITICAL_HIT);
				}
			}
			
			if (_owner.isInOlympiadMode() && target.isPlayer() && ((Player) target).isInOlympiadMode() && (((Player) target).getOlympiadGameId() == _owner.getOlympiadGameId()))
			{
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
			}
			
			final SystemMessage sm;
			if ((target.isHpBlocked() && !target.isNpc()) || (target.isPlayer() && target.isAffected(EffectFlag.DUELIST_FURY) && !_owner.isAffected(EffectFlag.FACEOFF)))
			{
				sm = new SystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
				sm.addNpcName(this);
				sm.addString(target.getName());
				sm.addInt(damage);
				sm.addPopup(target.getObjectId(), getObjectId(), (damage * -1));
			}
			
			sendPacket(sm);
		}
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill)
	{
		super.reduceCurrentHp(damage, attacker, skill);
		
		if (!isDead() && !isHpBlocked() && (_owner != null) && (attacker != null) && (!_owner.isAffected(EffectFlag.DUELIST_FURY) || attacker.isAffected(EffectFlag.FACEOFF)))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
			sm.addNpcName(this);
			sm.addString(attacker.getName());
			sm.addInt((int) damage);
			sm.addPopup(getObjectId(), attacker.getObjectId(), (int) -damage);
			sendPacket(sm);
		}
	}
	
	@Override
	public void doCast(Skill skill)
	{
		if ((skill.getTarget(this, false, false, false) == null) && !_owner.getAccessLevel().allowPeaceAttack())
		{
			// Send a System Message to the Player
			_owner.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			
			// Send a Server->Client packet ActionFailed to the Player
			_owner.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		super.doCast(skill);
	}
	
	@Override
	public boolean isInCombat()
	{
		return (_owner != null) && _owner.isInCombat();
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	public void updateAndBroadcastStatus(int value)
	{
		if (_owner == null)
		{
			return;
		}
		
		if (isSpawned())
		{
			sendPacket(new PetSummonInfo(this, value));
			sendPacket(new PetStatusUpdate(this));
			broadcastNpcInfo(value);
			
			final Party party = _owner.getParty();
			if (party != null)
			{
				party.broadcastToPartyMembers(_owner, new ExPartyPetWindowUpdate(this));
			}
		}
	}
	
	public void broadcastNpcInfo(int value)
	{
		World.getInstance().forEachVisibleObject(this, Player.class, player ->
		{
			if ((player == _owner))
			{
				return;
			}
			
			if (isPet())
			{
				player.sendPacket(new ExPetInfo(this, player, value));
			}
			else
			{
				player.sendPacket(new SummonInfo(this, player, value));
			}
		});
	}
	
	public boolean isHungry()
	{
		return false;
	}
	
	public int getWeapon()
	{
		return 0;
	}
	
	public int getArmor()
	{
		return 0;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		// Check if the Player is the owner of the Pet
		if (player == _owner)
		{
			player.sendPacket(new PetSummonInfo(this, isDead() ? 0 : 1));
			if (isPet())
			{
				player.sendPacket(new PetItemList(getInventory().getItems()));
			}
		}
		else
		{
			if (isPet())
			{
				player.sendPacket(new ExPetInfo(this, player, 0));
			}
			else
			{
				player.sendPacket(new SummonInfo(this, player, 0));
			}
		}
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		sendPacket(new TeleportToLocation(this, getX(), getY(), getZ(), getHeading()));
	}
	
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == Race.UNDEAD;
	}
	
	/**
	 * Change the summon's state.
	 */
	public void switchMode()
	{
		// Do nothing.
	}
	
	/**
	 * Cancel the summon's action.
	 */
	public void cancelAction()
	{
		if (!isMovementDisabled())
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}
	
	/**
	 * Performs an attack to the owner's target.
	 * @param target the target to attack.
	 */
	public void doAttack(WorldObject target)
	{
		if ((_owner != null) && (target != null))
		{
			setTarget(target);
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			if (target.isFakePlayer() && !Config.FAKE_PLAYER_AUTO_ATTACKABLE)
			{
				_owner.updatePvPStatus();
			}
		}
	}
	
	/**
	 * Verify if the summon can perform an attack.
	 * @param target the target to check if can be attacked.
	 * @param ctrlPressed {@code true} if Ctrl key is pressed
	 * @return {@code true} if the summon can attack, {@code false} otherwise
	 */
	public boolean canAttack(WorldObject target, boolean ctrlPressed)
	{
		if (_owner == null)
		{
			return false;
		}
		
		if ((target == null) || (this == target) || (_owner == target))
		{
			return false;
		}
		
		// Sin eater, Big Boom, Wyvern can't attack with attack button.
		final int npcId = getId();
		if (CommonUtil.contains(PASSIVE_SUMMONS, npcId))
		{
			_owner.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (isBetrayed())
		{
			sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (isPet() && ((getLevel() - _owner.getLevel()) > 20))
		{
			sendPacket(SystemMessageId.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (_owner.isInOlympiadMode() && !_owner.isOlympiadStart())
		{
			// If owner is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
			_owner.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (_owner.isSiegeFriend(target))
		{
			sendPacket(SystemMessageId.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (!_owner.getAccessLevel().allowPeaceAttack() && _owner.isInsidePeaceZone(this, target))
		{
			sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
			return false;
		}
		
		if (isLockedTarget())
		{
			sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
			return false;
		}
		
		// Summons can attack NPCs even when the owner cannot.
		if (!target.isAutoAttackable(_owner) && !ctrlPressed && !target.isNpc())
		{
			setFollowStatus(false);
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
			sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		// Siege golems AI doesn't support attacking other than doors/walls at the moment.
		if (target.isDoor() && (getTemplate().getRace() != Race.SIEGE_WEAPON))
		{
			return false;
		}
		
		return true;
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
	public void sendPacket(SystemMessageId id)
	{
		if (_owner != null)
		{
			_owner.sendPacket(id);
		}
	}
	
	@Override
	public boolean isSummon()
	{
		return true;
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic, boolean fish)
	{
		Item item;
		IItemHandler handler;
		if ((_owner.getAutoSoulShot() == null) || _owner.getAutoSoulShot().isEmpty())
		{
			return;
		}
		
		for (int itemId : _owner.getAutoSoulShot())
		{
			item = _owner.getInventory().getItemByItemId(itemId);
			if (item != null)
			{
				if (magic && (item.getTemplate().getDefaultAction() == ActionType.SUMMON_SPIRITSHOT))
				{
					handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
					{
						handler.useItem(_owner, item, false);
					}
				}
				
				if (physical && (item.getTemplate().getDefaultAction() == ActionType.SUMMON_SOULSHOT))
				{
					handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
					{
						handler.useItem(_owner, item, false);
					}
				}
			}
			else
			{
				_owner.removeAutoSoulShot(itemId);
			}
		}
	}
	
	@Override
	public int getClanId()
	{
		return (_owner != null) ? _owner.getClanId() : 0;
	}
	
	@Override
	public int getAllyId()
	{
		return (_owner != null) ? _owner.getAllyId() : 0;
	}
	
	public void setSummonPoints(int summonPoints)
	{
		_summonPoints = summonPoints;
	}
	
	public int getSummonPoints()
	{
		return _summonPoints;
	}
	
	public void sendInventoryUpdate(InventoryUpdate iu)
	{
		final Player owner = _owner;
		if (owner != null)
		{
			owner.sendInventoryUpdate(iu);
		}
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || !getTemplate().canMove();
	}
	
	@Override
	public boolean isTargetable()
	{
		return super.isTargetable() && getTemplate().isTargetable();
	}
	
	@Override
	public boolean isOnEvent()
	{
		return (_owner != null) && _owner.isOnEvent();
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("(");
		sb.append(getId());
		sb.append(") Owner: ");
		sb.append(_owner);
		return sb.toString();
	}
}
