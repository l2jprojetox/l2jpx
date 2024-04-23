package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.gameserver.data.manager.FestivalOfDarknessManager;
import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.GameClient;
import com.l2jpx.gameserver.network.GameClient.GameClientState;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.CharSelectInfo;
import com.l2jpx.gameserver.network.serverpackets.RestartResponse;
import com.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

import l2jbrasil.AutoFarm.AutofarmPlayerRoutine;

public final class RequestRestart extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.getActiveEnchantItem() != null || player.isLocked())
		{
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_RESTART))
		{
			player.sendPacket(SystemMessageId.NO_RESTART_HERE);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
        if (player.isAutoFarm())
        {
        	if(AutofarmPlayerRoutine.isIpAllowed(player.getIP())) 
        	{
        		AutofarmPlayerRoutine.removeIpEntry(player.getObjectId());
        	}   
        }
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(SystemMessageId.CANT_RESTART_WHILE_FIGHTING);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isFestivalParticipant() && FestivalOfDarknessManager.getInstance().isFestivalInitialized())
		{
			player.sendPacket(SystemMessageId.NO_RESTART_HERE);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		player.removeFromBossZone();
		
		final GameClient client = getClient();
		
		// detach the client from the char so that the connection isnt closed in the deleteMe
		player.setClient(null);
		
		// removing player from the world
		player.deleteMe();
		
		client.setPlayer(null);
		client.setState(GameClientState.AUTHED);
		
		sendPacket(RestartResponse.valueOf(true));
		
		// send char list
		final CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		sendPacket(cl);
		client.setCharSelectSlot(cl.getCharacterSlots());
	}
}