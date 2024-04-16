package com.px.gameserver.network.serverpackets;

public class GameGuardQuery extends L2GameServerPacket
{
	public GameGuardQuery()
	{
	}
	
	@Override
	public void runImpl()
	{
		getClient().setGameGuardOk(false);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf9);
	}
}