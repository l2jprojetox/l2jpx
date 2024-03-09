package net.l2jpx.gameserver.autofarm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.geo.GeoEngine;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.handler.ItemHandler;
import net.l2jpx.gameserver.handler.voicedcommandhandlers.AutoFarm;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2ShortCut;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Skill.SkillType;
import net.l2jpx.gameserver.model.L2Summon;
import net.l2jpx.gameserver.model.L2WorldRegion;
import net.l2jpx.gameserver.model.actor.instance.L2ChestInstance;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.ExShowScreenMessage;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.thread.ThreadPoolManager;
import net.l2jpx.gameserver.util.Util;
import net.l2jpx.util.database.L2DatabaseFactory;
import net.l2jpx.util.random.Rnd;

public class AutofarmPlayerRoutine
{
	
	private final L2PcInstance player;
	private ScheduledFuture<?> task;
	private L2Object committedTarget = null;
	
	public AutofarmPlayerRoutine(L2PcInstance player)
	{
		this.player = player;
	}
	
	public void start()
	{
		if (task == null)
		{
			if (isIpAllowed(player.getIP()))
			{
				player.sendMessage("Solo puedes usar Auto Farm con una IP a la vez.");
				return;
			}
			task = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this::executeRoutine, 450, 450);
			
			player.sendPacket(new ExShowScreenMessage("Auto Farming Actived...", 5 * 1000));
			player.sendPacket(new SystemMessage(SystemMessageId.AUTO_FARM_ACTIVATED));
		}
	}
	
	public void stop()
	{
		if (task != null)
		{
			task.cancel(false);
			task = null;
			
			player.sendPacket(new ExShowScreenMessage("Auto Farming Deactivated...", 5 * 1000));
			player.sendPacket(new SystemMessage(SystemMessageId.AUTO_FARM_DESACTIVATED));
		}
	}
	
	public static boolean isIpAllowed(String ip)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String selectSql = "SELECT * FROM Auto_Farm_Ip WHERE ip = ?";
			try (PreparedStatement selectStatement = con.prepareStatement(selectSql))
			{
				
				selectStatement.setString(1, ip);
				try (ResultSet result = selectStatement.executeQuery())
				{
					return result.next();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static void removeIpEntry(int charId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			String deleteSql = "DELETE FROM Auto_Farm_Ip WHERE char_id = ?";
			try (PreparedStatement deleteStatement = con.prepareStatement(deleteSql))
			{
				deleteStatement.setInt(1, charId);
				deleteStatement.executeUpdate();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void executeRoutine()
	{
		System.out.println();
		if (player.isNoBuffProtected() && (player.getAllEffects().length <= 8))
		{
			player.sendMessage("You don't have buffs to use autofarm.");
			player.broadcastUserInfo();
			stop();
			player.setAutoFarm(false);
			AutoFarm.showAutoFarm(player);
			return;
		}
		
		calculatePotions();
		checkSpoil();
		targetEligibleCreature();
		if (player.isMageClass())
		{
			useAppropriateSpell();
		}
		else if (shotcutsContainAttack())
		{
			attack();
		}
		else
		{
			useAppropriateSpell();
		}
		checkSpoil();
		useAppropriateSpell();
	}
	
	private void attack()
	{
		physicalAttack();
	}
	
	private void useAppropriateSpell()
	{
		L2Skill chanceSkill = nextAvailableSkill(getChanceSpells(), AutofarmSpellType.Chance);
		
		if (chanceSkill != null)
		{
			useMagicSkill(chanceSkill, false);
			return;
		}
		
		L2Skill lowLifeSkill = nextAvailableSkill(getLowLifeSpells(), AutofarmSpellType.LowLife);
		
		if (lowLifeSkill != null)
		{
			useMagicSkill(lowLifeSkill, true);
			return;
		}
		
		L2Skill attackSkill = nextAvailableSkill(getAttackSpells(), AutofarmSpellType.Attack);
		
		if (attackSkill != null)
		{
			useMagicSkill(attackSkill, false);
			return;
		}
	}
	
	public L2Skill nextAvailableSkill(List<Integer> skillIds, AutofarmSpellType spellType)
	{
		for (Integer skillId : skillIds)
		{
			L2Skill skill = player.getSkill(skillId);
			
			if (skill == null)
			{
				continue;
			}
			
			if ((skill.getSkillType() == SkillType.SIGNET) || (skill.getSkillType() == SkillType.SIGNET_CASTTIME))
			{
				continue;
			}
			
			if (isSpoil(skillId))
			{
				if (monsterIsAlreadySpoiled())
				{
					continue;
				}
				return skill;
			}
			
			if ((spellType == AutofarmSpellType.Chance) && (getMonsterTarget() != null))
			{
				if (getMonsterTarget().getFirstEffect(skillId) == null)
				{
					return skill;
				}
				continue;
			}
			
			if ((spellType == AutofarmSpellType.LowLife) && (getHpPercentage() > player.getHealPercent()))
			{
				break;
			}
			
			return skill;
		}
		
		return null;
	}
	
	private void checkSpoil()
	{
		if (canBeSweepedByMe() && getMonsterTarget().isDead())
		{
			L2Skill sweeper = player.getSkill(42);
			if (sweeper == null)
			{
				return;
			}
			
			useMagicSkill(sweeper, false);
		}
	}
	
	private Double getHpPercentage()
	{
		return (player.getCurrentHp() * 100.0f) / player.getMaxHp();
	}
	
	private Double percentageMpIsLessThan()
	{
		return (player.getCurrentMp() * 100.0f) / player.getMaxMp();
	}
	
	private Double percentageHpIsLessThan()
	{
		return (player.getCurrentHp() * 100.0f) / player.getMaxHp();
	}
	
	private List<Integer> getAttackSpells()
	{
		return getSpellsInSlots(AutofarmConstants.attackSlots);
	}
	
	private List<Integer> getSpellsInSlots(List<Integer> attackSlots)
	{
		L2ShortCut[] shortCutsArray = player.getAllShortCuts().toArray(new L2ShortCut[0]);
		
		return Arrays.stream(shortCutsArray).filter(shortcut -> (shortcut.getPage() == player.getPage()) && (shortcut.getType() == L2ShortCut.TYPE_SKILL) && attackSlots.contains(shortcut.getSlot())).map(L2ShortCut::getId).collect(Collectors.toList());
	}
	
	private List<Integer> getChanceSpells()
	{
		return getSpellsInSlots(AutofarmConstants.chanceSlots);
	}
	
	private List<Integer> getLowLifeSpells()
	{
		return getSpellsInSlots(AutofarmConstants.lowLifeSlots);
	}
	
	private boolean shotcutsContainAttack()
	{
		L2ShortCut[] shortCutsArray = player.getAllShortCuts().toArray(new L2ShortCut[0]);
		
		return Arrays.stream(shortCutsArray).anyMatch(shortcut -> (shortcut.getPage() == player.getPage()) && (shortcut.getType() == L2ShortCut.TYPE_ACTION) && ((shortcut.getId() == 2) || (player.isSummonAttack() && (shortcut.getId() == 22))));
	}
	
	private boolean monsterIsAlreadySpoiled()
	{
		return (getMonsterTarget() != null) && (getMonsterTarget().getIsSpoiledBy() != 0);
	}
	
	private static boolean isSpoil(Integer skillId)
	{
		return (skillId == 254) || (skillId == 302);
	}
	
	private boolean canBeSweepedByMe()
	{
		return (getMonsterTarget() != null) && getMonsterTarget().isDead() && (getMonsterTarget().getIsSpoiledBy() == player.getObjectId());
	}
	
	private void castSpellWithAppropriateTarget(L2Skill skill, Boolean forceOnSelf)
	{
		if (forceOnSelf)
		{
			L2Object oldTarget = player.getTarget();
			player.setTarget(player);
			player.useMagic(skill, false, false);
			player.setTarget(oldTarget);
			return;
		}
		
		player.useMagic(skill, false, false);
	}
	
	private void physicalAttack()
	{
		if (!(player.getTarget() instanceof L2MonsterInstance))
		{
			return;
		}
		
		L2MonsterInstance target = (L2MonsterInstance) player.getTarget();
		
		if (!player.isMageClass())
		{
			if (target.isAutoAttackable(player) && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				if (GeoEngine.getInstance().canSeeTarget(player, target))
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					player.onActionRequest();
					
					if (player.isSummonAttack() && (player.getPet() != null))
					{
						// Siege Golem's
						if (((player.getPet().getNpcId() >= 14702) && (player.getPet().getNpcId() <= 14798)) || ((player.getPet().getNpcId() >= 14839) && (player.getPet().getNpcId() <= 14869)))
						{
							return;
						}
						
						L2Summon activeSummon = player.getPet();
						activeSummon.setTarget(target);
						activeSummon.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
						
						int[] summonAttackSkills =
						{
							4261,
							4068,
							4137,
							4260,
							4708,
							4709,
							4710,
							4712,
							5135,
							5138,
							5141,
							5442,
							5444,
							6095,
							6096,
							6041,
							6044
						};
						if (Rnd.get(100) < player.getSummonSkillPercent())
						{
							for (int skillId : summonAttackSkills)
							{
								useMagicSkillBySummon(skillId, target);
							}
						}
					}
				}
			}
			else
			{
				if (target.isAutoAttackable(player) && GeoEngine.getInstance().canSeeTarget(player, target))
				{
					if (GeoEngine.getInstance().canSeeTarget(player, target))
					{
						player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
					}
				}
			}
		}
		else
		{
			if (player.isSummonAttack() && (player.getPet() != null))
			{
				// Siege Golem's
				if (((player.getPet().getNpcId() >= 14702) && (player.getPet().getNpcId() <= 14798)) || ((player.getPet().getNpcId() >= 14839) && (player.getPet().getNpcId() <= 14869)))
				{
					return;
				}
				
				L2Summon activeSummon = player.getPet();
				activeSummon.setTarget(target);
				activeSummon.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				
				int[] summonAttackSkills =
				{
					4261,
					4068,
					4137,
					4260,
					4708,
					4709,
					4710,
					4712,
					5135,
					5138,
					5141,
					5442,
					5444,
					6095,
					6096,
					6041,
					6044
				};
				if (Rnd.get(100) < player.getSummonSkillPercent())
				{
					for (int skillId : summonAttackSkills)
					{
						useMagicSkillBySummon(skillId, target);
					}
				}
			}
		}
	}
	
	public void targetEligibleCreature()
	{
		if (player.getTarget() == null || (committedTarget != null && !committedTarget.isDead() && !GeoEngine.getInstance().canSeeTarget(player, committedTarget)))
		{
			selectNewTarget();
			return;
		}
		
		if (committedTarget != null)
		{
			if (!committedTarget.isDead() && GeoEngine.getInstance().canSeeTarget(player, committedTarget))
			{
				attack();
				return;
			}
			else if (!GeoEngine.getInstance().canSeeTarget(player, committedTarget))
			{
				committedTarget = null;
				selectNewTarget();
				return;
			}
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, committedTarget);
			committedTarget = null;
			player.setTarget(null);
		}
		
		if (!(committedTarget instanceof L2Summon))
		{
			
			List<L2MonsterInstance> targets = getKnownMonstersInRadius(player, player.getRadius(), creature -> GeoEngine.getInstance().canSeeTarget(player.getX(), player.getY(), player.getZ(), creature.getX(), creature.getY(), creature.getZ()) && !player.ignoredMonsterContain(creature.getNpcId()) && !creature.isMinion() && !creature.isRaid() && !creature.isDead()
				&& !(creature instanceof L2ChestInstance) && !(player.isAntiKsProtected() && creature.getTarget() != null && creature.getTarget() != player && creature.getTarget() != player.getPet()));
			
			if (targets.isEmpty())
				return;
			
			L2MonsterInstance closestTarget = targets.stream().min((o1, o2) -> Integer.compare((int) Math.sqrt(player.getDistanceSq(o1)), (int) Math.sqrt(player.getDistanceSq(o2)))).get();
			
			committedTarget = closestTarget;
			player.setTarget(closestTarget);
		}
	}
	
	private void selectNewTarget()
	{
		List<L2MonsterInstance> targets = getKnownMonstersInRadius(player, player.getRadius(), creature -> GeoEngine.getInstance().canSeeTarget(player.getX(), player.getY(), player.getZ(), creature.getX(), creature.getY(), creature.getZ()) && !player.ignoredMonsterContain(creature.getNpcId()) && !creature.isMinion() && !creature.isRaid() && !creature.isDead()
			&& !(creature instanceof L2ChestInstance) && !(player.isAntiKsProtected() && creature.getTarget() != null && creature.getTarget() != player && creature.getTarget() != player.getPet()));
		
		if (targets.isEmpty())
			return;
		
		L2MonsterInstance closestTarget = targets.stream().min((o1, o2) -> Integer.compare((int) Math.sqrt(player.getDistanceSq(o1)), (int) Math.sqrt(player.getDistanceSq(o2)))).get();
		
		committedTarget = closestTarget;
		player.setTarget(closestTarget);
	}
	
	public final static List<L2MonsterInstance> getKnownMonstersInRadius(L2PcInstance player, int radius, Function<L2MonsterInstance, Boolean> condition)
	{
		final L2WorldRegion region = player.getWorldRegion();
		if (region == null)
			return Collections.emptyList();
		
		final List<L2MonsterInstance> result = new ArrayList<>();
		
		for (L2WorldRegion reg : region.getSurroundingRegions())
		{
			for (L2Object obj : reg.getVisibleObjects())
			{
				if (!(obj instanceof L2MonsterInstance) || !Util.checkIfInRange(radius, player, obj, true) || !condition.apply((L2MonsterInstance) obj))
					continue;
				
				result.add((L2MonsterInstance) obj);
			}
		}
		
		return result;
	}
	
	public L2MonsterInstance getMonsterTarget()
	{
		if (!(player.getTarget() instanceof L2MonsterInstance))
		{
			return null;
		}
		
		return (L2MonsterInstance) player.getTarget();
	}
	
	private void useMagicSkill(L2Skill skill, Boolean forceOnSelf)
	{
		if ((skill.getSkillType() == SkillType.RECALL) && (player.getKarma() > 0))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (skill.isToggle() && player.isMounted())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isAttackingNow())
		{
			castSpellWithAppropriateTarget(skill, forceOnSelf);
			// player.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST);
		}
		else
		{
			castSpellWithAppropriateTarget(skill, forceOnSelf);
		}
	}
	
	private boolean useMagicSkillBySummon(int skillId, L2Object target)
	{
		// No owner, or owner in shop mode.
		if ((player == null) || player.isInStoreMode())
		{
			return false;
		}
		
		final L2Summon activeSummon = player.getPet();
		if (activeSummon == null)
		{
			return false;
		}
		
		// Pet which is 20 levels higher than owner.
		if (activeSummon instanceof L2PetInstance && activeSummon.getLevel() - player.getLevel() > 20)
		{
			// player.sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
			return false;
		}
		
		// Out of control pet.
		if (activeSummon.isOutOfControl())
		{
			// player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
			return false;
		}
		
		// Verify if the launched skill is mastered by the summon.
		final L2Skill skill = activeSummon.getSkill(skillId);
		if (skill == null)
		{
			return false;
		}
		
		// Can't launch offensive skills on owner.
		if (skill.isOffensive() && (player == target))
		{
			return false;
		}
		
		activeSummon.setTarget(target);
		return activeSummon.useMagic(skill, false, false);
	}
	
	private void calculatePotions()
	{
		if (percentageHpIsLessThan() < player.getHpPotionPercentage())
		{
			forceUseItem(1539);
		}
		if (percentageMpIsLessThan() < player.getMpPotionPercentage())
		{
			forceUseItem(728);
		}
	}
	
	private void forceUseItem(int itemId)
	{
		final L2ItemInstance potion = player.getInventory().getItemByItemId(itemId);
		if (potion == null)
		{
			return;
		}
		final IItemHandler handler = ItemHandler.getInstance().getItemHandler(potion.getItemId());
		if (handler != null)
		{
			handler.useItem(player, potion);
		}
	}
}
