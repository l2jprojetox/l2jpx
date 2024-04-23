package com.l2jpx.gameserver.scripting.script.ai.area;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.NpcStringId;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;
import com.l2jpx.gameserver.skills.L2Skill;

/**
 * The AI of Breka's Stronghold.<br>
 * <br>
 * The Breka Orc Shaman summons Betrayer Orc Hero, when attacking is player on low HP.
 */
public class BrekaStronghold extends AttackableAIScript
{
	private static final L2Skill SKILL = SkillTable.getInstance().getInfo(4073, 5);
	
	public BrekaStronghold()
	{
		super("ai/area");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20269);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		// Chance to summon is 3%, when attacker's HP is below 20%. Also attacker must be player.
		if (Rnd.get(100) < 3 && attacker.getStatus().getHpRatio() < 0.2 && attacker instanceof Player)
		{
			// Spawn Betrayer Orc Hero and make him cast skill immediately.
			final Npc hero = addSpawn(21260, npc, true, 15000, false);
			hero.getAI().tryToCast(attacker, SKILL);
			hero.forceAttack(attacker, 1000);
			
			startQuestTimer("3002", hero, null, 8000);
		}
		
		return super.onAttack(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equals("3002"))
		{
			npc.broadcastNpcSay(NpcStringId.get(1000434 + Rnd.get(7)));
		}
		
		return super.onTimer(name, npc, player);
	}
}