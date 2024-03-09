package net.l2jpx.loginserver.network.clientpackets;

import net.l2jpx.Config;
import net.l2jpx.loginserver.LoginController;
import net.l2jpx.loginserver.SessionKey;
import net.l2jpx.loginserver.network.serverpackets.LoginFail.LoginFailReason;
import net.l2jpx.loginserver.network.serverpackets.PlayFail.PlayFailReason;
import net.l2jpx.loginserver.network.serverpackets.PlayOk;

/**
 * Fromat is ddc d: first part of session id d: second part of session id c: server ID
 */
public class RequestServerLogin extends L2LoginClientPacket
{
	private int skey1;
	private int skey2;
	private int serverId;
	
	/**
	 * @return
	 */
	public int getSessionKey1()
	{
		return skey1;
	}
	
	/**
	 * @return
	 */
	public int getSessionKey2()
	{
		return skey2;
	}
	
	/**
	 * @return
	 */
	public int getServerID()
	{
		return serverId;
	}
	
	@Override
	public boolean readImpl()
	{
		if (super.buf.remaining() >= 9)
		{
			skey1 = readD();
			skey2 = readD();
			serverId = readC();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		final SessionKey sk = getClient().getSessionKey();
		
		// if we didnt showed the license we cant check these values
		if (!Config.SHOW_LICENCE || sk.checkLoginPair(skey1, skey2))
		{
			if (LoginController.getInstance().isLoginPossible(getClient(), serverId))
			{
				getClient().setJoinedGS(true);
				getClient().sendPacket(new PlayOk(sk));
			}
			else
			{
				getClient().close(PlayFailReason.REASON_TOO_MANY_PLAYERS);
			}
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
