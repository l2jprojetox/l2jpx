package com.l2jpx.gameserver.network.serverpackets;

public final class TradePressOtherOk extends L2GameServerPacket
{
	public static final TradePressOtherOk STATIC_PACKET = new TradePressOtherOk();
	
	private TradePressOtherOk()
	{
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x7c);
	}
}