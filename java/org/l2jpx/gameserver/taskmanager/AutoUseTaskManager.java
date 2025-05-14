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
package org.l2jpx.gameserver.taskmanager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.xml.ActionData;
import org.l2jpx.gameserver.data.xml.PetSkillData;
import org.l2jpx.gameserver.handler.IItemHandler;
import org.l2jpx.gameserver.handler.IPlayerActionHandler;
import org.l2jpx.gameserver.handler.ItemHandler;
import org.l2jpx.gameserver.handler.PlayerActionHandler;
import org.l2jpx.gameserver.model.ActionDataHolder;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Creature;
import org.l2jpx.gameserver.model.actor.Playable;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.Summon;
import org.l2jpx.gameserver.model.actor.instance.Guard;
import org.l2jpx.gameserver.model.actor.instance.Monster;
import org.l2jpx.gameserver.model.actor.transform.TransformTemplate;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.item.OnItemUse;
import org.l2jpx.gameserver.model.holders.AttachSkillHolder;
import org.l2jpx.gameserver.model.holders.ItemSkillHolder;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.skill.AbnormalType;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.skill.targets.AffectScope;
import org.l2jpx.gameserver.model.skill.targets.TargetType;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.SystemMessageId;

/**
 * @author Mobius
 */
public class AutoUseTaskManager
{
	private static final Set<Set<Player>> POOLS = ConcurrentHashMap.newKeySet();
	private static final int POOL_SIZE = 300;
	private static final int TASK_DELAY = 300;
	private static final int REUSE_MARGIN_TIME = 3;
	
	protected AutoUseTaskManager()
	{
	}
	
	private class AutoUse implements Runnable
	{
		private final Set<Player> _players;
		
		public AutoUse(Set<Player> players)
		{
			_players = players;
		}
		
		@Override
		public void run()
		{
			if (_players.isEmpty())
			{
				return;
			}
			
			for (Player player : _players)
			{
				if (!player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()))
				{
					stopAutoUseTask(player);
					continue;
				}
				
				if (player.isSitting() || player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead() || player.isMounted() || (player.isTransformed() && player.getTransformation().get().isRiding()))
				{
					continue;
				}
				
				final boolean isInPeaceZone = player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.SAYUNE);
				
				if (Config.ENABLE_AUTO_ITEM && !isInPeaceZone)
				{
					ITEMS: for (Integer itemId : player.getAutoUseSettings().getAutoSupplyItems())
					{
						if (player.isTeleporting())
						{
							break ITEMS;
						}
						
						final Item item = player.getInventory().getItemByItemId(itemId.intValue());
						if (item == null)
						{
							player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
							continue ITEMS;
						}
						
						final ItemTemplate it = item.getTemplate();
						if (it != null)
						{
							if (!it.checkCondition(player, player, false))
							{
								continue ITEMS;
							}
							
							final List<ItemSkillHolder> skills = it.getAllSkills();
							if (skills != null)
							{
								for (ItemSkillHolder itemSkillHolder : skills)
								{
									final Skill skill = itemSkillHolder.getSkill();
									if (player.isAffectedBySkill(skill.getId()) || player.hasSkillReuse(skill.getReuseHashCode()) || !skill.checkCondition(player, player))
									{
										continue ITEMS;
									}
								}
							}
						}
						
						final int reuseDelay = item.getReuseDelay();
						if ((reuseDelay <= 0) || (player.getItemRemainingReuseTime(item.getObjectId()) <= 0))
						{
							final EtcItem etcItem = item.getEtcItem();
							final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
							if ((handler != null) && handler.useItem(player, item, false))
							{
								if (reuseDelay > 0)
								{
									player.addTimeStampItem(item, reuseDelay);
								}
								
								// Notify events.
								if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_USE, item.getTemplate()))
								{
									EventDispatcher.getInstance().notifyEventAsync(new OnItemUse(player, item), item.getTemplate());
								}
							}
						}
					}
				}
				
				if (Config.ENABLE_AUTO_POTION && !isInPeaceZone && (player.getCurrentHpPercent() < player.getAutoPlaySettings().getAutoPotionPercent()))
				{
					final int itemId = player.getAutoUseSettings().getAutoPotionItem();
					if (itemId > 0)
					{
						final Item item = player.getInventory().getItemByItemId(itemId);
						if (item == null)
						{
							player.getAutoUseSettings().setAutoPotionItem(0);
						}
						else
						{
							final int reuseDelay = item.getReuseDelay();
							if ((reuseDelay <= 0) || (player.getItemRemainingReuseTime(item.getObjectId()) <= 0))
							{
								final EtcItem etcItem = item.getEtcItem();
								final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
								if ((handler != null) && handler.useItem(player, item, false))
								{
									if (reuseDelay > 0)
									{
										player.addTimeStampItem(item, reuseDelay);
									}
									
									// Notify events.
									if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_USE, item.getTemplate()))
									{
										EventDispatcher.getInstance().notifyEventAsync(new OnItemUse(player, item), item.getTemplate());
									}
								}
							}
						}
					}
				}
				
				if (Config.ENABLE_AUTO_SKILL)
				{
					BUFFS: for (Integer skillId : player.getAutoUseSettings().getAutoBuffs())
					{
						// Fixes start area issue.
						if (isInPeaceZone)
						{
							break BUFFS;
						}
						
						// Already casting.
						if (player.isCastingNow())
						{
							break BUFFS;
						}
						
						// Attacking.
						if (player.isAttackingNow())
						{
							break BUFFS;
						}
						
						// Player is teleporting.
						if (player.isTeleporting())
						{
							break BUFFS;
						}
						
						Playable pet = null;
						Skill skill = player.getKnownSkill(skillId.intValue());
						if (skill == null)
						{
							if (player.hasServitors())
							{
								SUMMON_SEARCH: for (Summon summon : player.getServitors().values())
								{
									skill = summon.getKnownSkill(skillId.intValue());
									if (skill != null)
									{
										pet = summon;
										break SUMMON_SEARCH;
									}
								}
							}
							if ((skill == null) && player.hasPet())
							{
								pet = player.getPet();
								skill = pet.getKnownSkill(skillId.intValue());
							}
							if (skill == null)
							{
								player.getAutoUseSettings().getAutoBuffs().remove(skillId);
								continue BUFFS;
							}
						}
						
						// Buff use check.
						final WorldObject target = player.getTarget();
						if (!canCastBuff(player, target, skill))
						{
							continue BUFFS;
						}
						
						ATTACH_SEARCH: for (AttachSkillHolder holder : skill.getAttachSkills())
						{
							if (player.isAffectedBySkill(holder.getRequiredSkillId()))
							{
								skill = holder.getSkill();
								break ATTACH_SEARCH;
							}
						}
						
						// Playable target cast.
						final Playable caster = pet != null ? pet : player;
						if ((target != null) && target.isPlayable() && (target.getActingPlayer().getPvpFlag() == 0) && (target.getActingPlayer().getReputation() >= 0))
						{
							caster.doCast(skill);
						}
						else // Target self, cast and re-target.
						{
							final WorldObject savedTarget = target;
							caster.setTarget(caster);
							caster.doCast(skill);
							caster.setTarget(savedTarget);
						}
					}
					
					// Continue when auto play is not enabled.
					if (!player.isAutoPlaying())
					{
						continue;
					}
					
					final int count = player.getAutoUseSettings().getAutoSkills().size();
					SKILLS: for (int i = 0; i < count; i++)
					{
						// Already casting.
						if (player.isCastingNow())
						{
							break SKILLS;
						}
						
						// Player is teleporting.
						if (player.isTeleporting())
						{
							break SKILLS;
						}
						
						// Acquire next skill.
						Playable pet = null;
						final WorldObject target = player.getTarget();
						final Integer skillId = player.getAutoUseSettings().getNextSkillId();
						Skill skill = player.getKnownSkill(skillId);
						if (skill == null)
						{
							if (player.hasServitors())
							{
								SUMMON_SEARCH: for (Summon summon : player.getServitors().values())
								{
									skill = summon.getKnownSkill(skillId.intValue());
									if (skill == null)
									{
										skill = PetSkillData.getInstance().getKnownSkill(summon, skillId);
									}
									if (skill != null)
									{
										pet = summon;
										pet.setTarget(target);
										break SUMMON_SEARCH;
									}
								}
							}
							if ((skill == null) && player.hasPet())
							{
								pet = player.getPet();
								skill = pet.getKnownSkill(skillId);
								if (skill == null)
								{
									skill = PetSkillData.getInstance().getKnownSkill((Summon) pet, skillId);
								}
								if (pet.isSkillDisabled(skill))
								{
									player.getAutoUseSettings().incrementSkillOrder();
									break SKILLS;
								}
							}
							if (skill == null)
							{
								player.getAutoUseSettings().getAutoSkills().remove(skillId);
								player.getAutoUseSettings().resetSkillOrder();
								break SKILLS;
							}
						}
						
						// Casting on self stops movement.
						if (target == player)
						{
							break SKILLS;
						}
						
						// Check bad skill target.
						if ((target == null) || ((Creature) target).isDead())
						{
							break SKILLS;
						}
						
						// Peace zone and auto attackable checks.
						if (target.isInsideZone(ZoneId.PEACE) || !target.isAutoAttackable(player))
						{
							break SKILLS;
						}
						
						// Do not attack guards.
						if (target instanceof Guard)
						{
							final int targetMode = player.getAutoPlaySettings().getNextTargetMode();
							if ((targetMode != 3 /* NPC */) && (targetMode != 0 /* Any Target */))
							{
								break SKILLS;
							}
						}
						
						// Increment skill order.
						player.getAutoUseSettings().incrementSkillOrder();
						
						// Skill use check.
						final Playable caster = pet != null ? pet : player;
						if (!canUseMagic(caster, target, skill))
						{
							continue SKILLS;
						}
						
						// Use the skill.
						caster.useMagic(skill, null, true, false);
						
						break SKILLS;
					}
					
					ACTIONS: for (Integer actionId : player.getAutoUseSettings().getAutoActions())
					{
						final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
						if (info != null)
						{
							for (AbstractEffect effect : info.getEffects())
							{
								if (!effect.checkCondition(actionId))
								{
									player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
									break ACTIONS;
								}
							}
						}
						
						// Do not allow to do some action if player is transformed.
						if (player.isTransformed())
						{
							final TransformTemplate transformTemplate = player.getTransformation().get().getTemplate(player);
							final int[] allowedActions = transformTemplate.getBasicActionList();
							if ((allowedActions == null) || (Arrays.binarySearch(allowedActions, actionId) < 0))
							{
								continue ACTIONS;
							}
						}
						
						final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(actionId);
						if (actionHolder != null)
						{
							final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
							if (actionHandler != null)
							{
								actionHandler.useAction(player, actionHolder, false, false);
							}
						}
					}
				}
			}
		}
		
		private boolean canCastBuff(Player player, WorldObject target, Skill skill)
		{
			// Summon check.
			if ((skill.getAffectScope() == AffectScope.SUMMON_EXCEPT_MASTER) || (skill.getTargetType() == TargetType.SUMMON))
			{
				if (!player.hasServitors())
				{
					return false;
				}
				int occurrences = 0;
				for (Summon servitor : player.getServitors().values())
				{
					if (servitor.isAffectedBySkill(skill.getId()))
					{
						occurrences++;
					}
				}
				if (occurrences == player.getServitors().size())
				{
					return false;
				}
			}
			
			if ((target != null) && target.isCreature() && ((Creature) target).isAlikeDead() && (skill.getTargetType() != TargetType.SELF) && (skill.getTargetType() != TargetType.NPC_BODY) && (skill.getTargetType() != TargetType.PC_BODY))
			{
				return false;
			}
			
			final Playable playableTarget = (target == null) || !target.isPlayable() || (skill.getTargetType() == TargetType.SELF) ? player : (Playable) target;
			if ((player != playableTarget) && (player.calculateDistance3D(playableTarget) > skill.getCastRange()))
			{
				return false;
			}
			
			if (!canUseMagic(player, playableTarget, skill))
			{
				return false;
			}
			
			final BuffInfo buffInfo = playableTarget.getEffectList().getBuffInfoBySkillId(skill.getId());
			final BuffInfo abnormalBuffInfo = playableTarget.getEffectList().getFirstBuffInfoByAbnormalType(skill.getAbnormalType());
			if (abnormalBuffInfo != null)
			{
				if (buffInfo != null)
				{
					return (abnormalBuffInfo.getSkill().getId() == buffInfo.getSkill().getId()) && ((buffInfo.getTime() <= REUSE_MARGIN_TIME) || (buffInfo.getSkill().getLevel() < skill.getLevel()));
				}
				return (abnormalBuffInfo.getSkill().getAbnormalLevel() < skill.getAbnormalLevel()) || abnormalBuffInfo.isAbnormalType(AbnormalType.NONE);
			}
			return buffInfo == null;
		}
		
		private boolean canUseMagic(Playable playable, WorldObject target, Skill skill)
		{
			if ((skill.getItemConsumeCount() > 0) && (playable.getInventory().getInventoryItemCount(skill.getItemConsumeId(), -1) < skill.getItemConsumeCount()))
			{
				return false;
			}
			
			if ((skill.getMpConsume() > 0) && (playable.getCurrentMp() < skill.getMpConsume()))
			{
				return false;
			}
			
			// Check if monster is spoiled to avoid Spoil (254) skill recast.
			if ((skill.getId() == 254) && (target != null) && target.isMonster() && ((Monster) target).isSpoiled())
			{
				return false;
			}
			
			for (AttachSkillHolder holder : skill.getAttachSkills())
			{
				if (playable.isAffectedBySkill(holder.getRequiredSkillId()) //
					&& (playable.hasSkillReuse(holder.getSkill().getReuseHashCode()) || playable.isAffectedBySkill(holder)))
				{
					return false;
				}
			}
			
			return !playable.isSkillDisabled(skill) && skill.checkCondition(playable, target);
		}
	}
	
	public synchronized void startAutoUseTask(Player player)
	{
		for (Set<Player> pool : POOLS)
		{
			if (pool.contains(player))
			{
				return;
			}
		}
		
		for (Set<Player> pool : POOLS)
		{
			if (pool.size() < POOL_SIZE)
			{
				pool.add(player);
				return;
			}
		}
		
		final Set<Player> pool = ConcurrentHashMap.newKeySet(POOL_SIZE);
		pool.add(player);
		ThreadPool.schedulePriorityTaskAtFixedRate(new AutoUse(pool), TASK_DELAY, TASK_DELAY);
		POOLS.add(pool);
	}
	
	public void stopAutoUseTask(Player player)
	{
		player.getAutoUseSettings().resetSkillOrder();
		for (Set<Player> pool : POOLS)
		{
			if (pool.remove(player))
			{
				return;
			}
		}
	}
	
	public static AutoUseTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoUseTaskManager INSTANCE = new AutoUseTaskManager();
	}
}
