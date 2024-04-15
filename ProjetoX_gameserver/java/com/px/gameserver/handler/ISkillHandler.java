package com.px.gameserver.handler;

import com.px.commons.logging.CLogger;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;

public interface ISkillHandler
{
	public static final CLogger LOGGER = new CLogger(ISkillHandler.class.getName());
	
	/**
	 * The worker method called by a {@link Creature} when using a {@link L2Skill}.
	 * @param creature : The Creature who uses that L2Skill.
	 * @param skill : The L2Skill object itself.
	 * @param targets : The eventual targets.
	 */
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets);
	
	/**
	 * @return all known {@link L2SkillType}s.
	 */
	public L2SkillType[] getSkillIds();
}