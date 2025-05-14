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
package org.l2jpx.gameserver.model.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.enums.SkillFinishType;
import org.l2jpx.gameserver.model.EffectList;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.model.effects.EffectTaskInfo;
import org.l2jpx.gameserver.model.effects.EffectTickTask;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.options.Options;
import org.l2jpx.gameserver.model.stats.Formulas;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.taskmanager.GameTimeTaskManager;

/**
 * Buff Info.<br>
 * Complex DTO that holds all the information for a given buff (or debuff or dance/song) set of effects issued by an skill.
 * @author Zoey76
 */
public class BuffInfo
{
	// Data
	/** Data. */
	private final int _effectorObjectId;
	private final Creature _effector;
	private final Creature _effected;
	private final Skill _skill;
	/** The effects. */
	private final List<AbstractEffect> _effects = new ArrayList<>(1);
	// Tasks
	/** Effect tasks for ticks. */
	private Map<AbstractEffect, EffectTaskInfo> _tasks;
	// Time and ticks
	/** Abnormal time. */
	private int _abnormalTime;
	/** The game ticks at the start of this effect. */
	private int _periodStartTicks;
	// Misc
	/** If {@code true} then this effect has been cancelled. */
	private volatile SkillFinishType _finishType = SkillFinishType.NORMAL;
	/** If {@code true} then this effect is in use (or has been stop because an Herb took place). */
	private volatile boolean _isInUse = true;
	private final boolean _hideStartMessage;
	private final Item _item;
	private final Options _option;
	
	/**
	 * Buff Info constructor.
	 * @param effector the effector
	 * @param effected the effected
	 * @param skill the skill
	 * @param hideStartMessage hides start message
	 * @param item
	 * @param option
	 */
	public BuffInfo(Creature effector, Creature effected, Skill skill, boolean hideStartMessage, Item item, Options option)
	{
		_effectorObjectId = (effector != null) ? effector.getObjectId() : 0;
		_effector = effector;
		_effected = effected;
		_skill = skill;
		_abnormalTime = Formulas.calcEffectAbnormalTime(effector, effected, skill);
		_periodStartTicks = GameTimeTaskManager.getInstance().getGameTicks();
		_hideStartMessage = hideStartMessage;
		_item = item;
		_option = option;
	}
	
	/**
	 * Gets the effects on this buff info.
	 * @return the effects
	 */
	public List<AbstractEffect> getEffects()
	{
		return _effects;
	}
	
	/**
	 * Adds an effect to this buff info.
	 * @param effect the effect to add
	 */
	public void addEffect(AbstractEffect effect)
	{
		_effects.add(effect);
	}
	
	/**
	 * Adds an effect task to this buff info.<br>
	 * Uses double-checked locking to initialize the map if it's necessary.
	 * @param effect the effect that owns the task
	 * @param effectTaskInfo the task info
	 */
	private void addTask(AbstractEffect effect, EffectTaskInfo effectTaskInfo)
	{
		if (_tasks == null)
		{
			synchronized (this)
			{
				if (_tasks == null)
				{
					_tasks = new ConcurrentHashMap<>();
				}
			}
		}
		_tasks.put(effect, effectTaskInfo);
	}
	
	/**
	 * Gets the task for the given effect.
	 * @param effect the effect
	 * @return the task
	 */
	private EffectTaskInfo getEffectTask(AbstractEffect effect)
	{
		return (_tasks == null) ? null : _tasks.get(effect);
	}
	
	/**
	 * Gets the skill that created this buff info.
	 * @return the skill
	 */
	public Skill getSkill()
	{
		return _skill;
	}
	
	/**
	 * Gets the calculated abnormal time.
	 * @return the abnormal time
	 */
	public int getAbnormalTime()
	{
		return _abnormalTime;
	}
	
	/**
	 * Sets the abnormal time.
	 * @param abnormalTime the abnormal time to set
	 */
	public void setAbnormalTime(int abnormalTime)
	{
		_abnormalTime = abnormalTime;
	}
	
	/**
	 * Gets the period start ticks.
	 * @return the period start
	 */
	public int getPeriodStartTicks()
	{
		return _periodStartTicks;
	}
	
	/**
	 * @return the item that triggered this skill
	 */
	public Item getItem()
	{
		return _item;
	}
	
	/**
	 * @return the options that issued this effect
	 */
	public Options getOption()
	{
		return _option;
	}
	
	/**
	 * Get the remaining time in seconds for this buff info.
	 * @return the elapsed time
	 */
	public int getTime()
	{
		return _abnormalTime - ((GameTimeTaskManager.getInstance().getGameTicks() - _periodStartTicks) / GameTimeTaskManager.TICKS_PER_SECOND);
	}
	
	/**
	 * Verify if this buff info has been cancelled.
	 * @return {@code true} if this buff info has been cancelled, {@code false} otherwise
	 */
	public boolean isRemoved()
	{
		return _finishType == SkillFinishType.REMOVED;
	}
	
	/**
	 * Set the buff info to removed.
	 * @param type the SkillFinishType to set
	 */
	public void setFinishType(SkillFinishType type)
	{
		_finishType = type;
	}
	
	/**
	 * Verify if this buff info is in use.
	 * @return {@code true} if this buff info is in use, {@code false} otherwise
	 */
	public boolean isInUse()
	{
		return _isInUse;
	}
	
	/**
	 * Set the buff info to in use.
	 * @param value the value to set
	 */
	public void setInUse(boolean value)
	{
		_isInUse = value;
		
		// Send message that the effect is applied or removed.
		if ((_skill != null) && !_skill.isHidingMessages() && _effected.isPlayer())
		{
			if (value)
			{
				if (!_hideStartMessage && !_skill.isAura() && isDisplayedForEffected())
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_S_EFFECT_CAN_BE_FELT);
					sm.addSkillName(_skill);
					_effected.sendPacket(sm);
				}
			}
			else
			{
				final SystemMessage sm = new SystemMessage(_skill.isToggle() ? SystemMessageId.S1_HAS_BEEN_ABORTED : SystemMessageId.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED);
				sm.addSkillName(_skill);
				_effected.sendPacket(sm);
			}
		}
	}
	
	/**
	 * Gets the character's object id that launched the buff.
	 * @return the object id of the effector.
	 */
	public int getEffectorObjectId()
	{
		return _effectorObjectId;
	}
	
	/**
	 * Gets the character that launched the buff.
	 * @return the effector
	 */
	public Creature getEffector()
	{
		return _effector;
	}
	
	/**
	 * Gets the target of the skill.
	 * @return the effected
	 */
	public Creature getEffected()
	{
		return _effected;
	}
	
	/**
	 * Stops all the effects for this buff info.<br>
	 * Removes effects stats.<br>
	 * <b>It will not remove the buff info from the effect list</b>.<br>
	 * Instead call {@link EffectList#stopSkillEffects(SkillFinishType, Skill)}
	 * @param type determines the system message that will be sent.
	 */
	public void stopAllEffects(SkillFinishType type)
	{
		setFinishType(type);
		
		// Remove this buff info from BuffFinishTask.
		_effected.removeBuffInfoTime(this);
		finishEffects();
	}
	
	public void initializeEffects()
	{
		if ((_effected == null) || (_skill == null))
		{
			return;
		}
		
		// When effects are initialized, the successfully landed.
		if (!_hideStartMessage && _effected.isPlayer() && (_effected.getActingPlayer().hasEnteredWorld() || Config.SHOW_EFFECT_MESSAGES_ON_LOGIN) && !_skill.isHidingMessages() && !_skill.isAura() && isDisplayedForEffected())
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_S_EFFECT_CAN_BE_FELT);
			sm.addSkillName(_skill);
			_effected.sendPacket(sm);
		}
		
		// Creates a task that will stop all the effects.
		if (_abnormalTime > 0)
		{
			_effected.addBuffInfoTime(this);
		}
		
		for (AbstractEffect effect : _effects)
		{
			if (effect.isInstant() || (_effected.isDead() && !_skill.isPassive() && !_skill.isStayAfterDeath()))
			{
				continue;
			}
			
			// Call on start.
			effect.onStart(_effector, _effected, _skill, _item);
			
			// Do not add continuous effect if target just died from the initial effect, otherwise they'll be ticked forever.
			if (_effected.isDead())
			{
				continue;
			}
			
			// If it's a continuous effect, if has ticks schedule a task with period, otherwise schedule a simple task to end it.
			if (effect.getTicks() > 0)
			{
				// The task for the effect ticks.
				final EffectTickTask effectTask = new EffectTickTask(this, effect);
				final ScheduledFuture<?> scheduledFuture = ThreadPool.scheduleAtFixedRate(effectTask, effect.getTicks() * Config.EFFECT_TICK_RATIO, effect.getTicks() * Config.EFFECT_TICK_RATIO);
				// Adds the task for ticking.
				addTask(effect, new EffectTaskInfo(effectTask, scheduledFuture));
			}
		}
	}
	
	/**
	 * Called on each tick.<br>
	 * Verify if the effect should end and the effect task should be cancelled.
	 * @param effect the effect that is ticking
	 */
	public void onTick(AbstractEffect effect)
	{
		boolean continueForever = false;
		// If the effect is in use, allow it to affect the effected.
		if (_isInUse)
		{
			// Callback for on action time event.
			continueForever = effect.onActionTime(_effector, _effected, _skill, _item);
		}
		
		if (!continueForever && _skill.isToggle())
		{
			final EffectTaskInfo task = getEffectTask(effect);
			if (task != null)
			{
				final ScheduledFuture<?> schedule = task.getScheduledFuture();
				if ((schedule != null) && !schedule.isCancelled() && !schedule.isDone())
				{
					schedule.cancel(true); // Don't allow to finish current run.
				}
				_effected.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, _skill); // Remove the buff from the effect list.
			}
		}
	}
	
	public void finishEffects()
	{
		// Cancels the ticking task.
		if (_tasks != null)
		{
			for (EffectTaskInfo effectTask : _tasks.values())
			{
				final ScheduledFuture<?> schedule = effectTask.getScheduledFuture();
				if ((schedule != null) && !schedule.isCancelled() && !schedule.isDone())
				{
					schedule.cancel(true); // Don't allow to finish current run.
				}
			}
		}
		
		// Notify on exit.
		for (AbstractEffect effect : _effects)
		{
			// Instant effects shouldn't call onExit(..).
			// if (!effect.isInstant())
			// {
			effect.onExit(_effector, _effected, _skill);
			// }
		}
		
		// Set the proper system message.
		if ((_skill != null) && !(_effected.isSummon() && !((Summon) _effected).getOwner().hasSummon()) && !_skill.isHidingMessages())
		{
			SystemMessageId smId = null;
			if ((_finishType == SkillFinishType.SILENT) || !isDisplayedForEffected())
			{
				// smId is null.
			}
			else if (_skill.isToggle())
			{
				smId = SystemMessageId.S1_HAS_BEEN_ABORTED;
			}
			else if (_finishType == SkillFinishType.REMOVED)
			{
				smId = SystemMessageId.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED;
			}
			else if (!_skill.isPassive())
			{
				smId = SystemMessageId.S1_HAS_WORN_OFF;
			}
			
			if ((smId != null) && (_effected.getActingPlayer() != null) && _effected.getActingPlayer().isOnline())
			{
				final SystemMessage sm = new SystemMessage(smId);
				sm.addSkillName(_skill);
				_effected.sendPacket(sm);
			}
		}
	}
	
	public void resetAbnormalTime(int abnormalTime)
	{
		if (_abnormalTime > 0)
		{
			_periodStartTicks = GameTimeTaskManager.getInstance().getGameTicks();
			_abnormalTime = abnormalTime;
			_effected.removeBuffInfoTime(this);
			_effected.addBuffInfoTime(this);
		}
	}
	
	public boolean isAbnormalType(AbnormalType type)
	{
		return _skill.getAbnormalType() == type;
	}
	
	/**
	 * Determines if this BuffInfo is displayed for the effected. These checks are needed to display A3 skills properly.<br>
	 * A3 skills are a type of skills that has different behavior depending on the effector and the effected.
	 * @return true if this BuffInfo is displayed for the effected, false otherwise.
	 */
	public boolean isDisplayedForEffected()
	{
		return !_skill.isSelfContinuous() || (_effected == _effector) || !_skill.hasEffects(EffectScope.SELF);
	}
}
