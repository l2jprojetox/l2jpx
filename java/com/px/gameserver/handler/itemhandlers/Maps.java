package com.px.gameserver.handler.itemhandlers;

import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.serverpackets.ShowMiniMap;

/**
 * This class provides handling for items that should display a map when double clicked.
 */
public class Maps implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		playable.sendPacket(new ShowMiniMap(item.getItemId()));
	}
}