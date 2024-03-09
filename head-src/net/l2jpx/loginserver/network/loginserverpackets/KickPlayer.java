package net.l2jpx.loginserver.network.loginserverpackets;

import net.l2jpx.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class KickPlayer extends ServerBasePacket
{
	public KickPlayer(final String account)
	{
		writeC(0x04);
		writeS(account);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
