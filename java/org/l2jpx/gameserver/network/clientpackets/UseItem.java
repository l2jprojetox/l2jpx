/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.network.clientpackets;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.ai.CtrlEvent;
import org.l2jpx.gameserver.ai.CtrlIntention;
import org.l2jpx.gameserver.ai.NextAction;
import org.l2jpx.gameserver.data.xml.EnchantItemGroupsData;
import org.l2jpx.gameserver.enums.IllegalActionPunishmentType;
import org.l2jpx.gameserver.enums.ItemSkillType;
import org.l2jpx.gameserver.handler.AdminCommandHandler;
import org.l2jpx.gameserver.handler.IItemHandler;
import org.l2jpx.gameserver.handler.ItemHandler;
import org.l2jpx.gameserver.instancemanager.FortSiegeManager;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.effects.EffectType;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.item.OnItemUse;
import org.l2jpx.gameserver.model.holders.ItemSkillHolder;
import org.l2jpx.gameserver.model.item.EtcItem;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.ActionType;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jpx.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

public class UseItem extends ClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;
	private int _itemId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_ctrlPressed = readInt() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Flood protect UseItem
		if (!getClient().getFloodProtectors().canUseItem())
		{
			return;
		}
		
		if (player.isInsideZone(ZoneId.JAIL))
		{
			player.sendMessage("You cannot use items while jailed.");
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_PICK_UP_OR_USE_ITEMS_WHILE_TRADING);
			return;
		}
		
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			// GM can use other player item
			if (player.isGM())
			{
				final WorldObject obj = World.getInstance().findObject(_objectId);
				if ((obj != null) && obj.isItem())
				{
					AdminCommandHandler.getInstance().useAdminCommand(player, "admin_use_item " + _objectId, true);
				}
			}
			return;
		}
		
		if (item.isQuestItem() && (item.getTemplate().getDefaultAction() != ActionType.NONE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		// No UseItem is allowed while the player is in special conditions
		if (player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead())
		{
			return;
		}
		
		// Char cannot use item when dead
		if (player.isDead() || !player.getInventory().canManipulateWithItemId(item.getId()))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addItemName(item);
			player.sendPacket(sm);
			return;
		}
		
		if (!item.isEquipped() && !item.getTemplate().checkCondition(player, player, true))
		{
			return;
		}
		
		_itemId = item.getId();
		if (player.isFishing() && ((_itemId < 6535) || (_itemId > 6540)))
		{
			// You cannot do anything else while fishing
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_3);
			return;
		}
		
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (player.getReputation() < 0))
		{
			final List<ItemSkillHolder> skills = item.getTemplate().getSkills(ItemSkillType.NORMAL);
			if (skills != null)
			{
				for (ItemSkillHolder holder : skills)
				{
					if (holder.getSkill().hasEffectType(EffectType.TELEPORT))
					{
						return;
					}
				}
			}
		}
		
		// If the item has reuse time and it has not passed.
		// Message from reuse delay must come from item.
		final int reuseDelay = item.getReuseDelay();
		final int sharedReuseGroup = item.getSharedReuseGroup();
		if (reuseDelay > 0)
		{
			final long reuse = player.getItemRemainingReuseTime(item.getObjectId());
			if (reuse > 0)
			{
				reuseData(player, item, reuse);
				sendSharedGroupUpdate(player, sharedReuseGroup, reuse, reuseDelay);
				return;
			}
			
			final long reuseOnGroup = player.getReuseDelayOnGroup(sharedReuseGroup);
			if (reuseOnGroup > 0)
			{
				reuseData(player, item, reuseOnGroup);
				sendSharedGroupUpdate(player, sharedReuseGroup, reuseOnGroup, reuseDelay);
				return;
			}
		}
		
		player.onActionRequest();
		
		if (item.isEquipable())
		{
			// Max equipable item grade configuration.
			final int itemCrystalLevel = item.getTemplate().getCrystalType().getLevel();
			if (!player.isGM() && (itemCrystalLevel > Config.MAX_EQUIPABLE_ITEM_GRADE.getLevel()) && (itemCrystalLevel < CrystalType.EVENT.getLevel()))
			{
				return;
			}
			
			// Don't allow to put formal wear while a cursed weapon is equipped.
			if (player.isCursedWeaponEquipped() && (_itemId == 6408))
			{
				return;
			}
			
			// Equip or unEquip
			if (FortSiegeManager.getInstance().isCombat(_itemId))
			{
				return; // no message
			}
			
			if (player.isCombatFlagEquipped())
			{
				return;
			}
			
			if (player.getInventory().isItemSlotBlocked(item.getTemplate().getBodyPart()))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
				return;
			}
			
			switch (item.getTemplate().getBodyPart())
			{
				case ItemTemplate.SLOT_LR_HAND:
				case ItemTemplate.SLOT_L_HAND:
				case ItemTemplate.SLOT_R_HAND:
				{
					// Prevent players to equip weapon while wearing combat flag
					if ((player.getActiveWeaponItem() != null) && (player.getActiveWeaponItem().getId() == 9819))
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
						return;
					}
					
					if (player.isMounted() || player.isDisarmed())
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
						return;
					}
					
					// Don't allow weapon/shield equipment if a cursed weapon is equipped.
					if (player.isCursedWeaponEquipped())
					{
						return;
					}
					break;
				}
				case ItemTemplate.SLOT_DECO:
				{
					if (!item.isEquipped() && (player.getInventory().getTalismanSlots() == 0))
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
						return;
					}
					break;
				}
				case ItemTemplate.SLOT_BROOCH_JEWEL:
				{
					if (!item.isEquipped() && (player.getInventory().getBroochJewelSlots() == 0))
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_CANNOT_EQUIP_S1_WITHOUT_EQUIPPING_A_BROOCH);
						sm.addItemName(item);
						player.sendPacket(sm);
						return;
					}
					break;
				}
			}
			
			// Over-enchant protection.
			if (Config.OVER_ENCHANT_PROTECTION && !player.isGM() //
				&& ((item.isWeapon() && (item.getEnchantLevel() > EnchantItemGroupsData.getInstance().getMaxWeaponEnchant())) //
					|| ((item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY) && (item.getEnchantLevel() > EnchantItemGroupsData.getInstance().getMaxAccessoryEnchant())) //
					|| (item.isArmor() && (item.getTemplate().getType2() != ItemTemplate.TYPE2_ACCESSORY) && (item.getEnchantLevel() > EnchantItemGroupsData.getInstance().getMaxArmorEnchant()))))
			{
				PacketLogger.info("Over-enchanted (+" + item.getEnchantLevel() + ") " + item + " has been removed from " + player);
				player.getInventory().destroyItem("Over-enchant protection", item, player, null);
				if (Config.OVER_ENCHANT_PUNISHMENT != IllegalActionPunishmentType.NONE)
				{
					player.sendMessage("[Server]: You have over-enchanted items!");
					player.sendMessage("[Server]: Respect our server rules.");
					player.sendPacket(new ExShowScreenMessage("You have over-enchanted items!", 6000));
					Util.handleIllegalPlayerAction(player, player.getName() + " has over-enchanted items.", Config.OVER_ENCHANT_PUNISHMENT);
				}
				return;
			}
			
			if (player.isCastingNow())
			{
				// Create and Bind the next action to the AI.
				player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> player.useEquippableItem(item, true)));
			}
			else // Equip or unEquip.
			{
				final long currentTime = System.nanoTime();
				final long attackEndTime = player.getAttackEndTime();
				if (attackEndTime > currentTime)
				{
					ThreadPool.schedule(() -> player.useEquippableItem(item, false), TimeUnit.NANOSECONDS.toMillis(attackEndTime - currentTime));
				}
				else
				{
					player.useEquippableItem(item, true);
				}
			}
		}
		else
		{
			final EtcItem etcItem = item.getEtcItem();
			final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
			if (handler == null)
			{
				if ((etcItem != null) && (etcItem.getHandlerName() != null))
				{
					PacketLogger.warning("Unmanaged Item handler: " + etcItem.getHandlerName() + " for Item Id: " + _itemId + "!");
				}
			}
			else if (handler.useItem(player, item, _ctrlPressed))
			{
				// Item reuse time should be added if the item is successfully used.
				// Skill reuse delay is done at handlers.itemhandlers.ItemSkillsTemplate;
				if (reuseDelay > 0)
				{
					player.addTimeStampItem(item, reuseDelay);
					sendSharedGroupUpdate(player, sharedReuseGroup, reuseDelay, reuseDelay);
				}
				
				// Notify events.
				if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_USE, item.getTemplate()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnItemUse(player, item), item.getTemplate());
				}
			}
		}
	}
	
	private void reuseData(Player player, Item item, long remainingTime)
	{
		final int hours = (int) (remainingTime / 3600000);
		final int minutes = (int) (remainingTime % 3600000) / 60000;
		final int seconds = (int) ((remainingTime / 1000) % 60);
		final SystemMessage sm;
		if (hours > 0)
		{
			sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
			sm.addItemName(item);
			sm.addInt(hours);
			sm.addInt(minutes);
		}
		else if (minutes > 0)
		{
			sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
			sm.addItemName(item);
			sm.addInt(minutes);
		}
		else
		{
			sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
			sm.addItemName(item);
		}
		sm.addInt(seconds);
		player.sendPacket(sm);
	}
	
	private void sendSharedGroupUpdate(Player player, int group, long remaining, int reuse)
	{
		if (group > 0)
		{
			player.sendPacket(new ExUseSharedGroupItem(_itemId, group, remaining, reuse));
		}
	}
}
