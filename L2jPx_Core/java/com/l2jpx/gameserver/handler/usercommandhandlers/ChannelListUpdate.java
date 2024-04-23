package com.l2jpx.gameserver.handler.usercommandhandlers;

import com.l2jpx.gameserver.handler.IUserCommandHandler;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.group.CommandChannel;
import com.l2jpx.gameserver.model.group.Party;
import com.l2jpx.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;

public class ChannelListUpdate implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		97
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		final Party party = player.getParty();
		if (party == null)
			return;
		
		final CommandChannel channel = party.getCommandChannel();
		if (channel == null)
			return;
		
		player.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}