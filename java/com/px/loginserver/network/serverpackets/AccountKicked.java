package com.px.loginserver.network.serverpackets;

import com.px.loginserver.enums.AccountKickedReason;

public final class AccountKicked extends L2LoginServerPacket
{
	private final AccountKickedReason _reason;
	
	public AccountKicked(AccountKickedReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void write()
	{
		writeC(0x02);
		writeD(_reason.getCode());
	}
}