package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;

public class CombatPointHeal implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.COMBATPOINTHEAL
	};
	
	@Override
	public void useSkill(Creature actChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(SkillType.BUFF);
		if (handler != null)
			handler.useSkill(actChar, skill, targets, itemInstance);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Player))
				continue;
			
			final Player target = (Player) obj;
			if (target.isDead() || target.isInvul())
				continue;
			
			double cp = skill.getPower();
			
			if ((target.getStatus().getCp() + cp) >= target.getStatus().getMaxCp())
				cp = target.getStatus().getMaxCp() - target.getStatus().getCp();
			
			target.getStatus().setCp(cp + target.getStatus().getCp());
			
			if (actChar instanceof Player && actChar != target)
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_CP_WILL_BE_RESTORED_BY_S1).addCharName(actChar).addNumber((int) cp));
			else
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addNumber((int) cp));
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}