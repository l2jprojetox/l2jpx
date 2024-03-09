package net.l2jpx.gameserver.skills.l2skills;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.skills.effects.EffectSeed;
import net.l2jpx.gameserver.templates.StatsSet;

public class L2SkillSeed extends L2Skill
{
	
	public L2SkillSeed(final StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(final L2Character caster, final L2Object[] targets)
	{
		if (caster.isAlikeDead())
		{
			return;
		}
		
		// Update Seeds Effects
		for (final L2Object target2 : targets)
		{
			final L2Character target = (L2Character) target2;
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
			{
				continue;
			}
			
			final EffectSeed oldEffect = (EffectSeed) target.getFirstEffect(getId());
			if (oldEffect == null)
			{
				getEffects(caster, target, false, false, false);
			}
			else
			{
				oldEffect.increasePower();
			}
			
			final L2Effect[] effects = target.getAllEffects();
			for (final L2Effect effect : effects)
			{
				if (effect.getEffectType() == L2Effect.EffectType.SEED)
				{
					effect.rescheduleEffect();
					/*
					 * for (int j=0;j<effects.length;j++ { if (effects[j].getEffectType()==L2Effect.EffectType.SEED) { EffectSeed e = (EffectSeed)effects[j]; if (e.getInUse() || e.getSkill().getId()==this.getId()) { e.rescheduleEffect(); } } }
					 */
				}
			}
		}
	}
}
