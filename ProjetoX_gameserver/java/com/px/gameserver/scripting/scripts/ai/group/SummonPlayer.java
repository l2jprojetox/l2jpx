package com.px.gameserver.scripting.scripts.ai.group;

import com.px.commons.math.MathUtil;
import com.px.commons.random.Rnd;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate.SkillType;
import com.px.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * This script holds Portas/Perums monsters behavior. They got a luck to teleport you near them if they are under attack.
 */
public class SummonPlayer extends L2AttackableAIScript
{
	private static final int PORTA = 20213;
	private static final int PERUM = 20221;
	
	public SummonPlayer()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(PORTA, PERUM);
		addSpellFinishedId(PORTA, PERUM);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!(attacker instanceof Player))
			return null;
		
		if (npc.getScriptValue() == 0)
		{
			final double distance = MathUtil.calculateDistance(npc, attacker, true);
			if (distance > 300)
			{
				if (Rnd.nextBoolean())
				{
					npc.doCast(SkillType.TELEPORT);
					npc.setScriptValue(1);
				}
			}
			else if (distance > 100)
			{
				final Creature mostHated = ((Attackable) npc).getMostHated();
				final int chance = Rnd.get(100);
				
				if ((mostHated == attacker && chance < 50) || chance < 10)
				{
					npc.doCast(SkillType.TELEPORT);
					npc.setScriptValue(1);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, skill);
	}
	
	@Override
	public String onSpellFinished(Npc npc, Player player, L2Skill skill)
	{
		if (skill.getId() == 4161)
		{
			npc.setScriptValue(0);
			
			final Creature mostHated = ((Attackable) npc).getMostHated();
			if (mostHated == player && Rnd.get(100) < 33)
				npc.doCast(SkillType.SHORT_RANGE);
		}
		return super.onSpellFinished(npc, player, skill);
	}
}