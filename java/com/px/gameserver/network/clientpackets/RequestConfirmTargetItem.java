package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExConfirmVariationItem;

public final class RequestConfirmTargetItem extends AbstractRefinePacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
			return;
		
		if (!isValid(player, item))
		{
			// Different system message here
			if (item.isAugmented())
			{
				player.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
				return;
			}
			
			player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		player.sendPacket(new ExConfirmVariationItem(_objectId));
	}
}