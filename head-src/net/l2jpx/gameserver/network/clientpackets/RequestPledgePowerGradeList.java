package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.PledgePowerGradeList;

/**
 * Format: (ch)
 * @author -Wooden-
 * @author ReynalDev
 */
public final class RequestPledgePowerGradeList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		final L2Clan clan = player.getClan();
		
		if (clan != null)
		{
			player.sendPacket(new PledgePowerGradeList(clan.getAllRankPrivs().keySet(), clan.getMembers()));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:1A RequestPledgePowerGradeList";
	}
	
}
