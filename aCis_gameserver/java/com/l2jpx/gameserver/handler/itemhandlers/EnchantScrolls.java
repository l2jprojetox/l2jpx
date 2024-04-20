package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.ChooseInventoryItem;

public class EnchantScrolls implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (player.getActiveEnchantItem() == null)
			player.sendPacket(SystemMessageId.SELECT_ITEM_TO_ENCHANT);
		
		player.setActiveEnchantItem(item);
		player.sendPacket(new ChooseInventoryItem(item.getItemId()));
	}
}
