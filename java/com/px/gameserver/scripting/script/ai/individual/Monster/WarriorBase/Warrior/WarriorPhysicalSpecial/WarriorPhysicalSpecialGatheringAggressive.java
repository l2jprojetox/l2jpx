package com.px.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;

public class WarriorPhysicalSpecialGatheringAggressive extends WarriorPhysicalSpecial
{
	public WarriorPhysicalSpecialGatheringAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorPhysicalSpecialGatheringAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21676,
		21699,
		21722,
		21745,
		21768,
		21791
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("3100", npc, null, 5000, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3100"))
		{
			broadcastScriptEvent(npc, 10019, npc.getObjectId(), 500);
		}
		return super.onTimer(name, npc, player);
	}
}