package com.px.gameserver.model.actor.ai.type;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.instance.Door;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.location.SpawnLocation;

public class DoorAI extends CreatureAI
{
	public DoorAI(Door door)
	{
		super(door);
	}
	
	@Override
	protected void onIntentionIdle()
	{
	}
	
	@Override
	protected void onIntentionActive()
	{
	}
	
	@Override
	protected void onIntentionRest()
	{
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, WorldObject target)
	{
	}
	
	@Override
	protected void onIntentionMoveTo(Location loc)
	{
	}
	
	@Override
	protected void onIntentionFollow(Creature target)
	{
	}
	
	@Override
	protected void onIntentionPickUp(WorldObject item)
	{
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
	
	@Override
	protected void onEvtStunned(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtSleeping(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtRooted(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
	}
	
	@Override
	protected void onEvtArrivedBlocked(SpawnLocation loc)
	{
	}
	
	@Override
	protected void onEvtCancel()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
}