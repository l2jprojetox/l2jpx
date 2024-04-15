package com.px.gameserver.scripting.scripts.ai.group;

import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * Those monsters don't attack at sight players owning itemId 8064, 8065 or 8067.
 */
public class GatekeeperZombies extends L2AttackableAIScript
{
	public GatekeeperZombies()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAggroRangeEnterId(22136);
	}
	
	@Override
	public String onAggro(Npc npc, Player player, boolean isPet)
	{
		if (player.getInventory().hasAtLeastOneItem(8064, 8065, 8067))
			return null;
		
		return super.onAggro(npc, player, isPet);
	}
}