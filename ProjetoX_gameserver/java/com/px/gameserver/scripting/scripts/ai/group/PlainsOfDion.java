package com.px.gameserver.scripting.scripts.ai.group;

import com.px.commons.random.Rnd;
import com.px.commons.util.ArraysUtil;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * AI for mobs in Plains of Dion (near Floran Village)
 */
public final class PlainsOfDion extends L2AttackableAIScript
{
	private static final int MONSTERS[] =
	{
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	private static final String[] MONSTERS_MSG =
	{
		"$s1! How dare you interrupt our fight! Hey guys, help!",
		"$s1! Hey! We're having a duel here!",
		"The duel is over! Attack!",
		"Foul! Kill the coward!",
		"How dare you interrupt a sacred duel! You must be taught a lesson!"
	};
	
	private static final String[] MONSTERS_ASSIST_MSG =
	{
		"Die, you coward!",
		"Kill the coward!",
		"What are you looking at?"
	};
	
	public PlainsOfDion()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(MONSTERS);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isScriptValue(0))
		{
			npc.broadcastNpcSay(Rnd.get(MONSTERS_MSG).replace("$s1", attacker.getName()));
			
			for (Monster obj : npc.getKnownTypeInRadius(Monster.class, 300))
			{
				if (!obj.isAttackingNow() && !obj.isDead() && ArraysUtil.contains(MONSTERS, obj.getNpcId()))
				{
					attack(obj, attacker);
					obj.broadcastNpcSay(Rnd.get(MONSTERS_ASSIST_MSG));
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, attacker, damage, skill);
	}
}