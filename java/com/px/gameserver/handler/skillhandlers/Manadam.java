package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
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

public class Manadam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.MANADAM
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
			
			Creature target = ((Creature) obj);
			if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
				target = activeChar;
			
			boolean acted = Formulas.calcMagicAffected(activeChar, target, skill);
			if (target.isInvul() || !acted)
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MISSED_TARGET));
			else
			{
				if (skill.hasEffects())
				{
					target.stopSkillEffects(skill.getId());
					
					final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, false);
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						skill.getEffects(activeChar, target, sDef, bsps);
					else
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
				}
				
				double damage = Formulas.calcManaDam(activeChar, target, skill, sps, bsps);
				
				double mp = (damage > target.getStatus().getMp() ? target.getStatus().getMp() : damage);
				target.getStatus().reduceMp(mp);
				if (damage > 0)
				{
					target.stopEffects(EffectType.SLEEP);
					target.stopEffects(EffectType.IMMOBILE_UNTIL_ATTACKED);
				}
				
				if (target instanceof Player)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_DRAINED_BY_S1).addCharName(activeChar).addNumber((int) mp));
				
				if (activeChar instanceof Player)
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1).addNumber((int) mp));
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}