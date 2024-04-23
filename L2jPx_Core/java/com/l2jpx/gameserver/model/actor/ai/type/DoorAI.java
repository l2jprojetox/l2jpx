package com.l2jpx.gameserver.model.actor.ai.type;

import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.instance.Door;

public class DoorAI extends CreatureAI
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