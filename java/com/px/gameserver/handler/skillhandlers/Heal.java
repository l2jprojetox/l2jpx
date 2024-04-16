package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class Heal implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.HEAL,
		SkillType.HEAL_STATIC
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
		if (handler != null)
			handler.useSkill(activeChar, skill, targets, itemInstance);
		
		final double healAmount = Formulas.calcHealAmount(activeChar, skill, sps, bsps);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (!target.canBeHealed())
				continue;
			
			final double amount = target.getStatus().addHp(healAmount * target.getStatus().calcStat(Stats.HEAL_EFFECTIVNESS, 100, null, null) / 100.);
			
			if (target instanceof Player)
			{
				if (activeChar != target)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(activeChar).addNumber((int) amount));
				else
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED).addNumber((int) amount));
			}
		}
		
		if (skill.getSkillType() != SkillType.HEAL_STATIC && !skill.isPotion())
			activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}