package com.px.gameserver.handler.itemhandlers;

import com.px.commons.random.Rnd;

import com.px.gameserver.handler.IItemHandler;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.FloodProtectors;
import com.px.gameserver.network.FloodProtectors.Action;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.Dice;
import com.px.gameserver.network.serverpackets.SystemMessage;

public class RollingDice implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (!FloodProtectors.performAction(player.getClient(), Action.ROLL_DICE))
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
			return;
		}
		
		final int number = Rnd.get(1, 6);
		
		player.broadcastPacket(new Dice(player, item.getItemId(), number));
		player.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ROLLED_S2).addCharName(player).addNumber(number));
	}
}