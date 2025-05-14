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
package org.l2jpx.gameserver.network.clientpackets.crystalization;

import java.util.List;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.ItemCrystallizationData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.holders.ItemChanceHolder;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.item.type.CrystalType;
import org.l2jpx.gameserver.model.skill.CommonSkill;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.crystalization.ExGetCrystalizingEstimation;
import org.l2jpx.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class RequestCrystallizeEstimate extends ClientPacket
{
	private int _objectId;
	private long _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_count = readLong();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || player.isInCrystallize())
		{
			return;
		}
		
		// if (!client.getFloodProtectors().canPerformTransaction())
		// {
		// player.sendMessage("You are crystallizing too fast.");
		// return;
		// }
		
		if (_count < 1)
		{
			Util.handleIllegalPlayerAction(player, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + player.getName(), Config.DEFAULT_PUNISH);
			return;
		}
		
		if (player.isInStoreMode() || player.isInCrystallize())
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		final int skillLevel = player.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
		if (skillLevel <= 0)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if ((item == null) || item.isShadowItem() || item.isTimeLimitedItem() || item.isHeroItem() || (!Config.ALT_ALLOW_AUGMENT_DESTROY && item.isAugmented()))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!item.getTemplate().isCrystallizable() || (item.getTemplate().getCrystalCount() <= 0) || (item.getTemplate().getCrystalType() == CrystalType.NONE))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			PacketLogger.warning(player + ": tried to crystallize " + item.getTemplate());
			return;
		}
		
		if (_count > item.getCount())
		{
			_count = player.getInventory().getItemByObjectId(_objectId).getCount();
		}
		
		if (!player.getInventory().canManipulateWithItemId(item.getId()))
		{
			player.sendMessage("You cannot use this item.");
			return;
		}
		
		// Check if the char can crystallize items and return if false;
		boolean canCrystallize = true;
		
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case D:
			{
				if (skillLevel < 1)
				{
					canCrystallize = false;
				}
				break;
			}
			case C:
			{
				if (skillLevel < 2)
				{
					canCrystallize = false;
				}
				break;
			}
			case B:
			{
				if (skillLevel < 3)
				{
					canCrystallize = false;
				}
				break;
			}
			case A:
			{
				if (skillLevel < 4)
				{
					canCrystallize = false;
				}
				break;
			}
			case S:
			{
				if (skillLevel < 5)
				{
					canCrystallize = false;
				}
				break;
			}
			case R:
			{
				if (skillLevel < 6)
				{
					canCrystallize = false;
				}
				break;
			}
		}
		
		if (!canCrystallize)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Show crystallization rewards window.
		final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(item);
		if ((crystallizationRewards != null) && !crystallizationRewards.isEmpty())
		{
			player.setInCrystallize(true);
			player.sendPacket(new ExGetCrystalizingEstimation(crystallizationRewards));
		}
		else
		{
			player.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
		}
	}
}
