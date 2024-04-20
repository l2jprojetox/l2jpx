package com.l2jpx.gameserver.skills.effects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.l2jpx.commons.math.MathUtil;
import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;

public class EffectCancel extends AbstractEffect
{
	public EffectCancel(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isDead())
			return false;
		
		final int cancelLvl = getSkill().getMagicLevel();
		int count = getSkill().getMaxNegatedEffects();
		
		double rate = getTemplate().getEffectPower();
		
		// Resistance/vulnerability
		final double res = Formulas.calcSkillVulnerability(getEffector(), getEffected(), getSkill(), getTemplate().getEffectType());
		rate *= res;
		
		final List<AbstractEffect> list = Arrays.asList(getEffected().getAllEffects());
		Collections.shuffle(list);
		
		for (AbstractEffect effect : list)
		{
			// Don't cancel toggles or debuffs.
			if (effect.getSkill().isToggle() || effect.getSkill().isDebuff())
				continue;
			
			// Don't cancel specific EffectTypes.
			if (EffectType.isntCancellable(getEffectType()))
				continue;
			
			// Calculate the success chance following previous variables.
			if (calcCancelSuccess(effect, cancelLvl, (int) rate))
				effect.exit();
			
			// Remove 1 to the stack of buffs to remove.
			count--;
			
			// If the stack goes to 0, then break the loop.
			if (count == 0)
				break;
		}
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	private static boolean calcCancelSuccess(AbstractEffect effect, int cancelLvl, int baseRate)
	{
		int rate = 2 * (cancelLvl - effect.getSkill().getMagicLevel());
		rate += effect.getPeriod() / 120;
		rate += baseRate;
		
		return Rnd.get(100) < MathUtil.limit(rate, 25, 75);
	}
}