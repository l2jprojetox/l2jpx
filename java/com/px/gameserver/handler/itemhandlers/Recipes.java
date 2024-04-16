package com.px.gameserver.handler.itemhandlers;

import com.px.Config;
import com.px.gameserver.data.xml.RecipeData;
import com.px.gameserver.enums.actors.OperateType;
import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.Recipe;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.RecipeBookItemList;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;

public class Recipes implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (!Config.IS_CRAFTING_ENABLED)
		{
			player.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return;
		}
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			return;
		}
		
		final Recipe recipe = RecipeData.getInstance().getRecipeByItemId(item.getItemId());
		if (recipe == null)
			return;
		
		if (player.getRecipeBook().hasRecipe(recipe.getId()))
		{
			player.sendPacket(SystemMessageId.RECIPE_ALREADY_REGISTERED);
			return;
		}
		
		final boolean isDwarven = recipe.isDwarven();
		if (isDwarven)
		{
			if (!player.hasDwarvenCraft())
				player.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
			else if (player.getOperateType() == OperateType.MANUFACTURE)
				player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			else if (recipe.getLevel() > player.getSkillLevel(L2Skill.SKILL_CREATE_DWARVEN))
				player.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
			else if (player.getRecipeBook().get(isDwarven).size() >= player.getStatus().getDwarfRecipeLimit())
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(player.getStatus().getDwarfRecipeLimit()));
			else if (player.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				player.getRecipeBook().putRecipe(recipe, isDwarven, true);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				player.sendPacket(new RecipeBookItemList(player, isDwarven));
			}
		}
		else
		{
			if (!player.hasCommonCraft())
				player.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
			else if (player.getOperateType() == OperateType.MANUFACTURE)
				player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			else if (recipe.getLevel() > player.getSkillLevel(L2Skill.SKILL_CREATE_COMMON))
				player.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
			else if (player.getRecipeBook().get(isDwarven).size() >= player.getStatus().getCommonRecipeLimit())
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(player.getStatus().getCommonRecipeLimit()));
			else if (player.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				player.getRecipeBook().putRecipe(recipe, isDwarven, true);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				player.sendPacket(new RecipeBookItemList(player, isDwarven));
			}
		}
	}
}