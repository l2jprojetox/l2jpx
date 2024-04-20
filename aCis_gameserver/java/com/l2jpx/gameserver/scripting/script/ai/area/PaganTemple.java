package com.l2jpx.gameserver.scripting.script.ai.area;

import com.l2jpx.gameserver.model.actor.Npc;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.scripting.script.ai.AttackableAIScript;

/**
 * Those monsters don't attack at sight players owning itemId 8064, 8065 or 8067.
 */
public class PaganTemple extends AttackableAIScript
{
	public PaganTemple()
	{
		super("ai/area");
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