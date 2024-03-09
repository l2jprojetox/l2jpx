package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

/**
 * Format:(ch) h
 * @author -Wooden-
 */
public final class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int partyLeaderId;
	
	@Override
	protected void readImpl()
	{
		partyLeaderId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2PcInstance player = L2World.getInstance().getPlayer(partyLeaderId);
		if (player != null && player.getParty() != null)
		{
			activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:26 RequestExMPCCShowPartyMembersInfo";
	}
	
}
