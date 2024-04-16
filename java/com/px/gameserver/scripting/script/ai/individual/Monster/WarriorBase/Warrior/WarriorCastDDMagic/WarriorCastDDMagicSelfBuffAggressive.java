package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagic;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastDDMagicSelfBuffAggressive extends WarriorCastDDMagicSelfBuff
{
	public WarriorCastDDMagicSelfBuffAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastDDMagic");
	}
	
	public WarriorCastDDMagicSelfBuffAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22022,
		22026,
		22023,
		22010,
		22029,
		22032,
		22038,
		22039
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
		{
			final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF);
			if (getAbnormalLevel(creature, selfBuff) <= 0)
				npc.getAI().addCastDesire(npc, selfBuff, 1000000);
		}
		
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
}