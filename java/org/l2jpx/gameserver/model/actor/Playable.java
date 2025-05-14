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

import org.l2jpx.gameserver.ai.CtrlEvent;
import org.l2jpx.gameserver.enums.ClanWarState;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.instancemanager.ZoneManager;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.stat.PlayableStat;
import org.l2jpx.gameserver.model.actor.status.PlayableStatus;
import org.l2jpx.gameserver.model.actor.templates.CreatureTemplate;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.clan.ClanWar;
import org.l2jpx.gameserver.model.effects.EffectFlag;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jpx.gameserver.model.events.returns.TerminateReturn;
import org.l2jpx.gameserver.model.instancezone.Instance;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.quest.QuestState;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * This class represents all Playable characters in the world.<br>
 * Playable:
 * <ul>
 * <li>Player</li>
 * <li>Summon</li>
 * </ul>
 */
public abstract class Playable extends Creature
{
	private Creature _lockedTarget = null;
	private Player transferDmgTo = null;
	
	/**
	 * Constructor of Playable.<br>
	 * <br>
	 * <b><u>Actions</u>:</b>
	 * <ul>
	 * <li>Call the Creature constructor to create an empty _skills slot and link copy basic Calculator set to this Playable</li>
	 * </ul>
	 * @param objectId the object id
	 * @param template The CreatureTemplate to apply to the Playable
	 */
	public Playable(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.Playable);
		setInvul(false);
	}
	
	public Playable(CreatureTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Playable);
		setInvul(false);
	}
	
	@Override
	public PlayableStat getStat()
	{
		return (PlayableStat) super.getStat();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new PlayableStat(this));
	}
	
	@Override
	public PlayableStatus getStatus()
	{
		return (PlayableStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new PlayableStatus(this));
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (EventDispatcher.getInstance().hasListener(EventType.ON_CREATURE_DEATH, this))
		{
			final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
			if ((returnBack != null) && returnBack.terminate())
			{
				return false;
			}
		}
		
		// killing is only possible one time
		synchronized (this)
		{
			if (isDead())
			{
				return false;
			}
			// now reset currentHp to zero
			setCurrentHp(0);
			setDead(true);
		}
		
		// Set target to null and cancel Attack or Cast
		setTarget(null);
		
		// Abort casting after target has been cancelled.
		abortAttack();
		abortCast();
		
		// Stop movement
		stopMove(null);
		
		// Stop HP/MP/CP Regeneration task
		getStatus().stopHpMpRegeneration();
		
		boolean deleteBuffs = true;
		if (isNoblesseBlessedAffected())
		{
			stopEffects(EffectFlag.NOBLESS_BLESSING);
			deleteBuffs = false;
		}
		if (isResurrectSpecialAffected())
		{
			stopEffects(EffectFlag.RESURRECTION_SPECIAL);
			deleteBuffs = false;
		}
		if (isPlayer())
		{
			final Player player = getActingPlayer();
			if (player.hasCharmOfCourage())
			{
				if (player.isInSiege())
				{
					getActingPlayer().reviveRequest(getActingPlayer(), false, 0, 0, 0, 0);
				}
				player.setCharmOfCourage(false);
				player.sendPacket(new EtcStatusUpdate(player));
			}
		}
		
		if (deleteBuffs)
		{
			stopAllEffectsExceptThoseThatLastThroughDeath();
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
		broadcastStatusUpdate();
		
		ZoneManager.getInstance().getRegion(this).onDeath(this);
		
		// Notify Quest of Playable's death
		final Player actingPlayer = getActingPlayer();
		if (!actingPlayer.isNotifyQuestOfDeathEmpty())
		{
			for (QuestState qs : actingPlayer.getNotifyQuestOfDeath())
			{
				qs.getQuest().notifyDeath((killer == null ? this : killer), this, qs);
			}
		}
		
		// Notify instance
		if (isPlayer())
		{
			final Instance instance = getInstanceWorld();
			if (instance != null)
			{
				instance.onDeath(getActingPlayer());
			}
		}
		
		if (killer != null)
		{
			final Player killerPlayer = killer.getActingPlayer();
			if (killerPlayer != null)
			{
				killerPlayer.onPlayerKill(this);
			}
		}
		
		// Notify Creature AI
		getAI().notifyEvent(CtrlEvent.EVT_DEAD);
		return true;
	}
	
	public boolean checkIfPvP(Player target)
	{
		final Player player = getActingPlayer();
		if ((player == null) //
			|| (target == null) //
			|| (player == target) //
			|| (target.getReputation() < 0) //
			|| (target.getPvpFlag() > 0) //
			|| target.isOnDarkSide())
		{
			return true;
		}
		else if (player.isInParty() && player.getParty().containsPlayer(target))
		{
			return false;
		}
		
		final Clan playerClan = player.getClan();
		if ((playerClan != null) && !player.isAcademyMember() && !target.isAcademyMember())
		{
			final ClanWar war = playerClan.getWarWith(target.getClanId());
			return (war != null) && (war.getState() == ClanWarState.MUTUAL);
		}
		return false;
	}
	
	/**
	 * Return True.
	 */
	@Override
	public boolean canBeAttacked()
	{
		return true;
	}
	
	// Support for Noblesse Blessing skill, where buffs are retained after resurrect
	public boolean isNoblesseBlessedAffected()
	{
		return isAffected(EffectFlag.NOBLESS_BLESSING);
	}
	
	/**
	 * @return {@code true} if char can resurrect by himself, {@code false} otherwise
	 */
	public boolean isResurrectSpecialAffected()
	{
		return isAffected(EffectFlag.RESURRECTION_SPECIAL);
	}
	
	/**
	 * @return {@code true} if the Silent Moving mode is active, {@code false} otherwise
	 */
	public boolean isSilentMovingAffected()
	{
		return isAffected(EffectFlag.SILENT_MOVE);
	}
	
	/**
	 * For Newbie Protection Blessing skill, keeps you safe from an attack by a chaotic character >= 10 levels apart from you.
	 * @return
	 */
	public boolean isProtectionBlessingAffected()
	{
		return isAffected(EffectFlag.PROTECTION_BLESSING);
	}
	
	@Override
	public void updateEffectIcons(boolean partyOnly)
	{
		getEffectList().updateEffectIcons(partyOnly);
	}
	
	public boolean isLockedTarget()
	{
		return _lockedTarget != null;
	}
	
	public Creature getLockedTarget()
	{
		return _lockedTarget;
	}
	
	public void setLockedTarget(Creature creature)
	{
		_lockedTarget = creature;
	}
	
	public void setTransferDamageTo(Player val)
	{
		transferDmgTo = val;
	}
	
	public Player getTransferingDamageTo()
	{
		return transferDmgTo;
	}
	
	public abstract void doPickupItem(WorldObject object);
	
	public abstract boolean useMagic(Skill skill, Item item, boolean forceUse, boolean dontMove);
	
	public abstract void storeMe();
	
	public abstract void storeEffect(boolean storeEffects);
	
	public abstract void restoreEffects();
	
	public boolean isOnEvent()
	{
		return false;
	}
	
	@Override
	public boolean isPlayable()
	{
		return true;
	}
}
