package com.px.gameserver.handler.skillhandlers;

import com.px.commons.math.MathUtil;
import com.px.commons.util.ArraysUtil;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.items.WeaponType;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.FlyType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.location.Point2D;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.FlyToLocation;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.network.serverpackets.ValidateLocation;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.skills.effects.EffectFear;

public class Pdam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PDAM,
		SkillType.FATAL
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
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
			
			double damage = Formulas.calcPhysicalSkillDamage(activeChar, target, skill, sDef, isCrit, ss);
			
			if (damage > 0)
			{
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
					target.sendDamageMessage(activeChar, (int) damage, false, false, false);
				}
				else
				{
					// Manage cast break of the target (calculating rate, sending message...)
					Formulas.calcCastBreak(target, damage);
					
					// Reduce target HPs.
					target.reduceCurrentHp(damage, activeChar, skill);
					
					// Send damage message.
					activeChar.sendDamageMessage(target, (int) damage, false, false, false);
				}
				
				// Possibility of a lethal strike.
				Formulas.calcLethalHit(activeChar, target, skill);
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
		
		if (skill.getFlyType() == FlyType.CHARGE)
		{
			int heading = activeChar.getHeading();
			
			if (targets.length > 0)
				heading = MathUtil.calculateHeadingFrom(activeChar.getX(), activeChar.getY(), targets[0].getX(), targets[0].getY());
			
			final Point2D chargePoint = MathUtil.getNewLocationByDistanceAndHeading(activeChar.getX(), activeChar.getY(), heading, skill.getFlyRadius());
			
			final Location chargeLoc = GeoEngine.getInstance().getValidLocation(activeChar, chargePoint.getX(), chargePoint.getY(), activeChar.getZ());
			
			activeChar.broadcastPacket(new FlyToLocation(activeChar, chargeLoc.getX(), chargeLoc.getY(), chargeLoc.getZ(), FlyType.CHARGE));
			
			activeChar.setXYZ(chargeLoc.getX(), chargeLoc.getY(), chargeLoc.getZ());
			
			activeChar.broadcastPacket(new ValidateLocation(activeChar));
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