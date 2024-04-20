package com.l2jpx.gameserver.handler;

import com.l2jpx.commons.logging.CLogger;

import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.L2Skill;

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
	 * @return all known {@link SkillType}s.
	 */
	public SkillType[] getSkillIds();
}