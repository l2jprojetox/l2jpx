package com.px.gameserver.handler;

import com.px.commons.logging.CLogger;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.skills.L2Skill;

public interface ISkillHandler
{
	public static final CLogger LOGGER = new CLogger(ISkillHandler.class.getName());
	
	/**
	 * The worker method called by a {@link Creature} when using a {@link L2Skill}.
	 * @param creature : The Creature who uses that L2Skill.
	 * @param skill : The L2Skill object itself.
	 * @param targets : The eventual targets.
	 * @param item : The eventual item used for skill cast.
	 */
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item);
	
	/**
	 * @return all known {@link SkillType}s.
	 */
	public SkillType[] getSkillIds();
}