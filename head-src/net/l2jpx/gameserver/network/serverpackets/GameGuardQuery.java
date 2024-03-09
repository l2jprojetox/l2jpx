package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.crypt.nProtect;

/**
 * @author zabbix Lets drink to code!
 */
public class GameGuardQuery extends L2GameServerPacket
{
	public GameGuardQuery()
	{
		
	}
	
	@Override
	public void runImpl()
	{
		// Lets make user as gg-unauthorized
		// We will set him as ggOK after reply fromclient
		// or kick
		getClient().setGameGuardOk(false);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf9);
		nProtect.getInstance().sendGameGuardQuery(this);
	}
	
	@Override
	public String getType()
	{
		return "[S] F9 GameGuardQuery";
	}
}
