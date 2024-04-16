package com.px.gameserver.model.zone.type;

import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, used for castle's artifacts.<br>
 * <br>
 * A check forces players to cast on this type of zone, to avoid hiding spots or exploits.
 */
public class PrayerZone extends ZoneType
{
	public PrayerZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.CAST_ON_ARTIFACT, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.CAST_ON_ARTIFACT, false);
	}
}