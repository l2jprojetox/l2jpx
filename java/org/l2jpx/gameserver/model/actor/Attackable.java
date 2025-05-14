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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.ai.AttackableAI;
import org.l2jpx.gameserver.ai.CreatureAI;
import org.l2jpx.gameserver.ai.CtrlEvent;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.data.xml.ItemData;
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.enums.DropType;
import org.l2jpx.gameserver.enums.InstanceType;
import org.l2jpx.gameserver.enums.Team;
import org.l2jpx.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jpx.gameserver.instancemanager.PcCafePointsManager;
import org.l2jpx.gameserver.instancemanager.WalkingManager;
import org.l2jpx.gameserver.instancemanager.events.EventDropManager;
import org.l2jpx.gameserver.model.AbsorberInfo;
import org.l2jpx.gameserver.model.AggroInfo;
import org.l2jpx.gameserver.model.CommandChannel;
import org.l2jpx.gameserver.model.DamageDoneInfo;
import org.l2jpx.gameserver.model.Party;
import org.l2jpx.gameserver.model.Seed;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.instance.GrandBoss;
import org.l2jpx.gameserver.model.actor.instance.Monster;
import org.l2jpx.gameserver.model.actor.instance.Servitor;
import org.l2jpx.gameserver.model.actor.status.AttackableStatus;
import org.l2jpx.gameserver.model.actor.tasks.attackable.CommandChannelTimer;
import org.l2jpx.gameserver.model.actor.templates.NpcTemplate;
import org.l2jpx.gameserver.model.clan.Clan;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnAttackableAggroRangeEnter;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnAttackableAttack;
import org.l2jpx.gameserver.model.events.impl.creature.npc.OnAttackableKill;
import org.l2jpx.gameserver.model.holders.ItemHolder;
import org.l2jpx.gameserver.model.holders.SkillHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.olympiad.Hero;
import org.l2jpx.gameserver.model.skill.CommonSkill;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.skill.SkillCaster;
import org.l2jpx.gameserver.model.stats.Stat;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.CreatureSay;
import org.l2jpx.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.taskmanager.DecayTaskManager;

public class Attackable extends Npc
{
	// Raid
	private boolean _isRaid = false;
	private boolean _isRaidMinion = false;
	//
	private boolean _champion = false;
	private final Map<Creature, AggroInfo> _aggroList = new ConcurrentHashMap<>();
	private boolean _canReturnToSpawnPoint = true;
	private boolean _seeThroughSilentMove = false;
	// Manor
	private boolean _seeded = false;
	private Seed _seed = null;
	private int _seederObjId = 0;
	private final AtomicReference<ItemHolder> _harvestItem = new AtomicReference<>();
	// Spoil
	private int _spoilerObjectId;
	private boolean _plundered = false;
	private final AtomicReference<Collection<ItemHolder>> _sweepItems = new AtomicReference<>();
	// Over-hit
	private boolean _overhit;
	private double _overhitDamage;
	private Creature _overhitAttacker;
	// Command channel
	private CommandChannel _firstCommandChannelAttacked = null;
	private CommandChannelTimer _commandChannelTimer = null;
	private long _commandChannelLastAttack = 0;
	// Soul crystal
	private boolean _absorbed;
	private final Map<Integer, AbsorberInfo> _absorbersList = new ConcurrentHashMap<>();
	// Misc
	private boolean _mustGiveExpSp;
	
	/**
	 * Constructor of Attackable (use Creature and Npc constructor).<br>
	 * Actions:<br>
	 * Call the Creature constructor to set the _template of the Attackable (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)<br>
	 * Set the name of the Attackable<br>
	 * Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it.
	 * @param template the template to apply to the NPC.
	 */
	public Attackable(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.Attackable);
		setInvul(false);
		_mustGiveExpSp = true;
	}
	
	@Override
	public AttackableStatus getStatus()
	{
		return (AttackableStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new AttackableStatus(this));
	}
	
	@Override
	protected CreatureAI initAI()
	{
		return new AttackableAI(this);
	}
	
	public Map<Creature, AggroInfo> getAggroList()
	{
		return _aggroList;
	}
	
	public boolean canReturnToSpawnPoint()
	{
		return _canReturnToSpawnPoint;
	}
	
	public void setCanReturnToSpawnPoint(boolean value)
	{
		_canReturnToSpawnPoint = value;
	}
	
	public boolean canSeeThroughSilentMove()
	{
		return _seeThroughSilentMove;
	}
	
	public void setSeeThroughSilentMove(boolean value)
	{
		_seeThroughSilentMove = value;
	}
	
	/**
	 * Use the skill if minimum checks are pass.
	 * @param skill the skill
	 */
	public void useMagic(Skill skill)
	{
		if (!SkillCaster.checkUseConditions(this, skill))
		{
			return;
		}
		
		final WorldObject target = skill.getTarget(this, false, false, false);
		if (target != null)
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		}
	}
	
	/**
	 * Reduce the current HP of the Attackable, update its _aggroList and launch the doDie Task if necessary.
	 * @param attacker The Creature who attacks
	 * @param isDOT
	 * @param skill
	 */
	@Override
	public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect)
	{
		if (_isRaid && !isMinion() && (attacker != null) && (attacker.getParty() != null) && attacker.getParty().isInCommandChannel() && attacker.getParty().getCommandChannel().meetRaidWarCondition(this))
		{
			if (_firstCommandChannelAttacked == null) // looting right isn't set
			{
				synchronized (this)
				{
					if (_firstCommandChannelAttacked == null)
					{
						_firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
						if (_firstCommandChannelAttacked != null)
						{
							_commandChannelTimer = new CommandChannelTimer(this);
							_commandChannelLastAttack = System.currentTimeMillis();
							ThreadPool.schedule(_commandChannelTimer, 10000); // check for last attack
							_firstCommandChannelAttacked.broadcastPacket(new CreatureSay(null, ChatType.PARTYROOM_ALL, "", "You have looting rights!")); // TODO: retail msg
						}
					}
				}
			}
			else if (attacker.getParty().getCommandChannel().equals(_firstCommandChannelAttacked)) // is in same channel
			{
				_commandChannelLastAttack = System.currentTimeMillis(); // update last attack time
			}
		}
		
		// Add damage and hate to the attacker AggroInfo of the Attackable _aggroList
		if (attacker != null)
		{
			addDamage(attacker, (int) value, skill);
			
			// Check Raidboss attack. Character will be petrified if attacking a raid that's more than 8 levels lower. In retail you deal damage to raid before curse.
			if (_isRaid && giveRaidCurse() && !Config.RAID_DISABLE_CURSE && (attacker.getLevel() > (getLevel() + 8)))
			{
				final Skill raidCurse = CommonSkill.RAID_CURSE2.getSkill();
				if (raidCurse != null)
				{
					raidCurse.applyEffects(this, attacker);
				}
			}
		}
		
		// If this Attackable is a Monster and it has spawned minions, call its minions to battle
		if (isMonster())
		{
			Monster master = (Monster) this;
			if (master.hasMinions())
			{
				master.getMinionList().onAssist(this, attacker);
			}
			
			master = master.getLeader();
			if ((master != null) && master.hasMinions())
			{
				master.getMinionList().onAssist(this, attacker);
			}
		}
		// Reduce the current HP of the Attackable and launch the doDie Task if necessary
		super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect);
	}
	
	public synchronized void setMustRewardExpSp(boolean value)
	{
		_mustGiveExpSp = value;
	}
	
	public synchronized boolean getMustRewardExpSP()
	{
		return _mustGiveExpSp && !isFakePlayer();
	}
	
	/**
	 * Kill the Attackable (the corpse disappeared after 7 seconds), distribute rewards (EXP, SP, Drops...) and notify Quest Engine.<br>
	 * Actions:<br>
	 * Distribute Exp and SP rewards to Player (including Summon owner) that hit the Attackable and to their Party members<br>
	 * Notify the Quest Engine of the Attackable death if necessary.<br>
	 * Kill the Npc (the corpse disappeared after 7 seconds)<br>
	 * Caution: This method DOESN'T GIVE rewards to Pet.
	 * @param killer The Creature that has killed the Attackable
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		// Kill the Npc (the corpse disappeared after 7 seconds)
		if (!super.doDie(killer))
		{
			return false;
		}
		
		// Notify to scripts.
		if ((killer != null) && (killer.getActingPlayer() != null) && EventDispatcher.getInstance().hasListener(EventType.ON_ATTACKABLE_KILL, this))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(killer.getActingPlayer(), this, killer.isSummon()), this);
		}
		
		// Notify to minions if there are.
		if (isMonster())
		{
			final Monster mob = (Monster) this;
			if ((mob.getLeader() != null) && mob.getLeader().hasMinions())
			{
				final int respawnTime = Config.MINIONS_RESPAWN_TIME.containsKey(getId()) ? Config.MINIONS_RESPAWN_TIME.get(getId()) * 1000 : -1;
				mob.getLeader().getMinionList().onMinionDie(mob, respawnTime);
			}
			
			if (mob.hasMinions())
			{
				mob.getMinionList().onMasterDie(false);
			}
		}
		
		return true;
	}
	
	private static class PartyContainer
	{
		public Party party;
		public long damage;
		
		public PartyContainer(Party party, long damage)
		{
			this.party = party;
			this.damage = damage;
		}
	}
	
	/**
	 * Distribute Exp and SP rewards to Player (including Summon owner) that hit the Attackable and to their Party members.<br>
	 * Actions:<br>
	 * Get the Player owner of the Servitor (if necessary) and Party in progress.<br>
	 * Calculate the Experience and SP rewards in function of the level difference.<br>
	 * Add Exp and SP rewards to Player (including Summon penalty) and to Party members in the known area of the last attacker.<br>
	 * Caution : This method DOESN'T GIVE rewards to Pet.
	 * @param lastAttacker The Creature that has killed the Attackable
	 */
	@Override
	protected void calculateRewards(Creature lastAttacker)
	{
		try
		{
			if (_aggroList.isEmpty())
			{
				return;
			}
			
			// NOTE: Concurrent-safe map is used because while iterating to verify all conditions sometimes an entry must be removed.
			final Map<Player, DamageDoneInfo> rewards = new ConcurrentHashMap<>();
			
			Player maxDealer = null;
			long maxDamage = 0;
			long totalDamage = 0;
			
			// While Iterating over This Map Removing Object is Not Allowed
			// Go through the _aggroList of the Attackable
			for (AggroInfo info : _aggroList.values())
			{
				// Get the Creature corresponding to this attacker
				final Player attacker = info.getAttacker().getActingPlayer();
				if (attacker == null)
				{
					continue;
				}
				
				// Get damages done by this attacker
				final long damage = info.getDamage();
				
				// Prevent unwanted behavior
				if (damage > 1)
				{
					// Check if damage dealer isn't too far from this (killed monster)
					if (calculateDistance3D(attacker) > Config.ALT_PARTY_RANGE)
					{
						continue;
					}
					
					totalDamage += damage;
					
					// Calculate real damages (Summoners should get own damage plus summon's damage)
					final DamageDoneInfo reward = rewards.computeIfAbsent(attacker, DamageDoneInfo::new);
					reward.addDamage(damage);
					
					if (reward.getDamage() > maxDamage)
					{
						maxDealer = attacker;
						maxDamage = reward.getDamage();
					}
				}
			}
			
			final List<PartyContainer> damagingParties = new ArrayList<>();
			for (AggroInfo info : _aggroList.values())
			{
				final Creature attacker = info.getAttacker();
				if (attacker == null)
				{
					continue;
				}
				
				long totalMemberDamage = 0;
				final Party party = attacker.getParty();
				if (party == null)
				{
					continue;
				}
				
				Optional<PartyContainer> partyContainerStream = Optional.empty();
				for (int i = 0, damagingPartiesSize = damagingParties.size(); i < damagingPartiesSize; i++)
				{
					final PartyContainer p = damagingParties.get(i);
					if (p.party == party)
					{
						partyContainerStream = Optional.of(p);
						break;
					}
				}
				
				final PartyContainer container = partyContainerStream.orElse(new PartyContainer(party, 0L));
				final List<Player> members = party.getMembers();
				for (Player e : members)
				{
					final AggroInfo memberAggro = _aggroList.get(e);
					if (memberAggro == null)
					{
						continue;
					}
					
					if (memberAggro.getDamage() > 1)
					{
						totalMemberDamage += memberAggro.getDamage();
					}
				}
				container.damage = totalMemberDamage;
				
				if (!partyContainerStream.isPresent())
				{
					damagingParties.add(container);
				}
			}
			
			final PartyContainer mostDamageParty;
			damagingParties.sort(Comparator.comparingLong(c -> c.damage));
			mostDamageParty = !damagingParties.isEmpty() ? damagingParties.get(0) : null;
			
			// Calculate raidboss points
			if (_isRaid && !_isRaidMinion)
			{
				final Player player = (maxDealer != null) && maxDealer.isOnline() ? maxDealer : lastAttacker.getActingPlayer();
				broadcastPacket(new SystemMessage(SystemMessageId.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL));
				final int raidbossPoints = (int) (getTemplate().getRaidPoints() * Config.RATE_RAIDBOSS_POINTS);
				final Party party = player.getParty();
				if (party != null)
				{
					final CommandChannel command = party.getCommandChannel();
					final List<Player> members = new ArrayList<>();
					if (command != null)
					{
						for (Player p : command.getMembers())
						{
							if (p.calculateDistance3D(this) < Config.ALT_PARTY_RANGE)
							{
								members.add(p);
							}
						}
					}
					else
					{
						for (Player p : player.getParty().getMembers())
						{
							if (p.calculateDistance3D(this) < Config.ALT_PARTY_RANGE)
							{
								members.add(p);
							}
						}
					}
					
					members.forEach(p ->
					{
						final int points = Math.max(raidbossPoints / members.size(), 1);
						p.increaseRaidbossPoints(points);
						p.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S).addInt(points));
						if (p.isNoble())
						{
							Hero.getInstance().setRBkilled(p.getObjectId(), getId());
						}
					});
				}
				else
				{
					final int points = Math.max(raidbossPoints, 1);
					player.increaseRaidbossPoints(points);
					player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S).addInt(points));
					if (player.isNoble())
					{
						Hero.getInstance().setRBkilled(player.getObjectId(), getId());
					}
				}
			}
			
			if ((mostDamageParty != null) && (mostDamageParty.damage > maxDamage))
			{
				Player leader = mostDamageParty.party.getLeader();
				doItemDrop(leader);
				EventDropManager.getInstance().doEventDrop(leader, this);
			}
			else
			{
				doItemDrop((maxDealer != null) && maxDealer.isOnline() ? maxDealer : lastAttacker);
				EventDropManager.getInstance().doEventDrop(lastAttacker, this);
			}
			
			if (!getMustRewardExpSP())
			{
				return;
			}
			
			if (!rewards.isEmpty())
			{
				for (DamageDoneInfo reward : rewards.values())
				{
					if (reward == null)
					{
						continue;
					}
					
					// Attacker to be rewarded
					final Player attacker = reward.getAttacker();
					
					// Total amount of damage done
					final long damage = reward.getDamage();
					
					// Get party
					final Party attackerParty = attacker.getParty();
					
					// Penalty applied to the attacker's XP
					// If this attacker have servitor, get Exp Penalty applied for the servitor.
					float penalty = 1;
					
					for (Summon summon : attacker.getServitors().values())
					{
						if (((Servitor) summon).getExpMultiplier() > 1)
						{
							penalty = ((Servitor) summon).getExpMultiplier();
							break;
						}
					}
					
					// If there's NO party in progress
					if (attackerParty == null)
					{
						// Calculate Exp and SP rewards
						if (isInSurroundingRegion(attacker))
						{
							// Calculate the difference of level between this attacker (player or servitor owner) and the Attackable
							// mob = 24, atk = 10, diff = -14 (full xp)
							// mob = 24, atk = 28, diff = 4 (some xp)
							// mob = 24, atk = 50, diff = 26 (no xp)
							final double[] expSp = calculateExpAndSp(attacker.getLevel(), damage, totalDamage);
							double exp = expSp[0];
							double sp = expSp[1];
							if (Config.CHAMPION_ENABLE && _champion)
							{
								exp *= Config.CHAMPION_REWARDS_EXP_SP;
								sp *= Config.CHAMPION_REWARDS_EXP_SP;
							}
							
							exp *= penalty;
							
							// Check for an over-hit enabled strike
							final Creature overhitAttacker = _overhitAttacker;
							if (_overhit && (overhitAttacker != null) && (overhitAttacker.getActingPlayer() != null) && (attacker == overhitAttacker.getActingPlayer()))
							{
								attacker.sendPacket(SystemMessageId.OVER_HIT);
								attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker.getObjectId(), getObjectId(), ExMagicAttackInfo.OVERHIT));
								exp += calculateOverhitExp(exp);
							}
							
							// Distribute the Exp and SP between the Player and its Summon
							if (!attacker.isDead())
							{
								exp = attacker.getStat().getValue(Stat.EXPSP_RATE, exp) * Config.EXP_AMOUNT_MULTIPLIERS[attacker.getClassId().getId()];
								sp = attacker.getStat().getValue(Stat.EXPSP_RATE, sp) * Config.SP_AMOUNT_MULTIPLIERS[attacker.getClassId().getId()];
								
								// Premium rates
								if (attacker.hasPremiumStatus())
								{
									exp *= Config.PREMIUM_RATE_XP;
									sp *= Config.PREMIUM_RATE_SP;
								}
								
								attacker.addExpAndSp(exp, sp, useVitalityRate());
								if (exp > 0)
								{
									final Clan clan = attacker.getClan();
									if (clan != null)
									{
										double finalExp = exp;
										if (useVitalityRate())
										{
											finalExp *= attacker.getStat().getExpBonusMultiplier();
										}
										clan.addHuntingPoints(attacker, this, finalExp);
									}
									if (useVitalityRate())
									{
										attacker.updateVitalityPoints(getVitalityPoints(attacker.getLevel(), exp, _isRaid), true, false);
										PcCafePointsManager.getInstance().givePcCafePoint(attacker, exp);
									}
								}
							}
						}
					}
					else
					{
						// share with party members
						long partyDmg = 0;
						double partyMul = 1;
						int partyLvl = 0;
						
						// Get all Creature that can be rewarded in the party
						final List<Player> rewardedMembers = new ArrayList<>();
						// Go through all Player in the party
						final List<Player> groupMembers = attackerParty.isInCommandChannel() ? attackerParty.getCommandChannel().getMembers() : attackerParty.getMembers();
						for (Player partyPlayer : groupMembers)
						{
							if ((partyPlayer == null) || partyPlayer.isDead())
							{
								continue;
							}
							
							// Get the RewardInfo of this Player from Attackable rewards
							final DamageDoneInfo reward2 = rewards.get(partyPlayer);
							
							// If the Player is in the Attackable rewards add its damages to party damages
							if (reward2 != null)
							{
								if (calculateDistance3D(partyPlayer) < Config.ALT_PARTY_RANGE)
								{
									partyDmg += reward2.getDamage(); // Add Player damages to party damages
									rewardedMembers.add(partyPlayer);
									
									if (partyPlayer.getLevel() > partyLvl)
									{
										if (attackerParty.isInCommandChannel())
										{
											partyLvl = attackerParty.getCommandChannel().getLevel();
										}
										else
										{
											partyLvl = partyPlayer.getLevel();
										}
									}
								}
								rewards.remove(partyPlayer); // Remove the Player from the Attackable rewards
							}
							else if (calculateDistance3D(partyPlayer) < Config.ALT_PARTY_RANGE)
							{
								rewardedMembers.add(partyPlayer);
								if (partyPlayer.getLevel() > partyLvl)
								{
									if (attackerParty.isInCommandChannel())
									{
										partyLvl = attackerParty.getCommandChannel().getLevel();
									}
									else
									{
										partyLvl = partyPlayer.getLevel();
									}
								}
							}
						}
						
						// If the party didn't killed this Attackable alone
						if (partyDmg < totalDamage)
						{
							partyMul = ((double) partyDmg / totalDamage);
						}
						
						// Calculate Exp and SP rewards
						final double[] expSp = calculateExpAndSp(partyLvl, partyDmg, totalDamage);
						double exp = expSp[0];
						double sp = expSp[1];
						if (Config.CHAMPION_ENABLE && _champion)
						{
							exp *= Config.CHAMPION_REWARDS_EXP_SP;
							sp *= Config.CHAMPION_REWARDS_EXP_SP;
						}
						
						exp *= partyMul;
						sp *= partyMul;
						
						// Check for an over-hit enabled strike
						// (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
						final Creature overhitAttacker = _overhitAttacker;
						if (_overhit && (overhitAttacker != null) && (overhitAttacker.getActingPlayer() != null) && (attacker == overhitAttacker.getActingPlayer()))
						{
							attacker.sendPacket(SystemMessageId.OVER_HIT);
							attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker.getObjectId(), getObjectId(), ExMagicAttackInfo.OVERHIT));
							exp += calculateOverhitExp(exp);
						}
						
						// Distribute Experience and SP rewards to Player Party members in the known area of the last attacker
						if (partyDmg > 0)
						{
							attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl, this);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "", e);
		}
	}
	
	@Override
	public void addAttackerToAttackByList(Creature creature)
	{
		if ((creature == null) || (creature == this))
		{
			return;
		}
		
		for (WeakReference<Creature> ref : getAttackByList())
		{
			if (ref.get() == creature)
			{
				return;
			}
		}
		
		getAttackByList().add(new WeakReference<>(creature));
	}
	
	public Creature getMainDamageDealer()
	{
		if (_aggroList.isEmpty())
		{
			return null;
		}
		
		long damage = 0;
		Creature damageDealer = null;
		for (AggroInfo info : _aggroList.values())
		{
			if ((info != null) && (info.getDamage() > damage) && (calculateDistance3D(info.getAttacker()) < Config.ALT_PARTY_RANGE))
			{
				damage = info.getDamage();
				damageDealer = info.getAttacker();
			}
		}
		
		return damageDealer;
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the Attackable _aggroList.
	 * @param attacker The Creature that gave damages to this Attackable
	 * @param damage The number of damages given by the attacker Creature
	 * @param skill
	 */
	public void addDamage(Creature attacker, int damage, Skill skill)
	{
		if (attacker == null)
		{
			return;
		}
		
		// Notify the Attackable AI with EVT_ATTACKED
		if (!isDead())
		{
			try
			{
				// If monster is on walk - stop it
				if (isWalker() && !isCoreAIDisabled() && WalkingManager.getInstance().isOnWalk(this))
				{
					WalkingManager.getInstance().stopMoving(this, false, true);
				}
				
				getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
				
				// Calculate the amount of hate this attackable receives from this attack.
				double hateValue = (damage * 100) / (getLevel() + 7);
				if (skill == null)
				{
					hateValue *= attacker.getStat().getMul(Stat.HATE_ATTACK, 1);
				}
				
				addDamageHate(attacker, damage, (int) hateValue);
				
				final Player player = attacker.getActingPlayer();
				if ((player != null) && EventDispatcher.getInstance().hasListener(EventType.ON_ATTACKABLE_ATTACK, this))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(player, this, damage, skill, attacker.isSummon()), this);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "", e);
			}
		}
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the Attackable _aggroList.
	 * @param creature The Creature that gave damages to this Attackable
	 * @param damage The number of damages given by the attacker Creature
	 * @param aggroValue The hate (=damage) given by the attacker Creature
	 */
	public void addDamageHate(Creature creature, long damage, long aggroValue)
	{
		Creature attacker = creature;
		if ((attacker == null) || (attacker == this))
		{
			return;
		}
		
		// Check if fake players should aggro each other.
		if (isFakePlayer() && !Config.FAKE_PLAYER_AGGRO_FPC && attacker.isFakePlayer())
		{
			return;
		}
		
		Player targetPlayer = attacker.getActingPlayer();
		final Creature summoner = attacker.getSummoner();
		if (attacker.isNpc() && (summoner != null) && summoner.isPlayer() && !attacker.isTargetable())
		{
			targetPlayer = summoner.getActingPlayer();
			attacker = summoner;
		}
		
		// Get the AggroInfo of the attacker Creature from the _aggroList of the Attackable
		final AggroInfo ai = _aggroList.computeIfAbsent(attacker, AggroInfo::new);
		ai.addDamage(damage);
		
		// traps does not cause aggro
		// making this hack because not possible to determine if damage made by trap
		// so just check for triggered trap here
		long aggro = aggroValue;
		if ((targetPlayer == null) || (targetPlayer.getTrap() == null) || !targetPlayer.getTrap().isTriggered())
		{
			ai.addHate(aggro);
		}
		
		if ((targetPlayer != null) && (aggro == 0))
		{
			addDamageHate(attacker, 0, 1);
			
			// Set the intention to the Attackable to AI_INTENTION_ACTIVE
			if (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
			{
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
			
			// Notify to scripts
			if (EventDispatcher.getInstance().hasListener(EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER, this))
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAggroRangeEnter(this, targetPlayer, attacker.isSummon()), this);
			}
		}
		else if ((targetPlayer == null) && (aggro == 0))
		{
			aggro = 1;
			ai.addHate(1);
		}
		
		// Set the intention to the Attackable to AI_INTENTION_ACTIVE
		if ((aggro != 0) && (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE))
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}
	
	public void reduceHate(Creature target, long amount)
	{
		if (target == null) // whole aggrolist
		{
			final Creature mostHated = getMostHated();
			if (mostHated == null) // makes target passive for a moment more
			{
				((AttackableAI) getAI()).setGlobalAggro(-25);
				return;
			}
			
			for (AggroInfo ai : _aggroList.values())
			{
				ai.addHate(amount);
			}
			
			if (getHating(mostHated) >= 0)
			{
				((AttackableAI) getAI()).setGlobalAggro(-25);
				clearAggroList();
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				if (!isFakePlayer())
				{
					setWalking();
				}
			}
			return;
		}
		
		final AggroInfo ai = _aggroList.get(target);
		if (ai == null)
		{
			return;
		}
		
		ai.addHate(amount);
		if ((ai.getHate() >= 0) && (getMostHated() == null))
		{
			((AttackableAI) getAI()).setGlobalAggro(-25);
			clearAggroList();
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			if (!isFakePlayer())
			{
				setWalking();
			}
		}
	}
	
	/**
	 * Clears _aggroList hate of the Creature without removing from the list.
	 * @param target
	 */
	public void stopHating(Creature target)
	{
		if (target == null)
		{
			return;
		}
		
		final AggroInfo ai = _aggroList.get(target);
		if (ai != null)
		{
			ai.stopHate();
		}
	}
	
	/**
	 * @return the most hated Creature of the Attackable _aggroList.
	 */
	public Creature getMostHated()
	{
		if (_aggroList.isEmpty() || isAlikeDead())
		{
			return null;
		}
		
		Creature mostHated = null;
		long maxHate = 0;
		
		// While Interacting over This Map Removing Object is Not Allowed
		// Go through the aggroList of the Attackable
		for (AggroInfo ai : _aggroList.values())
		{
			if (ai == null)
			{
				continue;
			}
			
			if (ai.checkHate(this) > maxHate)
			{
				mostHated = ai.getAttacker();
				maxHate = ai.getHate();
			}
		}
		
		return mostHated;
	}
	
	/**
	 * @return the 2 most hated Creature of the Attackable _aggroList.
	 */
	public List<Creature> get2MostHated()
	{
		if (_aggroList.isEmpty() || isAlikeDead())
		{
			return null;
		}
		
		Creature mostHated = null;
		Creature secondMostHated = null;
		long maxHate = 0;
		final List<Creature> result = new ArrayList<>();
		
		// While iterating over this map removing objects is not allowed
		// Go through the aggroList of the Attackable
		for (AggroInfo ai : _aggroList.values())
		{
			if (ai.checkHate(this) > maxHate)
			{
				secondMostHated = mostHated;
				mostHated = ai.getAttacker();
				maxHate = ai.getHate();
			}
		}
		
		result.add(mostHated);
		
		final Creature secondMostHatedFinal = secondMostHated;
		boolean found = false;
		for (WeakReference<Creature> ref : getAttackByList())
		{
			if (ref.get() == secondMostHatedFinal)
			{
				found = true;
				break;
			}
		}
		if (found)
		{
			result.add(secondMostHated);
		}
		else
		{
			result.add(null);
		}
		
		return result;
	}
	
	public List<Creature> getHateList()
	{
		if (_aggroList.isEmpty() || isAlikeDead())
		{
			return null;
		}
		
		final List<Creature> result = new ArrayList<>();
		for (AggroInfo ai : _aggroList.values())
		{
			ai.checkHate(this);
			
			result.add(ai.getAttacker());
		}
		return result;
	}
	
	/**
	 * @param target The Creature whose hate level must be returned
	 * @return the hate level of the Attackable against this Creature contained in _aggroList.
	 */
	public long getHating(Creature target)
	{
		if (_aggroList.isEmpty() || (target == null))
		{
			return 0;
		}
		
		final AggroInfo ai = _aggroList.get(target);
		if (ai == null)
		{
			return 0;
		}
		
		if (ai.getAttacker().isPlayer())
		{
			final Player act = (Player) ai.getAttacker();
			if (act.isInvisible() || act.isInvul() || act.isSpawnProtected())
			{
				// Remove Object Should Use This Method and Can be Blocked While Interacting
				_aggroList.remove(target);
				return 0;
			}
		}
		
		if (!ai.getAttacker().isSpawned() || ai.getAttacker().isInvisible())
		{
			_aggroList.remove(target);
			return 0;
		}
		
		if (ai.getAttacker().isAlikeDead())
		{
			ai.stopHate();
			return 0;
		}
		return ai.getHate();
	}
	
	public void doItemDrop(Creature mainDamageDealer)
	{
		doItemDrop(getTemplate(), mainDamageDealer);
	}
	
	/**
	 * Manage Base, Quests and Special Events drops of Attackable (called by calculateRewards).<br>
	 * Concept:<br>
	 * During a Special Event all Attackable can drop extra Items.<br>
	 * Those extra Items are defined in the table allNpcDateDrops of the EventDroplist.<br>
	 * Each Special Event has a start and end date to stop to drop extra Items automatically.<br>
	 * Actions:<br>
	 * Manage drop of Special Events created by GM for a defined period.<br>
	 * Get all possible drops of this Attackable from NpcTemplate and add it Quest drops.<br>
	 * For each possible drops (base + quests), calculate which one must be dropped (random).<br>
	 * Get each Item quantity dropped (random).<br>
	 * Create this or these Item corresponding to each Item Identifier dropped.<br>
	 * If the autoLoot mode is actif and if the Creature that has killed the Attackable is a Player, Give the item(s) to the Player that has killed the Attackable.<br>
	 * If the autoLoot mode isn't actif or if the Creature that has killed the Attackable is not a Player, add this or these item(s) in the world as a visible object at the position where mob was last.
	 * @param npcTemplate
	 * @param mainDamageDealer
	 */
	public void doItemDrop(NpcTemplate npcTemplate, Creature mainDamageDealer)
	{
		if (mainDamageDealer == null)
		{
			return;
		}
		
		final Player player = mainDamageDealer.getActingPlayer();
		
		// Don't drop anything if the last attacker or owner isn't Player
		if (player == null)
		{
			// unless its a fake player and they can drop items
			if (mainDamageDealer.isFakePlayer() && Config.FAKE_PLAYER_CAN_DROP_ITEMS)
			{
				final Collection<ItemHolder> deathItems = npcTemplate.calculateDrops(DropType.DROP, this, mainDamageDealer);
				if (deathItems != null)
				{
					for (ItemHolder drop : deathItems)
					{
						final ItemTemplate item = ItemData.getInstance().getTemplate(drop.getId());
						// Check if the autoLoot mode is active
						if (Config.AUTO_LOOT_ITEM_IDS.contains(item.getId()) || isFlying() || (!item.hasExImmediateEffect() && ((!_isRaid && Config.AUTO_LOOT) || (_isRaid && Config.AUTO_LOOT_RAIDS))))
						{
							// do nothing
						}
						else if (Config.AUTO_LOOT_HERBS && item.hasExImmediateEffect())
						{
							for (SkillHolder skillHolder : item.getAllSkills())
							{
								SkillCaster.triggerCast(mainDamageDealer, null, skillHolder.getSkill(), null, false);
							}
							mainDamageDealer.broadcastInfo(); // ? check if this is necessary
						}
						else
						{
							final Item droppedItem = dropItem(mainDamageDealer, drop); // drop the item on the ground
							if (Config.FAKE_PLAYER_CAN_PICKUP)
							{
								mainDamageDealer.getFakePlayerDrops().add(droppedItem);
							}
						}
					}
					deathItems.clear();
				}
			}
			return;
		}
		
		CursedWeaponsManager.getInstance().checkDrop(this, player);
		if (isSpoiled() && !_plundered)
		{
			_sweepItems.set(npcTemplate.calculateDrops(DropType.SPOIL, this, player));
		}
		
		final Collection<ItemHolder> deathItems = npcTemplate.calculateDrops(DropType.DROP, this, player);
		if (deathItems != null)
		{
			for (ItemHolder drop : deathItems)
			{
				final ItemTemplate item = ItemData.getInstance().getTemplate(drop.getId());
				// Check if the autoLoot mode is active
				if (Config.AUTO_LOOT_ITEM_IDS.contains(item.getId()) || isFlying() || (!item.hasExImmediateEffect() && ((!_isRaid && Config.AUTO_LOOT) || (_isRaid && Config.AUTO_LOOT_RAIDS))) || (item.hasExImmediateEffect() && Config.AUTO_LOOT_HERBS))
				{
					player.doAutoLoot(this, drop); // Give the item(s) to the Player that has killed the Attackable
				}
				else
				{
					dropItem(player, drop); // drop the item on the ground
				}
				
				// Broadcast message if RaidBoss was defeated
				if (_isRaid && !_isRaidMinion)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.C1_DIED_AND_DROPPED_S3_S2_S);
					sm.addString(getName());
					sm.addItemName(item);
					sm.addLong(drop.getCount());
					broadcastPacket(sm);
				}
			}
			deathItems.clear();
		}
	}
	
	/**
	 * @return the active weapon of this Attackable (= null).
	 */
	public Item getActiveWeapon()
	{
		return null;
	}
	
	/**
	 * Verifies if the creature is in the aggro list.
	 * @param creature the creature
	 * @return {@code true} if the creature is in the aggro list, {@code false} otherwise
	 */
	public boolean isInAggroList(Creature creature)
	{
		return (creature != null) && _aggroList.containsKey(creature);
	}
	
	/**
	 * Clear the _aggroList of the Attackable.
	 */
	public void clearAggroList()
	{
		_aggroList.clear();
		
		// clear overhit values
		_overhit = false;
		_overhitDamage = 0;
		_overhitAttacker = null;
	}
	
	/**
	 * @return {@code true} if there is a loot to sweep, {@code false} otherwise.
	 */
	@Override
	public boolean isSweepActive()
	{
		return _sweepItems.get() != null;
	}
	
	/**
	 * @return a copy of dummy items for the spoil loot.
	 */
	public List<ItemTemplate> getSpoilLootItems()
	{
		final Collection<ItemHolder> sweepItems = _sweepItems.get();
		final List<ItemTemplate> lootItems = new LinkedList<>();
		if (sweepItems != null)
		{
			for (ItemHolder item : sweepItems)
			{
				lootItems.add(ItemData.getInstance().getTemplate(item.getId()));
			}
		}
		return lootItems;
	}
	
	/**
	 * @return table containing all Item that can be spoiled.
	 */
	public Collection<ItemHolder> takeSweep()
	{
		return _sweepItems.getAndSet(null);
	}
	
	/**
	 * @return table containing all Item that can be harvested.
	 */
	public ItemHolder takeHarvest()
	{
		return _harvestItem.getAndSet(null);
	}
	
	/**
	 * Checks if the corpse is too old.
	 * @param attacker the player to validate
	 * @param remainingTime the time to check
	 * @param sendMessage if {@code true} will send a message of corpse too old
	 * @return {@code true} if the corpse is too old
	 */
	public boolean isOldCorpse(Player attacker, int remainingTime, boolean sendMessage)
	{
		if (isDead() && (DecayTaskManager.getInstance().getRemainingTime(this) < remainingTime))
		{
			if (sendMessage && (attacker != null))
			{
				attacker.sendPacket(SystemMessageId.THE_CORPSE_IS_TOO_OLD_THE_SKILL_CANNOT_BE_USED);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * @param sweeper the player to validate.
	 * @param sendMessage sendMessage if {@code true} will send a message of sweep not allowed.
	 * @return {@code true} if is the spoiler or is in the spoiler party.
	 */
	public boolean checkSpoilOwner(Player sweeper, boolean sendMessage)
	{
		if ((sweeper.getObjectId() != _spoilerObjectId) && !sweeper.isInLooterParty(_spoilerObjectId))
		{
			if (sendMessage)
			{
				sweeper.sendPacket(SystemMessageId.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Set the over-hit flag on the Attackable.
	 * @param status The status of the over-hit flag
	 */
	public void overhitEnabled(boolean status)
	{
		_overhit = status;
	}
	
	/**
	 * Set the over-hit values like the attacker who did the strike and the amount of damage done by the skill.
	 * @param attacker The Creature who hit on the Attackable using the over-hit enabled skill
	 * @param damage The amount of damage done by the over-hit enabled skill on the Attackable
	 */
	public void setOverhitValues(Creature attacker, double damage)
	{
		// Calculate the over-hit damage
		// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
		final double overhitDmg = -(getCurrentHp() - damage);
		if (overhitDmg < 0)
		{
			// we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
			// let's just clear all the over-hit related values
			overhitEnabled(false);
			_overhitDamage = 0;
			_overhitAttacker = null;
			return;
		}
		overhitEnabled(true);
		_overhitDamage = overhitDmg;
		_overhitAttacker = attacker;
	}
	
	/**
	 * Return the Creature who hit on the Attackable using an over-hit enabled skill.
	 * @return Creature attacker
	 */
	public Creature getOverhitAttacker()
	{
		return _overhitAttacker;
	}
	
	/**
	 * Return the amount of damage done on the Attackable using an over-hit enabled skill.
	 * @return double damage
	 */
	public double getOverhitDamage()
	{
		return _overhitDamage;
	}
	
	/**
	 * @return True if the Attackable was hit by an over-hit enabled skill.
	 */
	public boolean isOverhit()
	{
		return _overhit;
	}
	
	/**
	 * Activate the absorbed soul condition on the Attackable.
	 */
	public void absorbSoul()
	{
		_absorbed = true;
	}
	
	/**
	 * @return True if the Attackable had his soul absorbed.
	 */
	public boolean isAbsorbed()
	{
		return _absorbed;
	}
	
	/**
	 * Adds an attacker that successfully absorbed the soul of this Attackable into the _absorbersList.
	 * @param attacker
	 */
	public void addAbsorber(Player attacker)
	{
		// If we have no _absorbersList initiated, do it
		final AbsorberInfo ai = _absorbersList.get(attacker.getObjectId());
		
		// If the Creature attacker isn't already in the _absorbersList of this Attackable, add it
		if (ai == null)
		{
			_absorbersList.put(attacker.getObjectId(), new AbsorberInfo(attacker.getObjectId(), getCurrentHp()));
		}
		else
		{
			ai.setAbsorbedHp(getCurrentHp());
		}
		
		// Set this Attackable as absorbed
		absorbSoul();
	}
	
	public void resetAbsorbList()
	{
		_absorbed = false;
		_absorbersList.clear();
	}
	
	public Map<Integer, AbsorberInfo> getAbsorbersList()
	{
		return _absorbersList;
	}
	
	/**
	 * Calculate the Experience and SP to distribute to attacker (Player, Servitor or Party) of the Attackable.
	 * @param charLevel The killer level
	 * @param damage The damages given by the attacker (Player, Servitor or Party)
	 * @param totalDamage The total damage done
	 * @return
	 */
	private double[] calculateExpAndSp(int charLevel, long damage, long totalDamage)
	{
		final int levelDiff = charLevel - getLevel();
		double xp = Math.max(0, (getExpReward(charLevel) * damage) / totalDamage);
		double sp = Math.max(0, (getSpReward(charLevel) * damage) / totalDamage);
		
		// According to https://4gameforum.com/threads/483941/
		if (levelDiff > 2)
		{
			double mul;
			switch (levelDiff)
			{
				case 3:
				{
					mul = 0.97;
					break;
				}
				case 4:
				{
					mul = 0.80;
					break;
				}
				case 5:
				{
					mul = 0.61;
					break;
				}
				case 6:
				{
					mul = 0.37;
					break;
				}
				case 7:
				{
					mul = 0.22;
					break;
				}
				case 8:
				{
					mul = 0.13;
					break;
				}
				case 9:
				{
					mul = 0.08;
					break;
				}
				default:
				{
					mul = 0.05;
					break;
				}
			}
			xp *= mul;
			sp *= mul;
		}
		return new double[]
		{
			xp,
			sp
		};
	}
	
	public double calculateOverhitExp(double exp)
	{
		// Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the Attackable
		double overhitPercentage = ((_overhitDamage * 100) / getMaxHp());
		
		// Over-hit damage percentages are limited to 25% max
		if (overhitPercentage > 25)
		{
			overhitPercentage = 25;
		}
		
		// Get the overhit exp bonus according to the above over-hit damage percentage
		// (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
		return (overhitPercentage / 100) * exp;
	}
	
	/**
	 * Return True.
	 */
	@Override
	public boolean canBeAttacked()
	{
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// Clear mob spoil, seed
		setSpoilerObjectId(0);
		
		// Clear all aggro list and overhit
		clearAggroList();
		
		// Clear Harvester reward
		_harvestItem.set(null);
		_sweepItems.set(null);
		_plundered = false;
		
		// fake players
		if (isFakePlayer())
		{
			getFakePlayerDrops().clear(); // Clear existing fake player drops
			setReputation(0); // reset reputation
			setScriptValue(0); // remove pvp flag
			setRunning(); // don't walk
		}
		else
		{
			setWalking();
		}
		
		// Clear mod Seeded stat
		_seeded = false;
		_seed = null;
		_seederObjId = 0;
		
		// Check the region where this mob is, do not activate the AI if region is inactive.
		if (hasAI())
		{
			// Set the intention of the Attackable to AI_INTENTION_ACTIVE
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			
			// Check the region where this mob is, do not activate the AI if region is inactive.
			if (!isInActiveRegion())
			{
				getAI().stopAITask();
			}
		}
	}
	
	@Override
	public void onRespawn()
	{
		// Reset champion state
		_champion = false;
		
		// Set champion on next spawn
		if (Config.CHAMPION_ENABLE && isMonster() && !isQuestMonster() && !getTemplate().isUndying() && !_isRaid && !_isRaidMinion && (Config.CHAMPION_FREQUENCY > 0) && (getLevel() >= Config.CHAMP_MIN_LEVEL) && (getLevel() <= Config.CHAMP_MAX_LEVEL) && (Config.CHAMPION_ENABLE_IN_INSTANCES || (getInstanceId() == 0)))
		{
			if (Rnd.get(100) < Config.CHAMPION_FREQUENCY)
			{
				_champion = true;
			}
			if (Config.SHOW_CHAMPION_AURA)
			{
				setTeam(_champion ? Team.RED : Team.NONE, false);
			}
		}
		
		// Reset the rest of NPC related states
		super.onRespawn();
	}
	
	/**
	 * Checks if it is spoiled.
	 * @return {@code true} if it is spoiled, {@code false} otherwise
	 */
	public boolean isSpoiled()
	{
		return _spoilerObjectId != 0;
	}
	
	/**
	 * Gets the spoiler object ID.
	 * @return the spoiler object ID if it is spoiled, 0 otherwise
	 */
	public int getSpoilerObjectId()
	{
		return _spoilerObjectId;
	}
	
	/**
	 * Sets the spoiler object ID.
	 * @param spoilerObjectId spoilerObjectId the spoiler object ID
	 */
	public void setSpoilerObjectId(int spoilerObjectId)
	{
		_spoilerObjectId = spoilerObjectId;
	}
	
	/**
	 * Sets state of the mob to plundered.
	 * @param player
	 */
	public void setPlundered(Player player)
	{
		_plundered = true;
		_spoilerObjectId = player.getObjectId();
		_sweepItems.set(getTemplate().calculateDrops(DropType.SPOIL, this, player));
	}
	
	/**
	 * Sets state of the mob to seeded. Parameters needed to be set before.
	 * @param seeder
	 */
	public void setSeeded(Player seeder)
	{
		if ((_seed != null) && (_seederObjId == seeder.getObjectId()))
		{
			_seeded = true;
			int count = 1;
			for (int skillId : getTemplate().getSkills().keySet())
			{
				switch (skillId)
				{
					case 4303: // Strong type x2
					{
						count *= 2;
						break;
					}
					case 4304: // Strong type x3
					{
						count *= 3;
						break;
					}
					case 4305: // Strong type x4
					{
						count *= 4;
						break;
					}
					case 4306: // Strong type x5
					{
						count *= 5;
						break;
					}
					case 4307: // Strong type x6
					{
						count *= 6;
						break;
					}
					case 4308: // Strong type x7
					{
						count *= 7;
						break;
					}
					case 4309: // Strong type x8
					{
						count *= 8;
						break;
					}
					case 4310: // Strong type x9
					{
						count *= 9;
						break;
					}
				}
			}
			
			// hi-level mobs bonus
			final int diff = getLevel() - _seed.getLevel() - 5;
			if (diff > 0)
			{
				count += diff;
			}
			_harvestItem.set(new ItemHolder(_seed.getCropId(), count * Config.RATE_DROP_MANOR));
		}
	}
	
	/**
	 * Sets the seed parameters, but not the seed state
	 * @param seed - instance {@link Seed} of used seed
	 * @param seeder - player who sows the seed
	 */
	public void setSeeded(Seed seed, Player seeder)
	{
		if (!_seeded)
		{
			_seed = seed;
			_seederObjId = seeder.getObjectId();
		}
	}
	
	public int getSeederId()
	{
		return _seederObjId;
	}
	
	public Seed getSeed()
	{
		return _seed;
	}
	
	public boolean isSeeded()
	{
		return _seeded;
	}
	
	/**
	 * Check if the server allows Random Animation.
	 */
	// This is located here because Monster and FriendlyMob both extend this class. The other non-pc instances extend either Npc or Monster.
	@Override
	public boolean hasRandomAnimation()
	{
		return ((Config.MAX_MONSTER_ANIMATION > 0) && isRandomAnimationEnabled() && !(this instanceof GrandBoss));
	}
	
	public void setCommandChannelTimer(CommandChannelTimer commandChannelTimer)
	{
		_commandChannelTimer = commandChannelTimer;
	}
	
	public CommandChannelTimer getCommandChannelTimer()
	{
		return _commandChannelTimer;
	}
	
	public CommandChannel getFirstCommandChannelAttacked()
	{
		return _firstCommandChannelAttacked;
	}
	
	public void setFirstCommandChannelAttacked(CommandChannel firstCommandChannelAttacked)
	{
		_firstCommandChannelAttacked = firstCommandChannelAttacked;
	}
	
	/**
	 * @return the _commandChannelLastAttack
	 */
	public long getCommandChannelLastAttack()
	{
		return _commandChannelLastAttack;
	}
	
	/**
	 * @param channelLastAttack the _commandChannelLastAttack to set
	 */
	public void setCommandChannelLastAttack(long channelLastAttack)
	{
		_commandChannelLastAttack = channelLastAttack;
	}
	
	public void returnHome()
	{
		clearAggroList();
		
		if (hasAI() && (getSpawn() != null))
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, getSpawn().getLocation());
		}
	}
	
	/*
	 * Return vitality points decrease (if positive) or increase (if negative) based on damage. Maximum for damage = maxHp.
	 */
	public int getVitalityPoints(int level, double exp, boolean isBoss)
	{
		if ((getLevel() <= 0) || (getExpReward(level) <= 0) || (isBoss && (Config.VITALITY_CONSUME_BY_BOSS == 0)))
		{
			return 0;
		}
		
		int points;
		if (level < 85)
		{
			points = Math.max((int) ((exp / 1000) * Math.max(level - getLevel(), 1)), 1);
		}
		else
		{
			points = Math.max((int) ((exp / (isBoss ? Config.VITALITY_CONSUME_BY_BOSS : Config.VITALITY_CONSUME_BY_MOB)) * Math.max(level - getLevel(), 1)), 1);
		}
		
		return -points;
	}
	
	/*
	 * True if vitality rate for exp and sp should be applied
	 */
	public boolean useVitalityRate()
	{
		return !_champion || Config.CHAMPION_ENABLE_VITALITY;
	}
	
	/** Return True if the Creature is RaidBoss or his minion. */
	@Override
	public boolean isRaid()
	{
		return _isRaid;
	}
	
	/**
	 * Set this Npc as a Raid instance.
	 * @param isRaid
	 */
	public void setIsRaid(boolean isRaid)
	{
		_isRaid = isRaid;
	}
	
	/**
	 * Set this Npc as a Minion instance.
	 * @param value
	 */
	public void setIsRaidMinion(boolean value)
	{
		_isRaid = value;
		_isRaidMinion = value;
	}
	
	@Override
	public boolean isRaidMinion()
	{
		return _isRaidMinion;
	}
	
	@Override
	public boolean isMinion()
	{
		return getLeader() != null;
	}
	
	/**
	 * @return leader of this minion or null.
	 */
	public Attackable getLeader()
	{
		return null;
	}
	
	@Override
	public boolean isChampion()
	{
		return _champion;
	}
	
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	@Override
	public void setTarget(WorldObject object)
	{
		if (isDead())
		{
			return;
		}
		
		if (object == null)
		{
			final WorldObject target = getTarget();
			if (target != null)
			{
				_aggroList.remove(target);
			}
			if (_aggroList.isEmpty())
			{
				if (getAI() instanceof AttackableAI)
				{
					((AttackableAI) getAI()).setGlobalAggro(-25);
				}
				if (!isFakePlayer())
				{
					setWalking();
				}
				clearAggroList();
			}
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
		super.setTarget(object);
	}
}
