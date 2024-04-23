package com.l2jpx.gameserver.handler.skillhandlers;

import com.l2jpx.commons.util.ArraysUtil;

import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.enums.items.WeaponType;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.enums.skills.ShieldDefense;
import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.handler.ISkillHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.effects.EffectFear;

public class Pdam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PDAM,
		SkillType.FATAL
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (activeChar.isAlikeDead())
			return;
		
		final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
		
		final ItemInstance weapon = activeChar.getActiveWeaponInstance();
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isDead())
				continue;
			
			if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId()))
				continue;
			
			// Calculate skill evasion. As Dodge blocks only melee skills, make an exception with bow weapons.
			if (weapon != null && weapon.getItemType() != WeaponType.BOW && Formulas.calcPhysicalSkillEvasion(target, skill))
			{
				if (activeChar instanceof Player)
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
				
				if (target instanceof Player)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(activeChar));
				
				// no futher calculations needed.
				continue;
			}
			
			final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(activeChar));
			final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, isCrit);
			final byte reflect = Formulas.calcSkillReflect(target, skill);
			
			if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null)
			{
				if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
				{
					activeChar.stopSkillEffects(skill.getId());
					
					skill.getEffects(target, activeChar);
				}
				else
				{
					target.stopSkillEffects(skill.getId());
					
					skill.getEffects(activeChar, target, sDef, false);
				}
			}
			
			final int damage = (int) Formulas.calcPhysicalSkillDamage(activeChar, target, skill, sDef, isCrit, ss);
			if (damage > 0)
			{
				activeChar.sendDamageMessage(target, damage, false, isCrit, false);
				
				// Possibility of a lethal strike
				Formulas.calcLethalHit(activeChar, target, skill);
				
				target.reduceCurrentHp(damage, activeChar, skill);
				
				// vengeance reflected damage
				if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0)
				{
					if (target instanceof Player)
						target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(activeChar));
					
					if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(target));
					
					double vegdamage = (700 * target.getStatus().getPAtk(activeChar) / activeChar.getStatus().getPDef(target));
					activeChar.reduceCurrentHp(vegdamage, target, skill);
				}
			}
			else
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (skill.isSuicideAttack())
			activeChar.doDie(activeChar);
		
		activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}