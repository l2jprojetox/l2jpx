package net.l2jpx.loginserver.network.loginserverpackets;

import net.l2jpx.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class LoginServerFail extends ServerBasePacket
{
	/**
	 * @param reason
	 */
	public LoginServerFail(final int reason)
	{
		writeC(0x01);
		writeC(reason);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
	
	public static final int REASON_IP_BANNED = 1;
	public static final int REASON_IP_RESERVED = 2;
	public static final int REASON_WRONG_HEXID = 3;
	public static final int REASON_ID_RESERVED = 4;
	public static final int REASON_NO_FREE_ID = 5;
	public static final int NOT_AUTHED = 6;
	public static final int REASON_ALREADY_LOGGED8IN = 7;
	
}
