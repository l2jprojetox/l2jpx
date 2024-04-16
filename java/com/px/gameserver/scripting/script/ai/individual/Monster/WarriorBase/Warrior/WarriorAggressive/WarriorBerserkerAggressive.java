package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorAggressive;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.L2Skill;

public class WarriorBerserkerAggressive extends WarriorAggressive
{
	public WarriorBerserkerAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorAggressive");
	}
	
	public WarriorBerserkerAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21269,
		21271,
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onClanDied(Npc caller, Npc called, Creature killer)
	{
		if (caller != called && called._i_ai1 < getNpcIntAIParam(called, "MaxRoarCount"))
		{
			called._i_ai1++;
			
			final L2Skill furyBase = getNpcSkillByType(called, NpcSkillType.FURY);
			final L2Skill fury = SkillTable.getInstance().getInfo(furyBase.getId(), called._i_ai1);
			
			called.getAI().addCastDesire(called, fury, 1000000);
		}
		
		super.onClanDied(caller, called, killer);
	}
}