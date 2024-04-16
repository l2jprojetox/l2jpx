package com.px.gameserver.scripting.task;

import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.pledge.ClanMember;
import com.px.gameserver.scripting.ScheduledQuest;

public class ClanLeaderTransfer extends ScheduledQuest
{
	public ClanLeaderTransfer()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getNewLeaderId() <= 0)
				continue;
			
			final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
			if (member == null)
			{
				clan.setNewLeaderId(0, true);
				continue;
			}
			
			clan.setNewLeader(member);
		}
	}
	
	@Override
	public final void onEnd()
	{
	}
}