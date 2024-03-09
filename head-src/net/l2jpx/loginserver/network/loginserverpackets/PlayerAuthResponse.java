package net.l2jpx.loginserver.network.loginserverpackets;

import net.l2jpx.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends ServerBasePacket
{
	public PlayerAuthResponse(final String account, final boolean response)
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
