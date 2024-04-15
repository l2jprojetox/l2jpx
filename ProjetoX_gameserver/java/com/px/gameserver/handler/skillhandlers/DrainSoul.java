package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.scripting.QuestState;

public class DrainSoul implements ISkillHandler
{
	private static final String qn = "Q350_EnhanceYourWeapon";
	
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.DRAIN_SOUL
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		// Check player.
		if (activeChar == null || activeChar.isDead() || !(activeChar instanceof Player))
			return;
		
		// Check quest condition.
		final Player player = (Player) activeChar;
		QuestState st = player.getQuestState(qn);
		if (st == null || !st.isStarted())
			return;
		
		// Get target.
		WorldObject target = targets[0];
		if (target == null || !(target instanceof Monster))
			return;
		
		// Check monster.
		final Monster mob = (Monster) target;
		if (mob.isDead())
			return;
		
		// Range condition, cannot be higher than skill's effectRange.
		if (!player.isInsideRadius(mob, skill.getEffectRange(), true, true))
			return;
		
		// Register.
		mob.registerAbsorber(player);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}