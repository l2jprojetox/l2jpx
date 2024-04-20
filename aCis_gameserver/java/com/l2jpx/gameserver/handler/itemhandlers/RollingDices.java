package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.Dice;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class RollingDices implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (!player.getClient().performAction(FloodProtector.ROLL_DICE))
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
			return;
		}
		
		final int number = Rnd.get(1, 6);
		
		player.broadcastPacket(new Dice(player, item.getItemId(), number));
		player.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ROLLED_S2).addCharName(player).addNumber(number));
	}
}