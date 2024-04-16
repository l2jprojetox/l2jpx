package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.skills.L2Skill;

public class WarriorBomb extends Warrior
{
	public WarriorBomb()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorBomb(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21666,
		21689,
		21712,
		21735,
		21758,
		21781
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.distance2D(attacker) < 200)
		{
			L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
			npc.getAI().addCastDesire(npc, selfRangeDDMagic, 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Player player, L2Skill skill, boolean success)
	{
		L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
		
		if (skill != null && selfRangeDDMagic != null)
		{
			if (selfRangeDDMagic.getId() == skill.getId())
			{
				if (success)
				{
					npc.doDie(npc);
				}
				npc.getAI().addCastDesire(npc, selfRangeDDMagic, 1000000);
			}
		}
		super.onUseSkillFinished(npc, player, skill, success);
	}
}