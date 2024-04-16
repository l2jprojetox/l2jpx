package com.px.gameserver.scripting.script.ai.boss.benom;

import com.px.gameserver.data.cache.HtmCache;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.residence.castle.Castle;
import com.px.gameserver.network.NpcStringId;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;

public class DungeonTeleporter extends DefaultNpc
{
	public DungeonTeleporter()
	{
		super("ai/boss/benom");
		addFirstTalkId(35506);
		addTalkId(35506);
	}
	
	public DungeonTeleporter(String descr)
	{
		super(descr);
		addFirstTalkId(35506);
		addTalkId(35506);
	}
	
	protected final int[] _npcIds =
	{
		35506 // rune_massymore_doorman
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 1;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return HtmCache.getInstance().getHtm("data/html/doormen/35506.htm");
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final Castle castle = npc.getCastle();
		
		if (castle.getSiege().isInProgress())
			return HtmCache.getInstance().getHtm("data/html/doormen/35506-2.htm");
		
		talker.teleportTo(12589, -49044, -3008, 0);
		
		return super.onTalk(npc, talker);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10101)
			npc.broadcastNpcShout(NpcStringId.ID_1010632);
	}
}