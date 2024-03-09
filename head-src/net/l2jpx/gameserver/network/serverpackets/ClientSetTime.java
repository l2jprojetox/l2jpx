package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.controllers.GameTimeController;

public class ClientSetTime extends L2GameServerPacket
{
	// public static final ClientSetTime STATIC_PACKET = new ClientSetTime(); private ClientSetTime() { }
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xEC);
		writeD(GameTimeController.getInstance().getGameTime()); // time in client minutes
		writeD(6); // constant to match the server time( this determines the speed of the client clock)
	}
	
	@Override
	public String getType()
	{
		return "[S] f2 ClientSetTime [dd]";
	}
}
