package com.l2jpx.gameserver.model.actor.instance;

import com.l2jpx.gameserver.enums.actors.ClassId;
import com.l2jpx.gameserver.enums.actors.ClassRace;
import com.l2jpx.gameserver.model.actor.template.NpcTemplate;

public final class VillageMasterDElf extends VillageMaster
{
	public VillageMasterDElf(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected final boolean checkVillageMasterRace(ClassId pclass)
	{
		if (pclass == null)
			return false;
		
		return pclass.getRace() == ClassRace.DARK_ELF;
	}
}