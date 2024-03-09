package net.l2jpx.gameserver.handler.usercommandhandlers;

import net.l2jpx.gameserver.handler.IUserCommandHandler;
import net.l2jpx.gameserver.model.L2CommandChannel;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ExOpenMPCC;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Chris
 * @author ReynalDev
 */
public class ChannelLeave implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		96
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (activeChar == null)
		{
			return false;
		}
		
		if (activeChar.isInParty())
		{
			if (activeChar.getParty().isLeader(activeChar) && activeChar.getParty().isInCommandChannel())
			{
				L2CommandChannel channel = activeChar.getParty().getCommandChannel();
				L2Party party = activeChar.getParty();
				channel.removeParty(party);
				
				party.getLeader().sendPacket(new SystemMessage(SystemMessageId.LEFT_COMMAND_CHANNEL));
				channel.broadcastToChannelMembers(new SystemMessage(SystemMessageId.S1_PARTY_LEFT_COMMAND_CHANNEL).addString(party.getLeader().getName()));
				
				if(activeChar == channel.getChannelLeader())
				{
					if(channel.getPartys().size() >=  2)
					{
						L2PcInstance newCCLeader = channel.getPartys().get(0).getLeader();
						channel.setChannelLeader(newCCLeader);
						SystemMessage msg = new SystemMessage(SystemMessageId.COMMAND_CHANNEL_LEADER_NOW_S1);
						msg.addString(newCCLeader.getName());
						channel.broadcastToChannelMembers(msg);
						channel.broadcastToChannelMembers(new ExOpenMPCC());
					}
				}
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
