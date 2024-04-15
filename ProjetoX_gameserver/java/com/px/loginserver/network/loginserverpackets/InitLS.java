package com.px.loginserver.network.loginserverpackets;

import com.px.loginserver.LoginServer;
import com.px.loginserver.network.serverpackets.ServerBasePacket;

public class InitLS extends ServerBasePacket
{
	public InitLS(byte[] publickey)
	{
		writeC(0x00);
		writeD(LoginServer.PROTOCOL_REV);
		writeD(publickey.length);
		writeB(publickey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}