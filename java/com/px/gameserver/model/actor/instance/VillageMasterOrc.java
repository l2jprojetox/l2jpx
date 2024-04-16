package com.px.gameserver.model.actor.instance;

import com.px.gameserver.enums.actors.ClassId;
import com.px.gameserver.enums.actors.ClassRace;
import com.px.gameserver.model.actor.template.NpcTemplate;

public final class VillageMasterOrc extends VillageMaster
{
	public VillageMasterOrc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected final boolean checkVillageMasterRace(ClassId pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.getRace() == ClassRace.ORC;
	}
}