package com.px.gameserver.model.actor.ai.type;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.instance.Door;

public class DoorAI extends CreatureAI<Door>
{
	public DoorAI(Door door)
	{
		super(door);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtFinishedAttack()
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
	}
	
	@Override
	protected void onEvtArrivedBlocked()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
}