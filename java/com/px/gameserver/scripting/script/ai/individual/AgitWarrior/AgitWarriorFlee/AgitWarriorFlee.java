package com.px.gameserver.scripting.script.ai.individual.AgitWarrior.AgitWarriorFlee;

import com.px.commons.random.Rnd;

import com.px.Config;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.script.ai.individual.AgitWarrior.AgitWarrior;
import com.px.gameserver.skills.L2Skill;

public class AgitWarriorFlee extends AgitWarrior
{
	public AgitWarriorFlee()
	{
		super("ai/individual/AgitWarrior");
	}
	
	public AgitWarriorFlee(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35429,
		35619
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
		{
			npc._i_ai1 = 0;
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai1 == 1)
			npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 10000);
		else
		{
			final Player player = attacker.getActingPlayer();
			if (player != null && (player.getClanId() != npc.getClanId() || player.getClanId() == 0))
			{
				final double hpRatio = npc.getStatus().getHp() / npc.getStatus().getMaxHp();
				if (hpRatio > 0.5)
					npc.getAI().addAttackDesire(attacker, (int) (((((double) damage) / npc.getStatus().getMaxHp()) / 0.05) * (attacker instanceof Player ? 100 : 10)));
				else if (hpRatio > 0.3)
				{
					if (Rnd.get(100) < 90)
						npc.getAI().addAttackDesire(attacker, (int) (((((double) damage) / npc.getStatus().getMaxHp()) / 0.05) * (attacker instanceof Player ? 100 : 10)));
					else
					{
						npc._i_ai1 = 1;
						npc.removeAllAttackDesire();
						
						startQuestTimer("3001", npc, player, 5000);
					}
				}
				else
				{
					npc._i_ai1 = 1;
					npc.removeAllAttackDesire();
				}
			}
		}
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc._i_ai1 == 0)
			super.onSeeCreature(npc, creature);
	}
}