package com.l2jpx.gameserver.scripting.script.ai.area;

import com.l2jpx.commons.random.Rnd;
import com.l2jpx.commons.util.ArraysUtil;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.network.NpcStringId;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;
import com.l2jpx.gameserver.skills.L2Skill;

/**
 * TODO To fully review. AI for mobs in Plains of Dion (near Floran Village)
 */
public final class PlainsOfDion extends AttackableAIScript
{
	private static final int[] MONSTERS =
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
	
	private static final NpcStringId[] MONSTERS_ASSIST_MSG =
	{
		NpcStringId.ID_1000392,
		NpcStringId.ID_1000393,
		NpcStringId.ID_1000394
	};
	
	public PlainsOfDion()
	{
		super("ai/area");
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
				if (!obj.getAttack().isAttackingNow() && !obj.isDead() && ArraysUtil.contains(MONSTERS, obj.getNpcId()))
				{
					obj.forceAttack(attacker, 200);
					obj.broadcastNpcSay(Rnd.get(MONSTERS_ASSIST_MSG));
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, attacker, damage, skill);
	}
}