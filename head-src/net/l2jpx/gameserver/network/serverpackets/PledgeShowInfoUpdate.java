package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.model.L2Clan;

/**
 * @author ReynalDev
 */
public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private L2Clan clan;
	
	public PledgeShowInfoUpdate(final L2Clan clan)
	{
		this.clan = clan;
	}
	
	@Override
	protected void writeImpl()
	{
		int rank = ClanTable.getInstance().getTopRate(clan.getClanId());
		// ddddddddddSdd
		writeC(0x88);
		// sending empty data so client will ask all the info in response ;)
		writeD(clan.getClanId());
		writeD(clan.getCrestId());
		writeD(clan.getLevel()); 
		writeD(clan.getHasFort() != 0 ? clan.getHasFort() : clan.getCastleId());
		writeD(clan.getHasHideout());
		writeD(rank);
		writeD(clan.getReputationScore());
		writeD(0);
		writeD(0);
		writeD(clan.getAllyId());
		writeS(clan.getAllyName());
		writeD(clan.getAllyCrestId());
		writeD(clan.isAtWar() ? 1 : 0);
	}
	
	@Override
	public String getType()
	{
		return "[S] 88 PledgeShowInfoUpdate";
	}
	
}
