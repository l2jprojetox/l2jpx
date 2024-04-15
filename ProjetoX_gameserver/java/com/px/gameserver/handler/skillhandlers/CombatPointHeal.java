package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.StatusUpdate;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class CombatPointHeal implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.COMBATPOINTHEAL
	};
	
	@Override
	public void useSkill(Creature actChar, L2Skill skill, WorldObject[] targets)
	{
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(L2SkillType.BUFF);
		if (handler != null)
			handler.useSkill(actChar, skill, targets);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = (Creature) obj;
			if (target.isDead() || target.isInvul())
				continue;
			
			double cp = skill.getPower();
			
			if ((target.getCurrentCp() + cp) >= target.getMaxCp())
				cp = target.getMaxCp() - target.getCurrentCp();
			
			target.setCurrentCp(cp + target.getCurrentCp());
			
			StatusUpdate sump = new StatusUpdate(target);
			sump.addAttribute(StatusUpdate.CUR_CP, (int) target.getCurrentCp());
			target.sendPacket(sump);
			
			if (target instanceof Player)
			{
				if (actChar instanceof Player && actChar != target)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_CP_WILL_BE_RESTORED_BY_S1).addCharName(actChar).addNumber((int) cp));
				else
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addNumber((int) cp));
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}