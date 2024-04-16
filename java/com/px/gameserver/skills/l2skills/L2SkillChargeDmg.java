package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class L2SkillChargeDmg extends L2Skill
{
	public L2SkillChargeDmg(StatSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		double modifier = 0;
		
		if (caster instanceof Player)
			modifier = 0.8 + 0.2 * (((Player) caster).getCharges() + getNumCharges());
		
		final boolean ss = caster.isChargedShot(ShotType.SOULSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isAlikeDead())
				continue;
			
			// Calculate skill evasion.
			boolean skillIsEvaded = Formulas.calcPhysicalSkillEvasion(target, this);
			if (skillIsEvaded)
			{
				if (caster instanceof Player)
					((Player) caster).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
				
				if (target instanceof Player)
					((Player) target).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(caster));
				
				continue;
			}
			
			final boolean isCrit = getBaseCritRate() > 0 && Formulas.calcCrit(getBaseCritRate() * 10 * Formulas.getSTRBonus(caster));
			final ShieldDefense sDef = Formulas.calcShldUse(caster, target, this, isCrit);
			final byte reflect = Formulas.calcSkillReflect(target, this);
			
			if (hasEffects())
			{
				if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
				{
					caster.stopSkillEffects(getId());
					
					getEffects(target, caster);
				}
				else
				{
					target.stopSkillEffects(getId());
					
					if (Formulas.calcSkillSuccess(caster, target, this, sDef, true))
						getEffects(caster, target, sDef, false);
					else
						caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(this));
				}
			}
			
			double damage = Formulas.calcPhysicalSkillDamage(caster, target, this, sDef, isCrit, ss);
			damage *= modifier;
			
			// Skill counter.
			if ((reflect & Formulas.SKILL_COUNTER) != 0)
			{
				if (target instanceof Player)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(caster));
				
				if (caster instanceof Player)
					caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(target));
				
				// Calculate the counter percent.
				final double counteredPercent = target.getStatus().calcStat(Stats.COUNTER_SKILL_PHYSICAL, 0, target, null) / 100.;
				
				damage *= counteredPercent;
				
				// Reduce caster HPs.
				caster.reduceCurrentHp(damage, target, this);
				
				// Send damage message.
				target.sendDamageMessage(caster, (int) damage, false, false, false);
			}
			else
			{
				// Manage cast break of the target (calculating rate, sending message...)
				Formulas.calcCastBreak(target, damage);
				
				// Reduce target HPs.
				target.reduceCurrentHp(damage, caster, this);
				
				// Send damage message.
				caster.sendDamageMessage(target, (int) damage, false, false, false);
			}
		}
		
		if (hasSelfEffects())
		{
			final AbstractEffect effect = caster.getFirstEffect(getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			getEffectsSelf(caster);
		}
		
		caster.setChargedShot(ShotType.SOULSHOT, isStaticReuse());
	}
}