package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.skills.L2Skill;

public class GiveSp implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.GIVE_SP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
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
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}