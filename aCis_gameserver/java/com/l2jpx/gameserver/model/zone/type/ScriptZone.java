package com.l2jpx.gameserver.model.zone.type;

import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, used for quests and custom scripts.
 */
public class ScriptZone extends ZoneType
{
	public ScriptZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.SCRIPT, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.SCRIPT, false);
	}
}