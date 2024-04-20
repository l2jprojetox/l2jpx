package com.l2jpx.gameserver.skills.l2skills;

import com.l2jpx.commons.data.StatSet;

import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.enums.skills.ShieldDefense;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;

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
			
			final double damage = Formulas.calcPhysicalSkillDamage(caster, target, this, sDef, isCrit, ss);
			if (damage > 0)
			{
				byte reflect = Formulas.calcSkillReflect(target, this);
				if (hasEffects())
				{
					if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
					{
						caster.stopSkillEffects(getId());
						getEffects(target, caster);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(getId());
						if (Formulas.calcSkillSuccess(caster, target, this, sDef, true))
							getEffects(caster, target, sDef, false);
						else
							caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(this));
					}
				}
				
				double finalDamage = damage * modifier;
				target.reduceCurrentHp(finalDamage, caster, this);
				
				// vengeance reflected damage
				if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0)
					caster.reduceCurrentHp(damage, target, this);
				
				caster.sendDamageMessage(target, (int) finalDamage, false, isCrit, false);
			}
			else
				caster.sendDamageMessage(target, 0, false, false, true);
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