package com.px.gameserver.handler.usercommandhandlers;

import com.px.gameserver.handler.IUserCommandHandler;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.group.CommandChannel;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;

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