package com.px.gameserver.scripting.script.ai.individual.Monster;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;

public class MonsterAI extends DefaultNpc
{
	public MonsterAI()
	{
		super("ai");
	}
	
	public MonsterAI(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttackFinished(Npc npc, Creature target)
	{
		if (target instanceof Summon && target.isDead())
		{
			final Player player = target.getActingPlayer();
			if (player != null)
				npc.getAI().addAttackDesire(player, 500);
		}
	}
}