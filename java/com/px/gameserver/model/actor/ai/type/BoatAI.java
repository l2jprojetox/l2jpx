package com.px.gameserver.model.actor.ai.type;

import com.px.gameserver.model.actor.Boat;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.serverpackets.VehicleDeparture;

public class BoatAI extends CreatureAI<Boat>
{
	public BoatAI(Boat boat)
	{
		super(boat);
	}
	
	@Override
	public void describeStateToPlayer(Player player)
	{
		if (_actor.isMoving())
			player.sendPacket(new VehicleDeparture(_actor));
	}
	
	@Override
	public void onEvtAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
		_actor.getMove().onArrival();
	}
	
	@Override
	protected void onEvtDead()
	{
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
	}
}