package com.px.gameserver.network.clientpackets;

import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.CommandChannel;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestExAcceptJoinMPCC extends L2GameClientPacket
{
	private int _response;
	
	@Override
	protected void readImpl()
	{
		_response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
			return;
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
		
		final Party requestorParty = requestor.getParty();
		if (requestorParty == null)
			return;
		
		final Party targetParty = player.getParty();
		if (targetParty == null)
			return;
		
		if (_response == 1)
		{
			CommandChannel channel = requestorParty.getCommandChannel();
			if (channel == null)
			{
				// Check the possibility to setup the CommandChannel.
				if (!CommandChannel.checkAuthority(requestor, true))
					return;
				
				new CommandChannel(requestorParty, targetParty);
			}
			else
				channel.addParty(targetParty);
		}
		else
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DECLINED_CHANNEL_INVITATION).addCharName(player));
	}
}