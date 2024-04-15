package com.px.loginserver.network.gameserverpackets;

import java.util.ArrayList;
import java.util.List;

import com.px.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerInGame extends ClientBasePacket
{
	private final List<String> _accounts = new ArrayList<>();
	
	public PlayerInGame(byte[] decrypt)
	{
		super(decrypt);
		
		int size = readH();
		for (int i = 0; i < size; i++)
			_accounts.add(readS());
	}
	
	public List<String> getAccounts()
	{
		return _accounts;
	}
}