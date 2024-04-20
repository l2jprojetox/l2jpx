package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.commons.util.ArraysUtil;

import com.l2jpx.Config;
import com.l2jpx.gameserver.enums.actors.OperateType;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.craft.ManufactureItem;
import com.l2jpx.gameserver.model.craft.ManufactureList;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.RecipeShopMsg;

public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8;
	
	private ManufactureItem[] _items;
	
	@Override
	protected void readImpl()
	{
		int count = readD();
		if (count < 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			count = 0;
		
		_items = new ManufactureItem[count];
		
		for (int i = 0; i < count; i++)
		{
			final int recipeId = readD();
			final int cost = readD();
			
			_items[i] = new ManufactureItem(recipeId, cost);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// Retrieve and clear the manufacture list.
		final ManufactureList manufactureList = player.getManufactureList();
		manufactureList.clear();
		
		// Integrity check.
		if (ArraysUtil.isEmpty(_items))
		{
			player.setOperateType(OperateType.NONE);
			player.sendPacket(SystemMessageId.NO_RECIPES_REGISTERED);
			return;
		}
		
		// Integrity check.
		if (!player.getRecipeBook().canPassManufactureProcess(_items))
		{
			player.setOperateType(OperateType.NONE);
			return;
		}
		
		// Check multiple conditions. Message and OperateType reset are sent directly from the method.
		if (!player.canOpenPrivateStore(false))
			return;
		
		// Feed it with packet informations.
		manufactureList.set(_items);
		
		player.getMove().stop();
		player.sitDown();
		player.setOperateType(OperateType.MANUFACTURE);
		player.broadcastUserInfo();
		player.broadcastPacket(new RecipeShopMsg(player));
	}
}