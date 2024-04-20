package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.model.CharSelectSlot;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.GameClient;
import com.l2jpx.gameserver.network.GameClient.GameClientState;
import com.l2jpx.gameserver.network.serverpackets.CharSelected;
import com.l2jpx.gameserver.network.serverpackets.SSQInfo;

public class RequestGameStart extends L2GameClientPacket
{
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readD();
		readH(); // Not used.
		readD(); // Not used.
		readD(); // Not used.
		readD(); // Not used.
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (!client.performAction(FloodProtector.CHARACTER_SELECT))
			return;
		
		// we should always be able to acquire the lock but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock())
		{
			try
			{
				// should always be null but if not then this is repeated packet and nothing should be done here
				if (client.getPlayer() == null)
				{
					final CharSelectSlot info = client.getCharSelectSlot(_slot);
					if (info == null || info.getAccessLevel() < 0)
						return;
					
					// Load up character from disk
					final Player player = client.loadCharFromDisk(_slot);
					if (player == null)
						return;
					
					player.setClient(client);
					client.setPlayer(player);
					player.setOnlineStatus(true, true);
					
					sendPacket(SSQInfo.sendSky());
					
					client.setState(GameClientState.ENTERING);
					
					sendPacket(new CharSelected(player, client.getSessionId().playOkID1));
				}
			}
			finally
			{
				client.getActiveCharLock().unlock();
			}
		}
	}
}