package net.l2jpx.gameserver.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.xml.RecipeTable;
import net.l2jpx.gameserver.model.Inventory;
import net.l2jpx.gameserver.model.L2ManufactureItem;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.holder.ItemHolder;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;
import net.l2jpx.gameserver.network.serverpackets.RecipeBookItemList;
import net.l2jpx.gameserver.network.serverpackets.RecipeItemMakeInfo;
import net.l2jpx.gameserver.network.serverpackets.RecipeShopItemInfo;
import net.l2jpx.gameserver.network.serverpackets.SetupGauge;
import net.l2jpx.gameserver.network.serverpackets.StatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Stats;
import net.l2jpx.gameserver.templates.Recipe;
import net.l2jpx.gameserver.templates.RecipeMaterial;
import net.l2jpx.gameserver.thread.ThreadPoolManager;
import net.l2jpx.gameserver.util.Util;
import net.l2jpx.util.random.Rnd;

/**
 * @author ReynalDev
 */
public class RecipeController
{
	protected static final Logger LOGGER = Logger.getLogger(RecipeController.class);
	
	private static RecipeController instance;
	protected static final Map<L2PcInstance, RecipeItemMaker> activeMakers = Collections.synchronizedMap(new WeakHashMap<L2PcInstance, RecipeItemMaker>());
	
	public static RecipeController getInstance()
	{
		return instance == null ? instance = new RecipeController() : instance;
	}
	
	public synchronized void requestBookOpen(final L2PcInstance player, final boolean isDwarvenCraft)
	{
		RecipeItemMaker maker = null;
		if (Config.ALT_GAME_CREATION)
		{
			maker = activeMakers.get(player);
		}
		
		if (maker == null)
		{
			RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
			response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
			player.sendPacket(response);
			response = null;
			return;
		}
		
		SystemMessage sm = new SystemMessage(SystemMessageId.YOU_MAY_NOT_ALTER_YOU_RECIPEBOOK_WHILE_ENGAGED_IN_MANUFACTURING);
		player.sendPacket(sm);
		maker = null;
		sm = null;
		
		return;
	}
	
	public synchronized void requestMakeItemAbort(final L2PcInstance player)
	{
		activeMakers.remove(player); // TODO: anything else here?
	}
	
	public synchronized void requestManufactureItem(final L2PcInstance manufacturer, final int recipeListId, final L2PcInstance player)
	{
		Recipe recipeList = getValidRecipeList(player, recipeListId);
		
		if (recipeList == null)
		{
			return;
		}
		
		List<Recipe> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
		List<Recipe> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			commonRecipes = null;
			dwarfRecipes = null;
			return;
		}
		
		RecipeItemMaker maker;
		
		if (Config.ALT_GAME_CREATION && (maker = activeMakers.get(manufacturer)) != null) // check if busy
		{
			player.sendMessage("Manufacturer is busy, please try later.");
			return;
		}
		
		maker = new RecipeItemMaker(manufacturer, recipeList, player);
		if (maker.isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(manufacturer, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
		maker = null;
		recipeList = null;
	}
	
	public synchronized void requestMakeItem(final L2PcInstance player, final int recipeListId)
	{
		if (player.isInDuel())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			return;
		}
		
		Recipe recipeList = getValidRecipeList(player, recipeListId);
		
		if (recipeList == null)
		{
			return;
		}
		
		List<Recipe> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
		List<Recipe> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			dwarfRecipes = null;
			commonRecipes = null;
			return;
		}
		
		RecipeItemMaker maker;
		
		// check if already busy (possible in alt mode only)
		if (Config.ALT_GAME_CREATION && (maker = activeMakers.get(player)) != null)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("You are busy creating ");
			sm.addItemName(recipeList.getProductItemId());
			player.sendPacket(sm);
			return;
		}
		
		maker = new RecipeItemMaker(player, recipeList, player);
		if (maker.isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(player, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
		maker = null;
		recipeList = null;
	}
	
	private class RecipeItemMaker implements Runnable
	{
		protected boolean isValid;
		protected List<ItemHolder> items = null;
		protected final Recipe recipeList;
		protected final L2PcInstance player; // "crafter"
		protected final L2PcInstance target; // "customer"
		protected final L2Skill skill;
		protected final int skillId;
		protected final int skillLevel;
		protected double creationPasses;
		protected double manaRequired;
		protected int price;
		protected int totalItems;
		protected int delay;
		
		public RecipeItemMaker(final L2PcInstance pPlayer, final Recipe pRecipeList, final L2PcInstance pTarget)
		{
			player = pPlayer;
			target = pTarget;
			recipeList = pRecipeList;
			
			isValid = false;
			skillId = recipeList.isDwarvenRecipe() ? L2Skill.SKILL_CREATE_DWARVEN : L2Skill.SKILL_CREATE_COMMON;
			skillLevel = player.getSkillLevel(skillId);
			skill = player.getKnownSkill(skillId);
			
			player.isInCraftMode(true);
			
			if (player.isAlikeDead())
			{
				player.sendMessage("Dead people don't craft.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (target.isAlikeDead())
			{
				target.sendMessage("Dead customers can't use manufacture.");
				target.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (target.isProcessingTransaction())
			{
				target.sendMessage("You are busy.");
				target.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (player.isProcessingTransaction())
			{
				if (player != target)
				{
					target.sendMessage("Manufacturer " + player.getName() + " is busy.");
				}
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// validate recipe list
			if (recipeList == null || recipeList.getMaterials().size() == 0)
			{
				player.sendMessage("No such recipe");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			manaRequired = recipeList.getMpCost();
			
			// validate skill level
			if (recipeList.getLevel() > skillLevel)
			{
				player.sendMessage("Need skill level " + recipeList.getLevel());
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// check that customer can afford to pay for creation services
			if (player != target)
			{
				for (final L2ManufactureItem temp : player.getCreateList().getList())
				{
					if (temp.getRecipeId() == recipeList.getId()) // find recipe for item we want manufactured
					{
						price = temp.getCost();
						if (target.getAdena() < price) // check price
						{
							target.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
							abort();
							return;
						}
						break;
					}
				}
			}
			
			// make temporary items
			if ((items = listItems(false)) == null)
			{
				abort();
				return;
			}
			
			// calculate reference price
			for (final ItemHolder i : items)
			{
				totalItems += i.getItemAmount();
			}
			// initial mana check requires MP as written on recipe
			if (player.getCurrentMp() < manaRequired)
			{
				target.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
				abort();
				return;
			}
			
			// determine number of creation passes needed
			// can "equip" skillLevel items each pass
			creationPasses = totalItems / skillLevel + (totalItems % skillLevel != 0 ? 1 : 0);
			
			if (Config.ALT_GAME_CREATION && creationPasses != 0)
			{
				manaRequired /= creationPasses; // checks to validateMp() will only need portion of mp for one pass
			}
			
			updateMakeInfo(true);
			updateCurMp();
			updateCurLoad();
			
			player.isInCraftMode(false);
			isValid = true;
		}
		
		@Override
		public void run()
		{
			if (!Config.IS_CRAFTING_ENABLED)
			{
				target.sendMessage("Item creation is currently disabled.");
				abort();
				return;
			}
			
			if (player == null || target == null)
			{
				LOGGER.warn("player or target == null (disconnected?), aborting" + target + player);
				abort();
				return;
			}
			
			// "target" is the player who wants to craft
			if (!target.isOnline())
			{
				LOGGER.warn("Target " + target.getName() + " is not online, aborting.");
				abort();
				return;
			}
			
			if (!player.isOnline())
			{
				LOGGER.warn("Player " + player.getName() + " is not online, aborting.");
				abort();
				return;
			}
			
			if (Config.ALT_GAME_CREATION && activeMakers.get(player) == null)
			{
				if (target != player)
				{
					target.sendMessage("Manufacture aborted");
					player.sendMessage("Manufacture aborted");
				}
				else
				{
					player.sendMessage("Item creation aborted");
				}
				
				abort();
				return;
			}
			
			if (Config.ALT_GAME_CREATION && !items.isEmpty())
			{
				
				// check mana
				if (!validateMp())
				{
					return;
				}
				// use some mp
				player.reduceCurrentMp(manaRequired);
				// update craft window mp bar
				updateCurMp();
				
				// grab (equip) some more items with a nice msg to player
				grabSomeItems();
				
				// if still not empty, schedule another pass
				if (!items.isEmpty())
				{
					// divided by RATE_CONSUMABLES_COST to remove craft time increase on higher consumables rates
					delay = (int) (Config.ALT_GAME_CREATION_SPEED * player.getMReuseRate(skill) * GameTimeController.TICKS_PER_SECOND / Config.RATE_CONSUMABLE_COST) * GameTimeController.MILLIS_IN_TICK;
					
					// FIXME: please fix this packet to show crafting animation (somebody)
					MagicSkillUser msk = new MagicSkillUser(player, skillId, skillLevel, delay, 0);
					player.broadcastPacket(msk);
					msk = null;
					
					player.sendPacket(new SetupGauge(0, delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + delay);
				}
				else
				{
					// for alt mode, sleep delay msec before finishing
					player.sendPacket(new SetupGauge(0, delay));
					
					try
					{
						Thread.sleep(delay);
					}
					catch (final InterruptedException e)
					{
						if (Config.ENABLE_ALL_EXCEPTIONS)
						{
							e.printStackTrace();
						}
					}
					finally
					{
						finishCrafting();
					}
				}
			}
			// for old craft mode just finish
			else
			{
				finishCrafting();
			}
		}
		
		private void finishCrafting()
		{
			if (!Config.ALT_GAME_CREATION)
			{
				player.reduceCurrentMp(manaRequired);
			}
			
			// first take adena for manufacture
			if (target != player && price > 0) // customer must pay for services
			{
				// attempt to pay for item
				L2ItemInstance adenatransfer = target.transferItem("PayManufacture", target.getInventory().getAdenaInstance().getObjectId(), price, player.getInventory(), player);
				
				if (adenatransfer == null)
				{
					target.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
					abort();
					return;
				}
				adenatransfer = null;
			}
			
			// this line actually takes materials from inventory
			if ((items = listItems(true)) == null)
			{
				// handle possible cheaters here
				// (they click craft then try to get rid of items in order to get free craft)
			}
			else if (Rnd.get(100) < recipeList.getSuccessRate())
			{
				rewardPlayer(); // and immediately puts created item in its place
				updateMakeInfo(true);
			}
			else
			{
				player.sendMessage("Item(s) failed to create");
				if (target != player)
				{
					target.sendMessage("Item(s) failed to create");
				}
				
				updateMakeInfo(false);
			}
			// update load and mana bar of craft window
			updateCurMp();
			updateCurLoad();
			activeMakers.remove(player);
			player.isInCraftMode(false);
			target.sendPacket(new ItemList(target, false));
		}
		
		private void updateMakeInfo(final boolean success)
		{
			if (target == player)
			{
				target.sendPacket(new RecipeItemMakeInfo(recipeList.getId(), target, success));
			}
			else
			{
				target.sendPacket(new RecipeShopItemInfo(player.getObjectId(), recipeList.getId()));
			}
		}
		
		private void updateCurLoad()
		{
			StatusUpdate su = new StatusUpdate(target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, target.getCurrentLoad());
			target.sendPacket(su);
			su = null;
		}
		
		private void updateCurMp()
		{
			StatusUpdate su = new StatusUpdate(target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
			target.sendPacket(su);
			su = null;
		}
		
		private void grabSomeItems()
		{
			int numItems = skillLevel;
			
			while (numItems > 0 && !items.isEmpty())
			{
				ItemHolder item = items.get(0);
				
				int count = item.getItemAmount();
				
				if (count >= numItems)
				{
					count = numItems;
				}
				
				item.setItemAmount(item.getItemAmount() - count);
				if (item.getItemAmount() <= 0)
				{
					items.remove(0);
				}
				else
				{
					items.set(0, item);
				}
				
				numItems -= count;
				
				if (target == player)
				{
					// you equipped ...
					SystemMessage sm = new SystemMessage(SystemMessageId.EQUIPPED_PLUS_S1_S2);
					sm.addNumber(count);
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
					sm = null;
				}
				else
				{
					target.sendMessage("Manufacturer " + player.getName() + " used " + count + " " + item.getItemTemplate().getName());
				}
				
				item = null;
			}
		}
		
		private boolean validateMp()
		{
			if (player.getCurrentMp() < manaRequired)
			{
				// rest (wait for MP)
				if (Config.ALT_GAME_CREATION)
				{
					player.sendPacket(new SetupGauge(0, delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + delay);
				}
				// no rest - report no mana
				else
				{
					target.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_MP));
					abort();
				}
				return false;
			}
			return true;
		}
		
		private List<ItemHolder> listItems(boolean remove)
		{
			Inventory inv = target.getInventory();
			List<ItemHolder> materials = new ArrayList<>();
			
			for (RecipeMaterial recipe : recipeList.getMaterials())
			{
				int quantity = recipeList.isConsumable() ? (int) (recipe.getQuantity() * Config.RATE_CONSUMABLE_COST) : recipe.getQuantity();
				
				if (quantity > 0)
				{
					L2ItemInstance item = inv.getItemByItemId(recipe.getItemId());
					
					// check materials
					if (item == null || item.getCount() < quantity)
					{
						target.sendMessage("You dont have the right elements for making this item" + (recipeList.isConsumable() && Config.RATE_CONSUMABLE_COST != 1 ? ".\nDue to server rates you need " + Config.RATE_CONSUMABLE_COST + "x more material than listed in recipe" : ""));
						abort();
						return null;
					}
					
					// make new temporary object, just for counting puroses
					
					ItemHolder temp = new ItemHolder(item.getItemId(), quantity);
					materials.add(temp);
				}
			}
			
			if (remove)
			{
				for (ItemHolder tmp : materials)
				{
					target.destroyItemByItemId("Manufacture", tmp.getItemId(), tmp.getItemAmount(), player, true);
				}
			}
			
			return materials;
		}
		
		private void abort()
		{
			updateMakeInfo(false);
			player.isInCraftMode(false);
			activeMakers.remove(player);
		}
		
		private void rewardPlayer()
		{
			final int itemId = recipeList.getProductItemId();
			final int itemCount = recipeList.getProductItemCount();
			
			if(target != player)
			{
				SystemMessage message = new SystemMessage(SystemMessageId.S1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
				message.addString(player.getName());
				message.addItemName(itemId);
				message.addNumber(price);
				target.sendPacket(message);
			}
			
			final L2ItemInstance createdItem = target.getInventory().addItem("Manufacture", itemId, itemCount, target, player);
			
			// inform customer of earned item
			SystemMessage sm = null;
			if (itemCount > 1)
			{
				sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1S);
				sm.addItemName(itemId);
				sm.addNumber(itemCount);
				target.sendPacket(sm);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
				sm.addItemName(itemId);
				target.sendPacket(sm);
			}
			
			if (target != player)
			{
				// inform manufacturer of earned profit
				sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
				sm.addNumber(price);
				player.sendPacket(sm);
			}
			sm = null;
			
			if (Config.ALT_GAME_CREATION)
			{
				final int recipeLevel = recipeList.getLevel();
				int exp = createdItem.getReferencePrice() * itemCount;
				// one variation
				
				// exp -= materialsRefPrice;
				// mat. ref. price is not accurate so other method is better
				
				if (exp < 0)
				{
					exp = 0;
				}
				
				// another variation
				exp /= recipeLevel;
				for (int i = skillLevel; i > recipeLevel; i--)
				{
					exp /= 4;
				}
				
				final int sp = exp / 10;
				
				// Added multiplication of Creation speed with XP/SP gain
				// slower crafting -> more XP, faster crafting -> less XP
				// you can use ALT_GAME_CREATION_XP_RATE/SP to
				// modify XP/SP gained (default = 1)
				
				player.addExpAndSp((int) player.calcStat(Stats.EXPSP_RATE, exp * Config.ALT_GAME_CREATION_XP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null), (int) player.calcStat(Stats.EXPSP_RATE, sp * Config.ALT_GAME_CREATION_SP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null));
			}
			updateMakeInfo(true); // success
		}
	}
	
	private Recipe getValidRecipeList(L2PcInstance player, int id)
	{
		Recipe recipeList = RecipeTable.getInstance().getRecipeById(id);
		
		if (recipeList == null || recipeList.getMaterials().size() == 0)
		{
			player.sendMessage("No recipe for: " + id);
			player.isInCraftMode(false);
			return null;
		}
		
		return recipeList;
	}
}
