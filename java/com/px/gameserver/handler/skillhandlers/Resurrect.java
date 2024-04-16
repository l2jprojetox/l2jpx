package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.taskmanager.DecayTaskManager;

public class Resurrect implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.RESURRECT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		for (WorldObject cha : targets)
		{
			final Creature target = (Creature) cha;
			if (activeChar instanceof Player)
			{
				if (cha instanceof Player)
					((Player) cha).reviveRequest((Player) activeChar, skill, false);
				else if (cha instanceof Pet)
				{
					if (((Pet) cha).getOwner() == activeChar)
						target.doRevive(Formulas.calcRevivePower(activeChar, skill.getPower()));
					else
						((Pet) cha).getOwner().reviveRequest((Player) activeChar, skill, true);
				}
				else
					target.doRevive(Formulas.calcRevivePower(activeChar, skill.getPower()));
			}
			else
			{
				DecayTaskManager.getInstance().cancel(target);
				target.doRevive(Formulas.calcRevivePower(activeChar, skill.getPower()));
			}
		}
		activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}