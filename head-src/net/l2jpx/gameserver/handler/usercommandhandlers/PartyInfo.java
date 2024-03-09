package net.l2jpx.gameserver.handler.usercommandhandlers;

import net.l2jpx.gameserver.handler.IUserCommandHandler;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * Support for /partyinfo command Added by Tempy - 28 Jul 05
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if (!activeChar.isInParty())
		{
			return false;
		}
		
		L2Party playerParty = activeChar.getParty();
		final int memberCount = playerParty.getMemberCount();
		final int lootDistribution = playerParty.getLootDistribution();
		final String partyLeader = playerParty.getPartyMembers().get(0).getName();
		
		playerParty = null;
		
		activeChar.sendPacket(new SystemMessage(SystemMessageId.PARTY_INFORMATION));
		
		switch (lootDistribution)
		{
			case L2Party.ITEM_LOOTER:
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LOOTING_FINDERS_KEEPERS));
				break;
			case L2Party.ITEM_ORDER:
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LOOTING_BY_TURN));
				break;
			case L2Party.ITEM_ORDER_SPOIL:
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LOOTING_BY_TURN_INCLUDE_SPOIL));
				break;
			case L2Party.ITEM_RANDOM:
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LOOTING_RANDOM));
				break;
			case L2Party.ITEM_RANDOM_SPOIL:
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LOOTING_RANDOM_INCLUDE_SPOIL));
				break;
		}
		activeChar.sendPacket(new SystemMessage(SystemMessageId.PARTY_LEADER_S1).addString(partyLeader));
		activeChar.sendMessage("Members: " + memberCount + "/9");
		activeChar.sendPacket(new SystemMessage(SystemMessageId.FOOTER_2));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
