package com.l2jpx.gameserver.handler.skillhandlers;

import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.handler.ISkillHandler;
import com.l2jpx.gameserver.handler.SkillHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;
import com.l2jpx.gameserver.skills.L2Skill;

public class Heal implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.HEAL,
		SkillType.HEAL_STATIC
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
		if (handler != null)
			handler.useSkill(activeChar, skill, targets);
		
		double power = skill.getPower() + activeChar.getStatus().calcStat(Stats.HEAL_PROFICIENCY, 0, null, null);
		
		if (skill.getSkillType() != SkillType.HEAL_STATIC)
		{
			final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
			final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
			
			double staticShotBonus = 0;
			double mAtkMul = 1.;
			
			if ((sps || bsps) && (activeChar instanceof Player && activeChar.getActingPlayer().isMageClass()) || activeChar instanceof Summon)
			{
				staticShotBonus = skill.getMpConsume(); // static bonus for spiritshots
				
				if (bsps)
				{
					mAtkMul = 4.;
					staticShotBonus *= 2.4;
				}
				else
					mAtkMul = 2.;
			}
			else if ((sps || bsps) && activeChar instanceof Npc)
			{
				staticShotBonus = 2.4 * skill.getMpConsume(); // always blessed spiritshots
				mAtkMul = 4.;
			}
			else
			{
				// shot dynamic bonus
				if (bsps)
					mAtkMul *= 4.;
				else
					mAtkMul += 1.;
			}
			
			power += staticShotBonus + Math.sqrt(mAtkMul * activeChar.getStatus().getMAtk(activeChar, null));
			
			if (!skill.isPotion())
				activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
		}
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (!target.canBeHealed())
				continue;
			
			final double amount = target.getStatus().addHp(power * target.getStatus().calcStat(Stats.HEAL_EFFECTIVNESS, 100, null, null) / 100.);
			
			if (target instanceof Player)
			{
				if (skill.getId() == 4051)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.REJUVENATING_HP));
				else
				{
					if (activeChar != target)
						target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(activeChar).addNumber((int) amount));
					else
						target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED).addNumber((int) amount));
				}
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}