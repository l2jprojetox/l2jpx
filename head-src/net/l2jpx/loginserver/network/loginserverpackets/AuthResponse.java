package net.l2jpx.loginserver.network.loginserverpackets;

import net.l2jpx.loginserver.datatables.xml.GameServerTable;
import net.l2jpx.loginserver.network.serverpackets.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class AuthResponse extends ServerBasePacket
{
	/**
	 * @param serverId
	 */
	public AuthResponse(final int serverId)
	{
		writeC(0x02);
		writeC(serverId);
		writeS(GameServerTable.getInstance().getServerNameById(serverId));
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
