package com.l2jpx.loginserver.network.gameserverpackets;

import com.l2jpx.commons.network.AttributeType;
import com.l2jpx.commons.network.ServerType;

import com.l2jpx.loginserver.data.manager.GameServerManager;
import com.l2jpx.loginserver.model.GameServerInfo;
import com.l2jpx.loginserver.network.clientpackets.ClientBasePacket;

public class ServerStatus extends ClientBasePacket
{
	private static final int ON = 0x01;
	
	public ServerStatus(byte[] decrypt, int serverId)
	{
		super(decrypt);
		
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServers().get(serverId);
		if (gsi != null)
		{
			int size = readD();
			for (int i = 0; i < size; i++)
			{
				int type = readD();
				int value = readD();
				
				switch (AttributeType.VALUES[type])
				{
					case STATUS:
						gsi.setType(ServerType.VALUES[value]);
						break;
					
					case CLOCK:
						gsi.setShowingClock(value == ON);
						break;
					
					case BRACKETS:
						gsi.setShowingBrackets(value == ON);
						break;
					
					case AGE_LIMIT:
						gsi.setAgeLimit(value);
						break;
					
					case TEST_SERVER:
						gsi.setTestServer(value == ON);
						break;
					
					case PVP_SERVER:
						gsi.setPvp(value == ON);
						break;
					
					case MAX_PLAYERS:
						gsi.setMaxPlayers(value);
						break;
				}
			}
		}
	}
}