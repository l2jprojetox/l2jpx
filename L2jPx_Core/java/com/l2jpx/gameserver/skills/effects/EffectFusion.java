package com.l2jpx.gameserver.skills.effects;

import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectFusion extends AbstractEffect
{
	public int _effect;
	public int _maxEffect;
	
	public EffectFusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
		
		_effect = getSkill().getLevel();
		_maxEffect = SkillTable.getInstance().getMaxLevel(getSkill().getId());
	}
	
	@Override
	public boolean onActionTime()
	{
		return true;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FUSION;
	}
	
	public void increaseEffect()
	{
		if (_effect < _maxEffect)
		{
			_effect++;
			updateBuff();
		}
	}
	
	public void decreaseForce()
	{
		_effect--;
		if (_effect < 1)
			exit();
		else
			updateBuff();
	}
	
	private void updateBuff()
	{
		exit();
		SkillTable.getInstance().getInfo(getSkill().getId(), _effect).getEffects(getEffector(), getEffected());
	}
}