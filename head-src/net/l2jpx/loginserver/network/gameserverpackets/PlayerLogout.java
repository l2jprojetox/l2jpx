package net.l2jpx.loginserver.network.gameserverpackets;

import net.l2jpx.loginserver.network.clientpackets.ClientBasePacket;

/**
 * @author -Wooden-
 */
public class PlayerLogout extends ClientBasePacket
{
	
	private final String account;
	
	/**
	 * @param decrypt
	 */
	public PlayerLogout(final byte[] decrypt)
	{
		super(decrypt);
		account = readS();
	}
	
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return account;
	}
	
}
