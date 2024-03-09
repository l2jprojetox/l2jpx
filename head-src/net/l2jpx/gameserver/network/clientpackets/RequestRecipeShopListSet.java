package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.L2ManufactureItem;
import net.l2jpx.gameserver.model.L2ManufactureList;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.RecipeShopMsg;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private int count;
	private int[] items; // count*2
	
	@Override
	protected void readImpl()
	{
		count = readD();
		
		if (count < 0 || count * 8 > buf.remaining() || count > Config.MAX_ITEM_IN_PACKET)
		{
			count = 0;
		}
		
		items = new int[count * 2];
		
		for (int x = 0; x < count; x++)
		{
			final int recipeID = readD();
			items[x * 2 + 0] = recipeID;
			final int cost = readD();
			items[x * 2 + 1] = cost;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.isInDuel())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isMoving())
		{
			player.sendPacket(SystemMessageId.CANNOT_OPEN_A_PRIVATE_STORE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isTradeDisabled())
		{
			player.sendMessage("Private manufacture are disable here. Try in another place.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (count == 0)
		{
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			player.standUp();
		}
		else
		{
			final L2ManufactureList createList = new L2ManufactureList();
			
			for (int x = 0; x < count; x++)
			{
				final int recipeID = items[x * 2 + 0];
				final int cost = items[x * 2 + 1];
				createList.add(new L2ManufactureItem(recipeID, cost));
			}
			createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
			player.setCreateList(createList);
			
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.sendPacket(new RecipeShopMsg(player));
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] b2 RequestRecipeShopListSet";
	}
	
}
