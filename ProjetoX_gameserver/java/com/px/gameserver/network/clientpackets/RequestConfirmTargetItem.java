package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExConfirmVariationItem;

/**
 * Format:(ch) d
 * @author -Wooden-
 */
public final class RequestConfirmTargetItem extends AbstractRefinePacket
{
	private int _itemObjId;
	
	@Override
	protected void readImpl()
	{
		_itemObjId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
		if (item == null)
			return;
		
		if (!isValid(activeChar, item))
		{
			// Different system message here
			if (item.isAugmented())
			{
				activeChar.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
				return;
			}
			
			activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		activeChar.sendPacket(new ExConfirmVariationItem(_itemObjId));
	}
}