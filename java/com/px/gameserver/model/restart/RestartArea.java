package com.px.gameserver.model.restart;

import java.util.EnumMap;

import com.px.gameserver.enums.actors.ClassRace;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.zone.form.ZoneNPoly;

/**
 * A zone used as restart point when dead or scrolling out.<br>
 * <br>
 * It priors and overrides behavior from {@link RestartPoint}, and enforce the restart point based on {@link ClassRace}.
 */
public class RestartArea
{
	private final ZoneNPoly _zone;
	private final EnumMap<ClassRace, String> _classRestrictions;
	
	public RestartArea(ZoneNPoly zone, EnumMap<ClassRace, String> classRestrictions)
	{
		_zone = zone;
		_classRestrictions = classRestrictions;
	}
	
	public ZoneNPoly getZone()
	{
		return _zone;
	}
	
	public String getClassRestriction(Player player)
	{
		return _classRestrictions.get(player.getTemplate().getRace());
	}
}