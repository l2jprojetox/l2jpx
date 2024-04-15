package com.px.gameserver.model.actor.instance;

import com.px.gameserver.enums.actors.ClassId;
import com.px.gameserver.enums.actors.ClassRace;
import com.px.gameserver.enums.actors.ClassType;
import com.px.gameserver.model.actor.template.NpcTemplate;

public final class VillageMasterFighter extends VillageMaster
{
	public VillageMasterFighter(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected final boolean checkVillageMasterRace(ClassId pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.getRace() == ClassRace.HUMAN || pclass.getRace() == ClassRace.ELF;
	}
	
	@Override
	protected final boolean checkVillageMasterTeachType(ClassId pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.getType() == ClassType.FIGHTER;
	}
}