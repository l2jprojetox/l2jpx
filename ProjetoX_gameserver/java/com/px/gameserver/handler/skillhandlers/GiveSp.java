package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;

public class GiveSp implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.GIVE_SP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		final int spToAdd = (int) skill.getPower();
		
		for (WorldObject obj : targets)
		{
			final Creature target = (Creature) obj;
			if (target != null)
				target.addExpAndSp(0, spToAdd);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}