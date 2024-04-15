package com.px.gameserver.network.serverpackets;

import com.px.gameserver.enums.FailReason;

public class AuthLoginFail extends L2GameServerPacket
{
	private final FailReason _reason;
	
	public AuthLoginFail(FailReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x14);
		writeD(_reason.ordinal());
	}
}