package com.px.loginserver.network.gameserverpackets;

import com.px.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerLogout extends ClientBasePacket
{
	private final String _account;
	
	public PlayerLogout(byte[] decrypt)
	{
		super(decrypt);
		_account = readS();
	}
	
	public String getAccount()
	{
		return _account;
	}
}