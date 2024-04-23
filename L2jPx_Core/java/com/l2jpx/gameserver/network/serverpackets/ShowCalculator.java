package com.l2jpx.gameserver.network.serverpackets;

public class ShowCalculator extends L2GameServerPacket
{
	private final int _calculatorId;
	
	public ShowCalculator(int calculatorId)
	{
		_calculatorId = calculatorId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdc);
		writeD(_calculatorId);
	}
}