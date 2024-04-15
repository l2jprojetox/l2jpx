package com.px.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import com.px.commons.concurrent.ThreadPool;
import com.px.commons.random.Rnd;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.MoveToPawn;

public class TownPet extends Folk
{
	private ScheduledFuture<?> _aiTask;
	
	public TownPet(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setRunning();
		
		_aiTask = ThreadPool.scheduleAtFixedRate(new RandomWalkTask(), 1000, 10000);
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (!canInteract(player))
				player.getAI().setIntention(IntentionType.INTERACT, this);
			else
			{
				// Stop moving if we're already in interact range.
				if (player.isMoving() || player.isInCombat())
					player.getAI().setIntention(IntentionType.IDLE);
				
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		super.deleteMe();
	}
	
	public class RandomWalkTask implements Runnable
	{
		@Override
		public void run()
		{
			if (getSpawn() == null)
				return;
			
			getAI().setIntention(IntentionType.MOVE_TO, GeoEngine.getInstance().canMoveToTargetLoc(getX(), getY(), getZ(), getSpawn().getLocX() + Rnd.get(-75, 75), getSpawn().getLocY() + Rnd.get(-75, 75), getZ()));
		}
	}
}