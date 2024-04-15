package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.handler.SkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.model.actor.instance.SiegeFlag;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.StatusUpdate;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class HealPercent implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.HEAL_PERCENT,
		L2SkillType.MANAHEAL_PERCENT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(L2SkillType.BUFF);
		if (handler != null)
			handler.useSkill(activeChar, skill, targets);
		
		boolean hp = false;
		boolean mp = false;
		
		switch (skill.getSkillType())
		{
			case HEAL_PERCENT:
				hp = true;
				break;
			
			case MANAHEAL_PERCENT:
				mp = true;
				break;
		}
		
		StatusUpdate su = null;
		SystemMessage sm;
		double amount = 0;
		boolean full = skill.getPower() == 100.0;
		boolean targetPlayer = false;
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isDead() || target.isInvul())
				continue;
			
			// Doors and flags can't be healed in any way
			if (target instanceof Door || target instanceof SiegeFlag)
				continue;
			
			targetPlayer = target instanceof Player;
			
			// Cursed weapon owner can't heal or be healed
			if (target != activeChar)
			{
				if (activeChar instanceof Player && ((Player) activeChar).isCursedWeaponEquipped())
					continue;
				
				if (targetPlayer && ((Player) target).isCursedWeaponEquipped())
					continue;
			}
			
			if (hp)
			{
				amount = Math.min(((full) ? target.getMaxHp() : target.getMaxHp() * skill.getPower() / 100.0), target.getMaxHp() - target.getCurrentHp());
				target.setCurrentHp(amount + target.getCurrentHp());
			}
			else if (mp)
			{
				amount = Math.min(((full) ? target.getMaxMp() : target.getMaxMp() * skill.getPower() / 100.0), target.getMaxMp() - target.getCurrentMp());
				target.setCurrentMp(amount + target.getCurrentMp());
			}
			
			if (targetPlayer)
			{
				su = new StatusUpdate(target);
				
				if (hp)
				{
					if (activeChar != target)
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_RESTORED_BY_S1).addCharName(activeChar);
					else
						sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);
					
					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su.addAttribute(StatusUpdate.CUR_HP, (int) target.getCurrentHp());
				}
				else if (mp)
				{
					if (activeChar != target)
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1).addCharName(activeChar);
					else
						sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);
					
					sm.addNumber((int) amount);
					target.sendPacket(sm);
					su.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
				}
				
				target.sendPacket(su);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}