package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.items.CrystalType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExConfirmVariationRefiner;

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
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance targetItem = player.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
			return;
		
		final ItemInstance refinerItem = player.getInventory().getItemByObjectId(_refinerItemObjId);
		if (refinerItem == null)
			return;
		
		if (!isValid(player, targetItem, refinerItem))
		{
			player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		final CrystalType grade = targetItem.getItem().getCrystalType();
		
		player.sendPacket(new ExConfirmVariationRefiner(_refinerItemObjId, refinerItem.getItem().getItemId(), grade.getGemstoneId(), grade.getGemstoneCount()));
	}
}