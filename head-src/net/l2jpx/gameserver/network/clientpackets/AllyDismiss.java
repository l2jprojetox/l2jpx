package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class AllyDismiss extends L2GameClientPacket
{
	private String clanName;
	
	@Override
	protected void readImpl()
	{
		clanName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (clanName == null)
		{
			return;
		}
		
		final L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		if (player.getClan() == null)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER));
			return;
		}
		
		final L2Clan leaderClan = player.getClan();
		
		if (leaderClan.getAllyId() == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_CURRENT_ALLIED_WITH_ANY_CLANS));
			return;
		}
		
		if (!player.isClanLeader() || leaderClan.getClanId() != leaderClan.getAllyId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_ALLIANCE_LEADERS));
			return;
		}
		
		final L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
		
		if (clan == null)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.THAT_CLAN_DOES_NOT_EXIST));
			return;
		}
		
		if (clan.getClanId() == leaderClan.getClanId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.ALLIANCE_LEADERS_CANNOT_WITHDRAW));
			return;
		}
		
		if (clan.getAllyId() != leaderClan.getAllyId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.DIFFERENT_ALLIANCE));
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		
		leaderClan.setAllyPenaltyExpiryTime(currentTime + Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000L, L2Clan.PENALTY_TYPE_DISMISS_CLAN); // 24*60*60*1000 = 86400000
		
		leaderClan.updateClanInDB();
		
		clan.setAllyId(0);
		clan.setAllyName(null);
		clan.setAllyPenaltyExpiryTime(currentTime + Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000L, L2Clan.PENALTY_TYPE_CLAN_DISMISSED); // 24*60*60*1000 = 86400000
		clan.setAllyCrest(0);
		clan.updateClanInDB();
		
		player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_A_CLAN));
	}
	
	@Override
	public String getType()
	{
		return "[C] 85 AllyDismiss";
	}
}