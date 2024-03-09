package net.l2jpx.loginserver.network.loginserverpackets;

import net.l2jpx.loginserver.L2LoginServer;
import net.l2jpx.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class InitLS extends ServerBasePacket
{
	// ID 0x00
	// format
	// d proto rev
	// d key size
	// b key
	
	public InitLS(final byte[] publickey)
	{
		writeC(0x00);
		writeD(L2LoginServer.PROTOCOL_REV);
		writeD(publickey.length);
		writeB(publickey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
