package com.l2jpx.gameserver.handler.skillhandlers;

import java.util.List;

import com.l2jpx.gameserver.enums.skills.SkillType;
import com.l2jpx.gameserver.handler.ISkillHandler;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.model.holder.IntIntHolder;
import com.l2jpx.gameserver.skills.L2Skill;

public class Sweep implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SWEEP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Monster))
				continue;
			
			final Monster monster = ((Monster) target);
			
			final List<IntIntHolder> items = monster.getSpoilState();
			if (items.isEmpty())
				continue;
			
			// Reward spoiler, based on sweep items retained on List.
			for (IntIntHolder item : items)
			{
				if (player.isInParty())
					player.getParty().distributeItem(player, item, true, monster);
				else
					player.addItem("Sweep", item.getId(), item.getValue(), player, true);
			}
			
			// Reset variables.
			monster.getSpoilState().clear();
		}
		
		if (skill.hasSelfEffects())
			skill.getEffectsSelf(activeChar);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}