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

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.enums.PlayerCondOverride;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.SkillUseHolder;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.ActionType;
import org.l2jpx.gameserver.model.item.type.EtcItemType;
import org.l2jpx.gameserver.model.itemcontainer.Inventory;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.skill.SkillCaster;
import org.l2jpx.gameserver.model.zone.ZoneId;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.util.GMAudit;
import org.l2jpx.gameserver.util.Util;

/**
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/02 21:25:21 $
 */
public class RequestDropItem extends ClientPacket
{
	private int _objectId;
	private long _count;
	private int _x;
	private int _y;
	private int _z;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_count = readLong();
		_x = readInt();
		_y = readInt();
		_z = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || player.isDead())
		{
			return;
		}
		
		// Flood protect drop to avoid packet lag
		if (!getClient().getFloodProtectors().canDropItem())
		{
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if ((item == null) || (_count == 0) || !player.validateItemManipulation(_objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !player.canOverrideCond(PlayerCondOverride.DROP_ALL_ITEMS)) || (!item.isDropable() && !(player.canOverrideCond(PlayerCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS)) || ((item.getItemType() == EtcItemType.PET_COLLAR) && player.havePetInvItems()) || player.isInsideZone(ZoneId.NO_ITEM_DROP))
		{
			player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED);
			return;
		}
		
		if (item.isQuestItem() && !(player.canOverrideCond(PlayerCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS))
		{
			return;
		}
		
		if (_count > item.getCount())
		{
			player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED);
			return;
		}
		
		if ((Config.PLAYER_SPAWN_PROTECTION > 0) && player.isInvul() && !player.isGM())
		{
			player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED);
			return;
		}
		
		if (_count < 0)
		{
			Util.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop item with oid " + _objectId + " but has count < 0!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!item.isStackable() && (_count > 1))
		{
			Util.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop non-stackable item with oid " + _objectId + " but has count > 1!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (Config.JAIL_DISABLE_TRANSACTION && player.isJailed())
		{
			player.sendMessage("You cannot drop items in Jail.");
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		if (player.isFishing())
		{
			// You can't mount, dismount, break and drop items while fishing
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}
		
		if (player.isFlying())
		{
			return;
		}
		
		if (player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
			return;
		}
		
		// Cannot discard item that the skill is consuming.
		if (player.isCastingNow(s -> (s.getSkill().getItemConsumeId() == item.getId()) && (item.getTemplate().getDefaultAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS)))
		{
			player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED);
			return;
		}
		
		if ((ItemTemplate.TYPE2_QUEST == item.getTemplate().getType2()) && !player.canOverrideCond(PlayerCondOverride.DROP_ALL_ITEMS))
		{
			player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED);
			return;
		}
		
		if (!player.isInsideRadius2D(_x, _y, 0, 150) || (Math.abs(_z - player.getZ()) > 50))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
			return;
		}
		
		if (!player.getInventory().canManipulateWithItemId(item.getId()))
		{
			player.sendMessage("You cannot use this item.");
			return;
		}
		
		// Do not drop items when casting known skills to avoid exploits.
		if (player.isCastingNow())
		{
			for (SkillCaster skillCaster : player.getSkillCasters())
			{
				final Skill skill = skillCaster.getSkill();
				if ((skill != null) && (player.getKnownSkill(skill.getId()) != null))
				{
					player.sendMessage("You cannot drop an item while casting " + skill.getName() + ".");
					return;
				}
			}
		}
		final SkillUseHolder skill = player.getQueuedSkill();
		if ((skill != null) && (player.getKnownSkill(skill.getSkillId()) != null))
		{
			player.sendMessage("You cannot drop an item while casting " + skill.getSkill().getName() + ".");
			return;
		}
		
		if (item.isEquipped())
		{
			player.getInventory().unEquipItemInSlot(item.getLocationSlot());
			player.broadcastUserInfo();
			player.sendItemList(true);
		}
		
		final Item dropedItem = player.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false, false);
		
		// player.broadcastUserInfo();
		if (player.isGM())
		{
			final String target = (player.getTarget() != null ? player.getTarget().getName() : "no-target");
			GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", "Drop", target, "(id: " + dropedItem.getId() + " name: " + dropedItem.getItemName() + " objId: " + dropedItem.getObjectId() + " x: " + player.getX() + " y: " + player.getY() + " z: " + player.getZ() + ")");
		}
		
		if ((dropedItem != null) && (dropedItem.getId() == Inventory.ADENA_ID) && (dropedItem.getCount() >= 1000000))
		{
			final String msg = "Character (" + player.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
			PacketLogger.warning(msg);
			AdminData.getInstance().broadcastMessageToGMs(msg);
		}
	}
}
