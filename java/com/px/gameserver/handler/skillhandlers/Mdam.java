package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class Mdam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.MDAM,
		SkillType.DEATHLINK
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
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
			if (target.isDead())
				continue;
			
			final boolean isCrit = Formulas.calcMCrit(activeChar, target, skill);
			final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, false);
			final byte reflect = Formulas.calcSkillReflect(target, skill);
			
			int damage = (int) Formulas.calcMagicDam(activeChar, target, skill, sDef, sps, bsps, isCrit);
			if (damage > 0)
			{
				// Manage cast break of the target (calculating rate, sending message...)
				Formulas.calcCastBreak(target, damage);
				
				activeChar.sendDamageMessage(target, damage, isCrit, false, false);
				target.reduceCurrentHp(damage, activeChar, skill);
				
				if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null)
				{
					if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0) // reflect skill effects
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(target, activeChar);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(skill.getId());
						if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
							skill.getEffects(activeChar, target, sDef, bsps);
						else
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
					}
				}
			}
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
		
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}