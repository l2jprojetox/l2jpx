package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingEnchant;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.NpcSkillType;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.skills.L2Skill;

public class WarriorCastingEnchantClan extends WarriorCastingEnchant
{
	public WarriorCastingEnchantClan()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingEnchant");
	}
	
	public WarriorCastingEnchantClan(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21616,
		21634,
		20081,
		20794,
		20840,
		21614,
		21615,
		20846,
		21632,
		21633,
		21010,
		20643,
		20593,
		20682
	};
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage, L2Skill skill)
	{
		if (((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK) && called._i_ai1 == 0)
		{
			L2Skill buff = getNpcSkillByType(called, NpcSkillType.BUFF);
			if (Rnd.get(100) < 50 && getAbnormalLevel(caller, buff) <= 0 && ((called.getStatus().getHp() / called.getStatus().getMaxHp()) * 100) > 50)
			{
				called.getAI().addCastDesire(caller, buff, 1000000);
			}
		}
		called._i_ai1 = 1;
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
