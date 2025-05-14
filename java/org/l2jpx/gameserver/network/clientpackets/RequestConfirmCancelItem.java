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
import org.l2jpx.gameserver.data.xml.VariationData;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ExPutItemResultForVariationCancel;
import org.l2jpx.gameserver.util.Util;

/**
 * Format(ch) d
 * @author -Wooden-
 */
public class RequestConfirmCancelItem extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		final Item item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			return;
		}
		
		if (item.getOwnerId() != player.getObjectId())
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tryied to destroy augment on item that doesn't own.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!item.isAugmented())
		{
			player.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}
		
		if (item.isPvp() && !Config.ALT_ALLOW_AUGMENT_PVP_ITEMS)
		{
			player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		final long price = VariationData.getInstance().getCancelFee(item.getId(), item.getAugmentation().getMineralId());
		if (price < 0)
		{
			player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		player.sendPacket(new ExPutItemResultForVariationCancel(item, price));
	}
}
