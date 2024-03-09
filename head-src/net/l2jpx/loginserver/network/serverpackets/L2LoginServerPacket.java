package net.l2jpx.loginserver.network.serverpackets;

import net.l2jpx.loginserver.L2LoginClient;
import net.l2jpx.netcore.SendablePacket;

/**
 * @author programmos
 */
public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient>
{
	public abstract String getType();
}
