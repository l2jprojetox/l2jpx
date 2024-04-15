package com.px.gameserver.handler.skillhandlers;

import java.util.List;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Monster;
import com.px.gameserver.model.holder.IntIntHolder;

public class Sweep implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.SWEEP
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
			if (!monster.isSpoiled())
				continue;
			
			final List<IntIntHolder> items = monster.getSweepItems();
			if (items.isEmpty())
				continue;
			
			for (IntIntHolder item : items)
			{
				if (player.isInParty())
					player.getParty().distributeItem(player, item, true, monster);
				else
					player.addItem("Sweep", item.getId(), item.getValue(), player, true);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}