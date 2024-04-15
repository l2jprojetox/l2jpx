package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.skills.Env;

/**
 * The Class ConditionPlayerActiveSkillId.
 * @author DrHouse
 */
public class ConditionPlayerActiveSkillId extends Condition
{
	private final int _skillId;
	private final int _skillLevel;
	
	/**
	 * Instantiates a new condition player active skill id.
	 * @param skillId the skill id
	 */
	public ConditionPlayerActiveSkillId(int skillId)
	{
		_skillId = skillId;
		_skillLevel = -1;
	}
	
	/**
	 * Instantiates a new condition player active skill id.
	 * @param skillId the skill id
	 * @param skillLevel the skill level
	 */
	public ConditionPlayerActiveSkillId(int skillId, int skillLevel)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		final L2Skill skill = env.getCharacter().getSkill(_skillId);
		return skill != null && _skillLevel <= skill.getLevel();
	}
}