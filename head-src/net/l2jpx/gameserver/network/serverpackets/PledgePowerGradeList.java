package net.l2jpx.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.Set;

import net.l2jpx.gameserver.model.L2ClanMember;

/**
 * sample 0000: 9c c10c0000 48 00 61 00 6d 00 62 00 75 00 72 .....H.a.m.b.u.r 0010: 00 67 00 00 00 00000000 00000000 00000000 00000000 00000000 00000000 00 00 00000000 ... format dd ??
 * @author ReynalDev
 */
public class PledgePowerGradeList extends L2GameServerPacket
{
	private Set<Integer> ranks;
	private Collection<L2ClanMember> members;
	
	public PledgePowerGradeList(Set<Integer> ranks, Collection<L2ClanMember> members)
	{
		this.ranks = ranks;
		this.members = members;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0x3b);
		writeD(ranks.size());
		for (int rank: ranks)
		{
			writeD(rank);
			writeD((int) members.stream().filter(member -> member.getPowerGrade() == rank).count());
		}
		
	}
	
	@Override
	public String getType()
	{
		return "[S] FE:3B PledgePowerGradeList";
	}
	
}
