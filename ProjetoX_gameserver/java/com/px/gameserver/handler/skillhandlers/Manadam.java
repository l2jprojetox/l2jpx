package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.StatusUpdate;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Env;
import com.px.gameserver.skills.Formulas;

public class Manadam implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.MANADAM
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
					byte shld = Formulas.calcShldUse(activeChar, target, skill);
					target.stopSkillEffects(skill.getId());
					
					if (Formulas.calcSkillSuccess(activeChar, target, skill, shld, bsps))
						skill.getEffects(activeChar, target, new Env(shld, sps, false, bsps));
					else
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
				}
				
				double damage = Formulas.calcManaDam(activeChar, target, skill, sps, bsps);
				
				double mp = (damage > target.getCurrentMp() ? target.getCurrentMp() : damage);
				target.reduceCurrentMp(mp);
				if (damage > 0)
					target.stopEffectsOnDamage(true);
				
				if (target instanceof Player)
				{
					StatusUpdate sump = new StatusUpdate(target);
					sump.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
					target.sendPacket(sump);
					
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_DRAINED_BY_S1).addCharName(activeChar).addNumber((int) mp));
				}
				
				if (activeChar instanceof Player)
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1).addNumber((int) mp));
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}