package net.l2jpx.gameserver.handler.itemhandlers;

import net.l2jpx.gameserver.datatables.xml.RecipeTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.Recipe;

/**
 * @author ReynalDev
 */
public class Recipes implements IItemHandler
{
	private final int[] ITEM_IDS;
	
	public Recipes()
	{
		RecipeTable recipeData = RecipeTable.getInstance();
		ITEM_IDS = new int[recipeData.getRecipesCount() + 1];
		
		for (int i = 1; i <= recipeData.getRecipesCount(); i++)
		{
			Recipe recipe = recipeData.getRecipeById(i);
			
			if(recipe == null)
			{
				continue;
			}
			
			ITEM_IDS[i] = recipeData.getRecipeById(i).getRecipeId();
		}
	}
	
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		Recipe recipe = RecipeTable.getInstance().getRecipeByItemId(item.getItemId());
		
		if (activeChar.hasRecipeList(recipe.getId()))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.THAT_RECIPE_IS_ALREADY_REGISTERED);
			activeChar.sendPacket(sm);
		}
		else
		{
			if (recipe.isDwarvenRecipe())
			{
				if (activeChar.hasDwarvenCraft())
				{
					if (recipe.getLevel() > activeChar.getDwarvenCraft())
					{
						// can't add recipe, becouse create item level too low
						SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
						activeChar.sendPacket(sm);
					}
					else if (activeChar.getDwarvenRecipeBook().length >= activeChar.GetDwarfRecipeLimit())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER);
						sm.addNumber(activeChar.GetDwarfRecipeLimit());
						activeChar.sendPacket(sm);
					}
					else
					{
						activeChar.registerDwarvenRecipeList(recipe);
						activeChar.saveRecipeIntoDB(recipe);
						activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED);
						sm.addString(item.getItemName());
						activeChar.sendPacket(sm);
					}
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
					activeChar.sendPacket(sm);
				}
			}
			else
			{
				if (activeChar.hasCommonCraft())
				{
					if (recipe.getLevel() > activeChar.getCommonCraft())
					{
						// can't add recipe, becouse create item level too low
						SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
						activeChar.sendPacket(sm);
					}
					else if (activeChar.getCommonRecipeBook().length >= activeChar.GetCommonRecipeLimit())
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER);
						sm.addNumber(activeChar.GetCommonRecipeLimit());
						activeChar.sendPacket(sm);
					}
					else
					{
						activeChar.registerCommonRecipeList(recipe);
						activeChar.saveRecipeIntoDB(recipe);
						activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
						SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED);
						sm.addString(item.getItemName());
						activeChar.sendPacket(sm);
					}
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
					activeChar.sendPacket(sm);
				}
			}
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
