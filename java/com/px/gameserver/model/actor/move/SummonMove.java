package com.px.gameserver.model.actor.move;

import com.px.commons.geometry.Circle;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.actors.MoveType;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.model.location.Location;

public class SummonMove extends CreatureMove<Summon>
{
	private static final int AVOID_RADIUS = 70;
	
	public SummonMove(Summon actor)
	{
		super(actor);
	}
	
	@Override
	public void avoidAttack(Creature attacker)
	{
		final Player owner = _actor.getOwner();
		
		if (owner == null || owner == attacker || !owner.isIn3DRadius(_actor, 2 * AVOID_RADIUS) || !owner.isInCombat())
			return;
		
		if (_actor.getAI().getCurrentIntention().getType() != IntentionType.IDLE && _actor.getAI().getCurrentIntention().getType() != IntentionType.FOLLOW)
			return;
		
		if (_actor.isMoving() || _actor.isDead() || _actor.isMovementDisabled())
			return;
		
		final Circle circle = new Circle(owner.getX(), owner.getY(), AVOID_RADIUS);
		final Location fleeLoc = circle.getRandomEquidistantPoint(12, owner.getZ());
		
		_actor.getAI().tryToMoveTo(fleeLoc, null);
	}
	
	@Override
	protected void offensiveFollowTask(Creature target, int offset)
	{
		// No follow task, return.
		if (_followTask == null)
			return;
		
		// Invalid pawn to follow, or the pawn isn't registered on knownlist.
		if (!_actor.knows(target))
		{
			_actor.getAI().setFollowStatus(false);
			_actor.getAI().tryToIdle();
			return;
		}
		
		final Location destination = target.getPosition().clone();
		final int realOffset = (int) (offset + _actor.getCollisionRadius() + target.getCollisionRadius());
		
		// Don't bother moving if already in radius.
		if ((getMoveType() == MoveType.GROUND) ? _actor.isIn2DRadius(destination, realOffset) : _actor.isIn3DRadius(destination, realOffset))
			return;
		
		_pawn = target;
		_offset = offset;
		
		moveToLocation(destination, true);
	}
	
	@Override
	protected void friendlyFollowTask(Creature target, int offset)
	{
		// No follow task, return.
		if (_followTask == null)
			return;
		
		// Invalid pawn to follow, or the pawn isn't registered on knownlist.
		if (!_actor.knows(target))
		{
			_actor.getAI().setFollowStatus(false);
			_actor.getAI().tryToIdle();
			return;
		}
		
		final Location destination = target.getPosition().clone();
		final int realOffset = (int) (offset + _actor.getCollisionRadius() + target.getCollisionRadius());
		
		// Don't bother moving if already in radius.
		if ((getMoveType() == MoveType.GROUND) ? _actor.isIn2DRadius(destination, realOffset) : _actor.isIn3DRadius(destination, realOffset))
			return;
		
		_pawn = null;
		_offset = 0;
		
		if (target.getActingPlayer() != _actor.getOwner())
		{
			_actor.tryToPassBoatEntrance(destination);
			return;
		}
		moveToLocation(destination, true);
	}
}