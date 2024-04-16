package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.spawn.Spawn;
import com.px.gameserver.skills.L2Skill;

public class L2SkillSpawn extends L2Skill
{
	private final int _npcId;
	private final int _despawnDelay;
	
	public L2SkillSpawn(StatSet set)
	{
		super(set);
		
		_npcId = set.getInteger("npcId", 0);
		_despawnDelay = set.getInteger("despawnDelay", 0);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		try
		{
			// Create spawn.
			final Spawn spawn = new Spawn(_npcId);
			spawn.setLoc(caster.getPosition());
			
			// Spawn NPC.
			final Npc npc = spawn.doSpawn(false);
			if (_despawnDelay > 0)
				npc.scheduleDespawn(_despawnDelay);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to initialize a spawn.", e);
		}
	}
}