package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.actors.ClassId;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.skills.L2Skill;

// Used for Frintezza raid only

public class WarriorCast2SkillsApproachAggressive extends Warrior
{
	private static final int DEWDROP_OF_DESTRUCTION = 8556;
	
	public WarriorCast2SkillsApproachAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorCast2SkillsApproachAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18329,
		18331,
		18332,
		18335,
		18336,
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		npc._i_ai3 = 0;
		npc._i_ai4 = 0;
		
		startQuestTimer("1009", npc, null, 40000);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
		if (selfBuff != null)
			npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		
		if (!(creature instanceof Playable))
			return;
		
		if (npc.isInMyTerritory())
			npc.getAI().addAttackDesire(creature, 200);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (Rnd.get(100) < 20 && ClassId.isInGroup(caster, "@cleric_group"))
		{
			npc.removeAllDesire();
			
			npc.getAI().addAttackDesire(caster, 200000);
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (arg1 == 10037 && npc.getNpcId() == 18329)
			npc._i_ai4++;
		else if (arg1 == 10038 && npc.getNpcId() == 18331)
			npc._i_ai3++;
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			called.getAI().addAttackDesire(attacker, ((1.0 * damage) / (called.getStatus().getLevel() + 7)) * 1000);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1008"))
		{
			if (npc._c_ai1 != null)
				broadcastScriptEventEx(npc, 0, 10043, npc._c_ai1.getObjectId(), 8000);
		}
		else if (name.equalsIgnoreCase("1009"))
		{
			if (npc._c_ai1 != null)
				broadcastScriptEventEx(npc, 0, 10042, npc._c_ai1.getObjectId(), 8000);
			
			startQuestTimer("1008", npc, null, 60000);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33)
		{
			final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
			if (physicalSpecial != null)
				npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
		}
		
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null)
			npc._c_ai1 = topDesireTarget;
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc._i_ai4 == 9)
			((Monster) npc).dropItem(new IntIntHolder(DEWDROP_OF_DESTRUCTION, 1));
		else if (npc.getNpcId() == 18329)
			broadcastScriptEventEx(npc, 0, 10037, 0, 5000);
		
		if (npc._i_ai3 == 9)
			((Monster) npc).dropItem(new IntIntHolder(DEWDROP_OF_DESTRUCTION, 1));
		else if (npc.getNpcId() == 18331)
			broadcastScriptEventEx(npc, 0, 10038, 0, 5000);
	}
}