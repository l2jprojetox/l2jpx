package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.RecipeBookItemList;
import com.px.gameserver.skills.L2Skill;

public class Craft implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.COMMON_CRAFT,
		SkillType.DWARVEN_CRAFT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		if (player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING);
			return;
		}
		
		player.sendPacket(new RecipeBookItemList(player, skill.getSkillType() == SkillType.DWARVEN_CRAFT));
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}