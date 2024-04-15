package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.items.CrystalType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExConfirmVariationRefiner;

/**
 * Fromat(ch) dd
 * @author -Wooden-
 */
public class RequestConfirmRefinerItem extends AbstractRefinePacket
{
	private int _targetItemObjId;
	private int _refinerItemObjId;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
			return;
		
		final ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
		if (refinerItem == null)
			return;
		
		if (!isValid(activeChar, targetItem, refinerItem))
		{
			activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		final int refinerItemId = refinerItem.getItem().getItemId();
		final CrystalType grade = targetItem.getItem().getCrystalType();
		final int gemStoneId = getGemStoneId(grade);
		final int gemStoneCount = getGemStoneCount(grade);
		
		activeChar.sendPacket(new ExConfirmVariationRefiner(_refinerItemObjId, refinerItemId, gemStoneId, gemStoneCount));
	}
}