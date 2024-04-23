package com.l2jpx.gameserver.handler.skillhandlers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.l2jpx.commons.math.MathUtil;
import com.l2jpx.commons.random.Rnd;

import com.l2jpx.Config;
import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.handler.ISkillHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;

public class Cancel implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.CANCEL,
		SkillType.MAGE_BANE,
		SkillType.WARRIOR_BANE
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		// Delimit min/max % success.
		final int minRate = (skill.getSkillType() == SkillType.CANCEL) ? 25 : 40;
		final int maxRate = (skill.getSkillType() == SkillType.CANCEL) ? 75 : 95;
		
		// Get skill power (which is used as baseRate).
		final double skillPower = skill.getPower();
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = (Creature) obj;
			if (target.isDead())
				continue;
			
			int count = skill.getMaxNegatedEffects();
			
			// Calculate the difference of level between skill level and victim, and retrieve the vuln/prof.
			final int diffLevel = skill.getMagicLevel() - target.getStatus().getLevel();
			final double skillVuln = Formulas.calcSkillVulnerability(activeChar, target, skill, skill.getSkillType());
			
			final List<AbstractEffect> list = Arrays.asList(target.getAllEffects());
			Collections.shuffle(list);
			
			for (AbstractEffect effect : list)
			{
				// Don't cancel toggles or debuffs.
				if (effect.getSkill().isToggle() || effect.getSkill().isDebuff())
					continue;
				
				// Don't cancel specific EffectTypes.
				if (EffectType.isntCancellable(effect.getEffectType()))
					continue;
				
				// Mage && Warrior Bane drop only particular stacktypes.
				switch (skill.getSkillType())
				{
					case MAGE_BANE:
						if ("casting_time_down".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						if ("ma_up".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						continue;
					
					case WARRIOR_BANE:
						if ("attack_time_down".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						if ("speed_up".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						continue;
				}
				
				// Calculate the success chance following previous variables.
				if (calcCancelSuccess(effect.getPeriod(), diffLevel, skillPower, skillVuln, minRate, maxRate))
					effect.exit();
				
				// Remove 1 to the stack of buffs to remove.
				count--;
				
				// If the stack goes to 0, then break the loop.
				if (count == 0)
					break;
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	private static boolean calcCancelSuccess(int effectPeriod, int diffLevel, double baseRate, double vuln, int minRate, int maxRate)
	{
		double rate = (2 * diffLevel + baseRate + effectPeriod / 120) * vuln;
		
		if (Config.DEVELOPER)
			LOGGER.info("calcCancelSuccess(): diffLevel:{}, baseRate:{}, vuln:{}, total:{}.", diffLevel, baseRate, vuln, rate);
		
		return Rnd.get(100) < MathUtil.limit((int) rate, minRate, maxRate);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}