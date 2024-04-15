package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.serverpackets.ManagePledgePower;

public final class RequestPledgePower extends L2GameClientPacket
{
	private int _rank;
	private int _action;
	private int _privs;
	
	@Override
	protected void readImpl()
	{
		_rank = readD();
		_action = readD();
		
		if (_action == 2)
			_privs = readD();
		else
			_privs = 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		if (_action == 2)
		{
			if (player.isClanLeader())
			{
				if (_rank == 9)
					_privs = (_privs & Clan.CP_CL_VIEW_WAREHOUSE) + (_privs & Clan.CP_CH_OPEN_DOOR) + (_privs & Clan.CP_CS_OPEN_DOOR);
				
				player.getClan().setPriviledgesForRank(_rank, _privs);
			}
		}
		else
			player.sendPacket(new ManagePledgePower(clan, _action, _rank));
	}
}