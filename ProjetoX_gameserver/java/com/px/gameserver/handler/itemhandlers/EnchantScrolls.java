package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ChooseInventoryItem;

public class EnchantScrolls implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player activeChar = (Player) playable;
		if (activeChar.isCastingNow())
			return;
		
		if (activeChar.getActiveEnchantItem() == null)
			activeChar.sendPacket(SystemMessageId.SELECT_ITEM_TO_ENCHANT);
		
		activeChar.setActiveEnchantItem(item);
		activeChar.sendPacket(new ChooseInventoryItem(item.getItemId()));
	}
}
