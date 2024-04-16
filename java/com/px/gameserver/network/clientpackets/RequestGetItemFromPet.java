package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.network.SystemMessageId;

public final class RequestGetItemFromPet extends L2GameClientPacket
{
	private int _objectId;
	private int _amount;
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount = readD();
		_unknown = readD();// = 0 for most trades
	}
	
	@Override
	protected void runImpl()
	{
		if (_amount <= 0)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null || !player.hasPet())
			return;
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		player.cancelActiveEnchant();
		
		final Pet pet = (Pet) player.getSummon();
		
		pet.transferItem("Transfer", _objectId, _amount, player);
	}
}