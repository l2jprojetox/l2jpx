package com.px.gameserver.network.serverpackets;

import java.util.Set;

import com.px.gameserver.model.actor.instance.Pet;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.model.item.kind.Item;

public class PetItemList extends L2GameServerPacket
{
	private final Set<ItemInstance> _items;
	
	public PetItemList(Pet character)
	{
		_items = character.getInventory().getItems();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xB2);
		writeH(_items.size());
		
		for (ItemInstance temp : _items)
		{
			Item item = temp.getItem();
			
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			writeH(temp.isEquipped() ? 0x01 : 0x00);
			writeD(item.getBodyPart());
			writeH(temp.getEnchantLevel());
			writeH(temp.getCustomType2());
		}
	}
}