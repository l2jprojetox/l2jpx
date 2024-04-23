package com.l2jpx.gameserver.model.actor.move;

import com.l2jpx.commons.random.Rnd;

import com.l2jpx.gameserver.enums.IntentionType;
import com.l2jpx.gameserver.enums.actors.MoveType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.model.location.Location;

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
		
		if (_actor.getAI().getCurrentIntention().getType() != IntentionType.ACTIVE && _actor.getAI().getCurrentIntention().getType() != IntentionType.FOLLOW)
			return;
		
		if (_actor.isMoving() || _actor.isDead() || _actor.isMovementDisabled())
			return;
		
		final int ownerX = owner.getX();
		final int ownerY = owner.getY();
		final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());
		
		final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
		final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
		
		maybeMoveToLocation(new Location(targetX, targetY, _actor.getZ()), 0, true, false);
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
			_actor.getAI().tryToActive();
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
			_actor.getAI().tryToActive();
			return;
		}
		
		final Location destination = target.getPosition().clone();
		final int realOffset = (int) (offset + _actor.getCollisionRadius() + target.getCollisionRadius());
		
		// Don't bother moving if already in radius.
		if ((getMoveType() == MoveType.GROUND) ? _actor.isIn2DRadius(destination, realOffset) : _actor.isIn3DRadius(destination, realOffset))
			return;
		
		_pawn = null;
		_offset = 0;
		
		moveToLocation(destination, true);
	}
}