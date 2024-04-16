package net.sf.l2j.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderCasting3SkillsMagicalAggressive;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.actors.NpcSkillType;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderWarrior;
import net.sf.l2j.gameserver.skills.L2Skill;

public class PartyLeaderCasting3SkillsMagicalAggressive extends PartyLeaderWarrior
{
	private static final L2Skill DD_MAGIC = SkillTable.getInstance().getInfo(4001, 1);
	private static final L2Skill DEBUFF = SkillTable.getInstance().getInfo(4037, 1);
	
	public PartyLeaderCasting3SkillsMagicalAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior/PartyLeaderCasting3SkillsMagicalAggressive");
	}
	
	public PartyLeaderCasting3SkillsMagicalAggressive(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory())
		{
			if (npc.distance3D(creature) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC1, DD_MAGIC);
					npc.getAI().addCastDesire(creature, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC2, DD_MAGIC);
					npc.getAI().addCastDesire(creature, DDMagic2, 1000000);
				}
			}
			
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.WANDER)
			{
				final L2Skill debuff = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DEBUFF, DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(creature, debuff) <= 0)
					npc.getAI().addCastDesire(creature, debuff, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance3D(attacker) > 100)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && topDesireTarget == attacker)
				{
					if (Rnd.get(100) < 33)
					{
						final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC1, DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
					}
					
					if (Rnd.get(100) < 33)
					{
						final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC2, DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic2, 1000000);
					}
					
					final L2Skill debuff = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DEBUFF, DEBUFF);
					if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
						npc.getAI().addCastDesire(attacker, debuff, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance3D(attacker) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(called, NpcSkillType.DD_MAGIC1, DD_MAGIC);
					called.getAI().addCastDesire(attacker, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(called, NpcSkillType.DD_MAGIC2, DD_MAGIC);
					called.getAI().addCastDesire(attacker, DDMagic2, 1000000);
				}
			}
			
			final L2Skill debuff = getNpcSkillByTypeOrDefault(called, NpcSkillType.DEBUFF, DEBUFF);
			if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
				called.getAI().addCastDesire(attacker, debuff, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}