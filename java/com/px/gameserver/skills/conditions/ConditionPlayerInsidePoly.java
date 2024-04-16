package com.px.gameserver.skills.conditions;

import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.item.kind.Item;
import com.px.gameserver.model.zone.form.ZoneNPoly;
import com.px.gameserver.skills.L2Skill;

public class ConditionPlayerInsidePoly extends Condition
{
	private final ZoneNPoly _zoneNPoly;
	private final boolean _checkInside;
	
	public ConditionPlayerInsidePoly(ZoneNPoly zoneNPoly, boolean checkInside)
	{
		_zoneNPoly = zoneNPoly;
		_checkInside = checkInside;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		final boolean isInside = _zoneNPoly.isInsideZone(effector.getX(), effector.getY(), effector.getZ());
		return _checkInside ? isInside : !isInside;
	}
}
