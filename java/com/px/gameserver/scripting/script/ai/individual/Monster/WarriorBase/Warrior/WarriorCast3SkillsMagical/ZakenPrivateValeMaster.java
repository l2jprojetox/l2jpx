package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class ZakenPrivateValeMaster extends WarriorCast3SkillsMagicalAggressive
{
	public ZakenPrivateValeMaster()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical");
	}
	
	public ZakenPrivateValeMaster(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29024 // vale_master_b
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._weightPoint = npc.getX();
		npc._respawnTime = npc.getY();
		npc._flag = npc.getZ();
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature.getZ() > (npc.getZ() - 100) && creature.getZ() < (npc.getZ() + 100))
		{
			if (!(creature instanceof Playable))
				return;
			
			if (npc.isInMyTerritory())
				npc.getAI().addAttackDesire(creature, 200);
			
			onAttacked(npc, creature, DROP_DIVMOD, null);
			
			makeAttackEvent(npc, creature, 9, true);
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidMute = SkillTable.getInstance().getInfo(4215, 1);
			npc.getAI().addCastDesire(caster, raidMute, 1000000);
			
			return;
		}
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}