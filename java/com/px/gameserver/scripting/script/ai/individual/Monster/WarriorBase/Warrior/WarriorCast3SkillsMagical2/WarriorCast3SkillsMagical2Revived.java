package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical2;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical2Revived extends WarriorCast3SkillsMagical2
{
	public WarriorCast3SkillsMagical2Revived()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical2");
	}
	
	public WarriorCast3SkillsMagical2Revived(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20860,
		21071
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc._param1 == 1000)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(npc._param2);
			if (c0 != null)
			{
				final L2Skill npcHate = SkillTable.getInstance().getInfo(4663, 1);
				
				npc.getAI().addCastDesire(c0, npcHate, 10000);
				npc.getAI().addAttackDesire(c0, 500);
			}
		}
		
		super.onCreated(npc);
	}
}