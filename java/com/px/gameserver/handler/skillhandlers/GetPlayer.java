package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.skills.L2Skill;

public class GetPlayer implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.GET_PLAYER
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (activeChar.isAlikeDead())
			return;
		
		for (WorldObject target : targets)
		{
			final Player victim = target.getActingPlayer();
			if (victim == null || victim.isAlikeDead())
				continue;
			
			victim.instantTeleportTo(activeChar.getPosition(), 0);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}