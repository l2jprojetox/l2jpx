package com.px.gameserver.network.clientpackets;

import com.px.gameserver.data.xml.AugmentationData;
import com.px.gameserver.model.L2Augmentation;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.ExVariationResult;
import com.px.gameserver.network.serverpackets.InventoryUpdate;
import com.px.gameserver.network.serverpackets.StatusUpdate;

/**
 * Format:(ch) dddd
 * @author -Wooden-
 */
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
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
		if (refinerItem == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final ItemInstance gemStoneItem = activeChar.getInventory().getItemByObjectId(_gemStoneItemObjId);
		if (gemStoneItem == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		if (!isValid(activeChar, targetItem, refinerItem, gemStoneItem))
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final LifeStone ls = getLifeStone(refinerItem.getItemId());
		if (ls == null)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final int lifeStoneLevel = ls.getLevel();
		final int lifeStoneGrade = ls.getGrade();
		if (_gemStoneCount != getGemStoneCount(targetItem.getItem().getCrystalType()))
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		// unequip item
		if (targetItem.isEquipped())
		{
			ItemInstance[] unequipped = activeChar.getInventory().unEquipItemInSlotAndRecord(targetItem.getLocationSlot());
			InventoryUpdate iu = new InventoryUpdate();
			
			for (ItemInstance itm : unequipped)
				iu.addModifiedItem(itm);
			
			activeChar.sendPacket(iu);
			activeChar.broadcastUserInfo();
		}
		
		// Consume the life stone
		if (!activeChar.destroyItem("RequestRefine", refinerItem, 1, null, false))
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		// Consume gemstones
		if (!activeChar.destroyItem("RequestRefine", gemStoneItem, _gemStoneCount, null, false))
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0));
			activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}
		
		final L2Augmentation aug = AugmentationData.getInstance().generateRandomAugmentation(lifeStoneLevel, lifeStoneGrade);
		targetItem.setAugmentation(aug);
		
		final int stat12 = 0x0000FFFF & aug.getAugmentationId();
		final int stat34 = aug.getAugmentationId() >> 16;
		activeChar.sendPacket(new ExVariationResult(stat12, stat34, 1));
		
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		activeChar.sendPacket(iu);
		
		StatusUpdate su = new StatusUpdate(activeChar);
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);
	}
}