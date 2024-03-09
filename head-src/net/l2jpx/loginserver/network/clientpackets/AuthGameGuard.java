package net.l2jpx.loginserver.network.clientpackets;

import net.l2jpx.loginserver.L2LoginClient.LoginClientState;
import net.l2jpx.loginserver.network.serverpackets.GGAuth;
import net.l2jpx.loginserver.network.serverpackets.LoginFail.LoginFailReason;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends L2LoginClientPacket
{
	private int sessionId;
	private int data1;
	private int data2;
	private int data3;
	private int data4;
	
	public int getSessionId()
	{
		return sessionId;
	}
	
	public int getData1()
	{
		return data1;
	}
	
	public int getData2()
	{
		return data2;
	}
	
	public int getData3()
	{
		return data3;
	}
	
	public int getData4()
	{
		return data4;
	}
	
	/**
	 * @see net.l2jpx.loginserver.network.clientpackets.L2LoginClientPacket#readImpl()
	 */
	@Override
	protected boolean readImpl()
	{
		if (super.buf.remaining() >= 20)
		{
			sessionId = readD();
			data1 = readD();
			data2 = readD();
			data3 = readD();
			data4 = readD();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (sessionId == getClient().getSessionId())
		{
			getClient().setState(LoginClientState.AUTHED_GG);
			getClient().sendPacket(new GGAuth(getClient().getSessionId()));
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
