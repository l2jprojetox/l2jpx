package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorAggressive;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.skills.L2Skill;

public class WarriorRunAwayPhysicalSpecialAggressive extends WarriorAggressive
{
	public WarriorRunAwayPhysicalSpecialAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorAggressive");
	}
	
	public WarriorRunAwayPhysicalSpecialAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21513,
		21516,
		21515,
		21508,
		21511,
		21510
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null)
		{
			npc._c_ai0 = mostHated;
			
			final int i0 = getAbnormalLevel(npc, 1201, 1);
			if (i0 >= 0 && npc.distance2D(mostHated) > 40)
			{
				if (npc._i_ai0 == 1)
				{
					npc._i_ai0 = 3;
					
					npc.removeAllDesire();
				}
				
				if (!npc.getAttack().canAttack(mostHated))
					npc.getAI().getAggroList().stopHate(mostHated);
				
				if (attacker instanceof Playable)
				{
					if (damage == 0)
						damage = 1;
					
					npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 100);
				}
			}
		}
		else
			npc._c_ai0 = attacker;
		
		final Location fleeLoc = npc.getSpawn().getFleeLocation();
		if (fleeLoc != null && npc.getStatus().getHpRatio() < 0.5 && npc._i_ai0 == 0 && Rnd.get(100) < 20)
		{
			npc._i_ai0 = 1;
			
			npc.removeAllDesire();
			npc.getAI().addMoveToDesire(fleeLoc, 2000);
			return;
		}
		
		if (attacker instanceof Playable && mostHated == attacker && Rnd.get(100) < 33)
		{
			final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
			
			npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		if (npc._i_ai0 == 1)
		{
			if (npc.getSpawn().getFleeLocation().equals(x, y, z))
			{
				npc.removeAllDesire();
				npc.getAI().addWanderDesire(5, 50);
				
				npc._i_ai0 = 2;
				
				startQuestTimer((Rnd.get(100) < 50) ? "2001" : "2002", npc, null, 1000);
			}
		}
		else if (npc._i_ai0 == 3)
		{
			if (npc.getSpawnLocation().equals(x, y, z))
				npc._i_ai0 = 0;
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc._i_ai0 == 2)
			{
				if (npc._c_ai0 != null)
				{
					npc.getAI().addAttackDesire(npc._c_ai0, 1000);
					broadcastScriptEvent(npc, 10000, npc._c_ai0.getObjectId(), 400);
				}
				npc._i_ai0 = 3;
			}
		}
		else if (name.equalsIgnoreCase("2002"))
		{
			if (npc._i_ai0 == 2)
			{
				if (npc.getStatus().getHpRatio() == 1.0)
					npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 50000);
				else
					startQuestTimer("2002", npc, null, 1000);
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10000)
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				final Creature c0 = (Creature) World.getInstance().getObject(arg1);
				if (c0 != null)
					npc.getAI().addAttackDesire(c0, 1000);
			}
		}
	}
}