package com.px.loginserver.network.loginserverpackets;

import com.px.loginserver.network.serverpackets.ServerBasePacket;

public class PlayerAuthResponse extends ServerBasePacket
{
	public PlayerAuthResponse(String account, boolean response)
	{
		writeC(0x03);
		writeS(account);
		writeC(response ? 1 : 0);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}