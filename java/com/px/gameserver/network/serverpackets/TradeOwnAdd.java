package com.px.gameserver.network.serverpackets;

import com.px.gameserver.model.trade.TradeItem;

public class TradeOwnAdd extends L2GameServerPacket
{
	private final TradeItem _item;
	private final int _quantity;
	
	public TradeOwnAdd(TradeItem item, int quantity)
	{
		_item = item;
		_quantity = quantity;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x20);
		
		writeH(1);
		writeH(_item.getItem().getType1());
		writeD(_item.getObjectId());
		writeD(_item.getItem().getItemId());
		writeD(_quantity);
		writeH(_item.getItem().getType2());
		writeH(0x00);
		writeD(_item.getItem().getBodyPart());
		writeH(_item.getEnchant());
		writeH(0x00);
		writeH(0x00);
	}
}