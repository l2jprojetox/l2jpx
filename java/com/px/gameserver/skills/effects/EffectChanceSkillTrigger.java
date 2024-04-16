package com.px.gameserver.skills.effects;

import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.ChanceCondition;
import com.px.gameserver.skills.IChanceSkillTrigger;
import com.px.gameserver.skills.L2Skill;

public class EffectChanceSkillTrigger extends AbstractEffect implements IChanceSkillTrigger
{
	private final int _triggeredId;
	private final int _triggeredLevel;
	private final ChanceCondition _chanceCondition;
	
	public EffectChanceSkillTrigger(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
		
		_triggeredId = template.getTriggeredId();
		_triggeredLevel = template.getTriggeredLevel();
		_chanceCondition = template.getChanceCondition();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CHANCE_SKILL_TRIGGER;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().addChanceTrigger(this);
		return super.onStart();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().removeChanceEffect(this);
		super.onExit();
	}
	
	@Override
	public int getTriggeredChanceId()
	{
		return _triggeredId;
	}
	
	@Override
	public int getTriggeredChanceLevel()
	{
		return _triggeredLevel;
	}
	
	@Override
	public boolean triggersChanceSkill()
	{
		return _triggeredId > 1;
	}
	
	@Override
	public ChanceCondition getTriggeredChanceCondition()
	{
		return _chanceCondition;
	}
}