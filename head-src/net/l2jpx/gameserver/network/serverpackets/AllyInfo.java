package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.datatables.sql.ClanTable;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;

/**
 * @author ReynalDev
 */
public class AllyInfo extends L2GameServerPacket
{
	private final L2PcInstance character;
	
	public AllyInfo(final L2PcInstance cha)
	{
		character = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.getAllyId() == 0)
		{
			character.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_CURRENT_ALLIED_WITH_ANY_CLANS));
			return;
		}
		
		// ======<AllyInfo>======
		SystemMessage sm = new SystemMessage(SystemMessageId.ALLIANCE_INFO_HEAD);
		character.sendPacket(sm);
		// ======<Ally Name>======
		sm = new SystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
		sm.addString(character.getClan().getAllyName());
		character.sendPacket(sm);
		int online = 0;
		int count = 0;
		int clancount = 0;
		for (final L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == character.getAllyId())
			{
				clancount++;
				online += clan.getOnlineMembers().size();
				count += clan.getMembers().size();
			}
		}
		// Connection
		sm = new SystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
		sm.addString("" + online);
		sm.addString("" + count);
		character.sendPacket(sm);
		final L2Clan leaderclan = ClanTable.getInstance().getClan(character.getAllyId());
		sm = new SystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
		sm.addString(leaderclan.getName());
		sm.addString(leaderclan.getLeaderName());
		character.sendPacket(sm);
		// clan count
		sm = new SystemMessage(SystemMessageId.AFFILIATED_CLANS_TOTAL_S1_CLANS);
		sm.addString("" + clancount);
		character.sendPacket(sm);
		// clan information
		sm = new SystemMessage(SystemMessageId.CLAN_INFO_HEAD);
		character.sendPacket(sm);
		for (final L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == character.getAllyId())
			{
				// clan name
				sm = new SystemMessage(SystemMessageId.CLAN_NAME_S1);
				sm.addString(clan.getName());
				character.sendPacket(sm);
				// clan leader name
				sm = new SystemMessage(SystemMessageId.CLAN_LEADER_S1);
				sm.addString(clan.getLeaderName());
				character.sendPacket(sm);
				// clan level
				sm = new SystemMessage(SystemMessageId.CLAN_LEVEL_S1);
				sm.addNumber(clan.getLevel());
				character.sendPacket(sm);
				// ---------
				sm = new SystemMessage(SystemMessageId.SEPARATOR);
				character.sendPacket(sm);
			}
		}
		// =========================
		sm = new SystemMessage(SystemMessageId.FOOTER_3);
		character.sendPacket(sm);
	}
	
	@Override
	public String getType()
	{
		return "[S] 7a AllyInfo";
	}
}
