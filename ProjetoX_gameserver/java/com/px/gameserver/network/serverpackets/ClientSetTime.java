package com.px.gameserver.network.serverpackets;

import com.px.gameserver.taskmanager.GameTimeTaskManager;

public class ClientSetTime extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xEC);
		writeD(GameTimeTaskManager.getInstance().getGameTime());
		writeD(6);
	}
}