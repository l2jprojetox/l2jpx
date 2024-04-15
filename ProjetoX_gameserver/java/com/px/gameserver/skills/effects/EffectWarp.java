package com.px.gameserver.skills.effects;

import com.px.commons.math.MathUtil;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.enums.skills.FlyType;
import com.px.gameserver.enums.skills.L2EffectType;
import com.px.gameserver.geoengine.GeoEngine;
import com.px.gameserver.model.L2Effect;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.network.serverpackets.FlyToLocation;
import com.px.gameserver.network.serverpackets.ValidateLocation;
import com.px.gameserver.skills.Env;

/**
 * This class handles warp effects, disappear and quickly turn up in a near location. If geodata enabled and an object is between initial and final point, flight is stopped just before colliding with object. Flight course and radius are set as skill properties (flyCourse and flyRadius):
 * <li>Fly Radius means the distance between starting point and final point, it must be an integer.</li>
 * <li>Fly Course means the movement direction: imagine a compass above player's head, making north player's heading. So if fly course is 180, player will go backwards (good for blink, e.g.). By the way, if flyCourse = 360 or 0, player will be moved in in front of him. <br>
 * <br>
 * If target is effector, put in XML self = "1". This will make _actor = getEffector(). This, combined with target type, allows more complex actions like flying target's backwards or player's backwards.<br>
 * <br>
 * @author House
 */
public class EffectWarp extends L2Effect
{
	private int x, y, z;
	private Creature _actor;
	
	public EffectWarp(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.WARP;
	}
	
	@Override
	public boolean onStart()
	{
		_actor = isSelfEffect() ? getEffector() : getEffected();
		
		if (_actor.isMovementDisabled())
			return false;
		
		int _radius = getSkill().getFlyRadius();
		
		double angle = MathUtil.convertHeadingToDegree(_actor.getHeading());
		double radian = Math.toRadians(angle);
		double course = Math.toRadians(getSkill().getFlyCourse());
		
		int x1 = (int) (Math.cos(Math.PI + radian + course) * _radius);
		int y1 = (int) (Math.sin(Math.PI + radian + course) * _radius);
		
		x = _actor.getX() + x1;
		y = _actor.getY() + y1;
		z = _actor.getZ();
		
		Location destiny = GeoEngine.getInstance().canMoveToTargetLoc(_actor.getX(), _actor.getY(), _actor.getZ(), x, y, z);
		x = destiny.getX();
		y = destiny.getY();
		z = destiny.getZ();
		
		// TODO: check if this AI intention is retail-like. This stops player's previous movement
		_actor.getAI().setIntention(IntentionType.IDLE);
		
		_actor.broadcastPacket(new FlyToLocation(_actor, x, y, z, FlyType.DUMMY));
		_actor.abortAttack();
		_actor.abortCast();
		
		_actor.setXYZ(x, y, z);
		_actor.broadcastPacket(new ValidateLocation(_actor));
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}