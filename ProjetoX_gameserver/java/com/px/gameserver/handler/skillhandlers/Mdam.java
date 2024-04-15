package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;

public class Mdam implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.MDAM,
		L2SkillType.DEATHLINK
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (activeChar.isAlikeDead())
			return;
		
		final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (activeChar instanceof Player && target instanceof Player && ((Player) target).isFakeDeath())
				target.stopFakeDeath(true);
			else if (target.isDead())
				continue;
			
			boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, skill));
			final byte shld = Formulas.calcShldUse(activeChar, target, skill);
			final byte reflect = Formulas.calcSkillReflect(target, skill);
			
			int damage = (int) Formulas.calcMagicDam(activeChar, target, skill, shld, sps, bsps, mcrit);
			if (damage > 0)
			{
				// Manage cast break of the target (calculating rate, sending message...)
				Formulas.calcCastBreak(target, damage);
				
				// vengeance reflected damage
				if ((reflect & Formulas.SKILL_REFLECT_VENGEANCE) != 0)
					activeChar.reduceCurrentHp(damage, target, skill);
				else
				{
					activeChar.sendDamageMessage(target, damage, mcrit, false, false);
					target.reduceCurrentHp(damage, activeChar, skill);
				}
				
				if (skill.hasEffects() && target.getFirstEffect(L2EffectType.BLOCK_DEBUFF) == null)
				{
					if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0) // reflect skill effects
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(target, activeChar);
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(skill));
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(skill.getId());
						if (Formulas.calcSkillSuccess(activeChar, target, skill, shld, bsps))
							skill.getEffects(activeChar, target, new Env(shld, sps, false, bsps));
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
					}
				}
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (skill.isSuicideAttack())
			activeChar.doDie(activeChar);
		
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}