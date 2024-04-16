package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class CpDamPercent implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.CPDAMPERCENT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (activeChar.isAlikeDead())
			return;
		
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Player))
				continue;
			
			final Player target = ((Player) obj);
			if (target.isDead() || target.isInvul())
				continue;
			
			final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, false);
			
			int damage = (int) (target.getStatus().getCp() * (skill.getPower() / 100));
			
			// Manage cast break of the target (calculating rate, sending message...)
			Formulas.calcCastBreak(target, damage);
			
			skill.getEffects(activeChar, target, sDef, bsps);
			activeChar.sendDamageMessage(target, damage, false, false, false);
			target.getStatus().setCp(target.getStatus().getCp() - damage);
			
			// Custom message to see Wrath damage on target
			target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_GAVE_YOU_S2_DMG).addCharName(activeChar).addNumber(damage));
		}
		activeChar.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}