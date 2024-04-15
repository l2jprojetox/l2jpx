package com.px.gameserver.handler.skillhandlers;

import com.px.commons.random.Rnd;

import com.px.gameserver.enums.skills.L2SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.L2ExtractableProductItem;
import com.px.gameserver.model.L2ExtractableSkill;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.holder.IntIntHolder;
import com.px.gameserver.network.SystemMessageId;

public class Extractable implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.EXTRACTABLE,
		L2SkillType.EXTRACTABLE_FISH
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final L2ExtractableSkill exItem = skill.getExtractableSkill();
		if (exItem == null || exItem.getProductItemsArray().isEmpty())
		{
			LOGGER.warn("Missing informations for extractable skill id: {}.", skill.getId());
			return;
		}
		
		final Player player = activeChar.getActingPlayer();
		final int chance = Rnd.get(100000);
		
		boolean created = false;
		int chanceIndex = 0;
		
		for (L2ExtractableProductItem expi : exItem.getProductItemsArray())
		{
			chanceIndex += (int) (expi.getChance() * 1000);
			if (chance <= chanceIndex)
			{
				for (IntIntHolder item : expi.getItems())
					player.addItem("Extract", item.getId(), item.getValue(), targets[0], true);
				
				created = true;
				break;
			}
		}
		
		if (!created)
		{
			player.sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
			return;
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}