package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.managers.RaidBossPointsManager;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.ExGetBossRecord;

/**
 * Format: (ch) d
 * @author -Wooden-
 */
public class RequestGetBossRecord extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int bossId;
	
	@Override
	protected void readImpl()
	{
		bossId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final int points = RaidBossPointsManager.getPointsByOwnerId(activeChar.getObjectId());
		final int ranking = RaidBossPointsManager.calculateRanking(activeChar.getObjectId());
		
		// trigger packet
		activeChar.sendPacket(new ExGetBossRecord(ranking, points, RaidBossPointsManager.getList(activeChar)));
		sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:18 RequestGetBossRecord";
	}
	
}
