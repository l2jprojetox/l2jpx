package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.Henna;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.HennaInfo;
import com.px.gameserver.network.serverpackets.UserInfo;

public final class RequestHennaUnequip extends L2GameClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Henna henna = player.getHennaList().getBySymbolId(_symbolId);
		if (henna == null)
			return;
		
		if (player.getAdena() < henna.getRemovePrice())
		{
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			return;
		}
		
		boolean success = player.getHennaList().remove(henna);
		if (!success)
			return;
		
		sendPacket(new HennaInfo(player));
		sendPacket(new UserInfo(player));
		
		player.reduceAdena("Henna", henna.getRemovePrice(), player, false);
		
		player.addItem("Henna", henna.getDyeId(), Henna.REMOVE_AMOUNT, player, true);
		player.sendPacket(SystemMessageId.SYMBOL_DELETED);
	}
}