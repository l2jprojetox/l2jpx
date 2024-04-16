package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorHold;

import com.px.commons.random.Rnd;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.skills.L2Skill;

public class AncientEgg extends WarriorHold
{
	public AncientEgg()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorHold");
	}
	
	public AncientEgg(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18344
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) <= 80)
			broadcastScriptEvent(npc, 10016, attacker.getObjectId(), 300);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		// Do nothing
	}
}