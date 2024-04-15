package com.px.gameserver.scripting.scripts.ai.group;

import com.px.commons.random.Rnd;

import com.px.Config;
import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * A fleeing NPC.<br>
 * <br>
 * His behavior is to always flee, and never attack.
 */
public class FleeingNPCs extends L2AttackableAIScript
{
	public FleeingNPCs()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20432);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		// Calculate random coords.
		final int rndX = npc.getX() + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE);
		final int rndY = npc.getY() + Rnd.get(-Config.MAX_DRIFT_RANGE, Config.MAX_DRIFT_RANGE);
		
		// Wait the NPC to be immobile to move him again. Also check destination point.
		if (!npc.isMoving() && GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), rndX, rndY, npc.getZ()))
			npc.getAI().setIntention(IntentionType.MOVE_TO, new Location(rndX, rndY, npc.getZ()));
		
		return null;
	}
}