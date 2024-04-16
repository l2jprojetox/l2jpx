package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class Blow implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.BLOW
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (activeChar.isAlikeDead())
			return;
		
		final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isAlikeDead())
				continue;
			
			if (Formulas.calcBlowRate(activeChar, target, skill))
			{
				// Calculate skill evasion.
				if (Formulas.calcPhysicalSkillEvasion(target, skill))
				{
					if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
					
					if (target instanceof Player)
						target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(activeChar));
					
					continue;
				}
				
				final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(activeChar));
				final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, isCrit);
				final byte reflect = Formulas.calcSkillReflect(target, skill);
				
				if (skill.hasEffects())
				{
					if (reflect == Formulas.SKILL_REFLECT_SUCCEED)
					{
						activeChar.stopSkillEffects(skill.getId());
						
						skill.getEffects(target, activeChar);
					}
					else
					{
						target.stopSkillEffects(skill.getId());
						
						if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, true))
							skill.getEffects(activeChar, target, sDef, false);
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					}
				}
				
				double damage = (int) Formulas.calcBlowDamage(activeChar, target, skill, sDef, ss);
				if (isCrit)
					damage *= 2;
				
				// Skill counter.
				if ((reflect & Formulas.SKILL_COUNTER) != 0)
				{
					if (target instanceof Player)
						target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(activeChar));
					
					if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(target));
					
					// Calculate the counter percent.
					final double counteredPercent = target.getStatus().calcStat(Stats.COUNTER_SKILL_PHYSICAL, 0, target, null) / 100.;
					
					damage *= counteredPercent;
					
					// Reduce caster HPs.
					activeChar.reduceCurrentHp(damage, target, skill);
					
					// Send damage message.
					target.sendDamageMessage(activeChar, (int) damage, false, true, false);
				}
				else
				{
					// Manage cast break of the target (calculating rate, sending message...)
					Formulas.calcCastBreak(target, damage);
					
					// Reduce target HPs.
					target.reduceCurrentHp(damage, activeChar, skill);
					
					// Send damage message.
					activeChar.sendDamageMessage(target, (int) damage, false, true, false);
				}
				
				activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
			}
			
			// Possibility of a lethal strike.
			Formulas.calcLethalHit(activeChar, target, skill);
			
			if (skill.hasSelfEffects())
			{
				final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
				if (effect != null && effect.isSelfEffect())
					effect.exit();
				
				skill.getEffectsSelf(activeChar);
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}