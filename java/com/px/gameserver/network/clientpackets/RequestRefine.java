package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.AugmentationData;
import com.px.gameserver.enums.ShortcutType;
import com.px.gameserver.model.Augmentation;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.LifeStone;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExVariationResult;

public final class RequestRefine extends AbstractRefinePacket
{
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemStoneItemObjId;
	private int _gemStoneCount;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
		_gemStoneItemObjId = readD();
		_gemStoneCount = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance targetItem = player.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final ItemInstance refinerItem = player.getInventory().getItemByObjectId(_refinerItemObjId);
		if (refinerItem == null)
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final ItemInstance gemStoneItem = player.getInventory().getItemByObjectId(_gemStoneItemObjId);
		if (gemStoneItem == null)
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		if (!isValid(player, targetItem, refinerItem, gemStoneItem))
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final LifeStone ls = getLifeStone(refinerItem.getItemId());
		if (ls == null)
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		if (_gemStoneCount != targetItem.getItem().getCrystalType().getGemstoneCount())
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		// unequip item
		if (targetItem.isEquipped())
		{
			player.getInventory().unequipItemInSlotAndRecord(targetItem.getLocationSlot());
			player.broadcastUserInfo();
		}
		
		// Consume the life stone
		if (!player.destroyItem("RequestRefine", refinerItem, 1, null, false))
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		// Consume gemstones
		if (!player.destroyItem("RequestRefine", gemStoneItem, _gemStoneCount, null, false))
		{
			player.sendPacket(ExVariationResult.RESULT_FAILED);
			player.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final Augmentation aug = AugmentationData.getInstance().generateRandomAugmentation(ls.getLevel(), ls.getGrade());
		targetItem.setAugmentation(aug, player);
		
		final int stat12 = 0x0000FFFF & aug.getId();
		final int stat34 = aug.getId() >> 16;
		player.sendPacket(new ExVariationResult(stat12, stat34, 1));
		
		// Refresh shortcuts.
		player.getShortcutList().refreshShortcuts(s -> targetItem.getObjectId() == s.getId() && s.getType() == ShortcutType.ITEM);
	}
}