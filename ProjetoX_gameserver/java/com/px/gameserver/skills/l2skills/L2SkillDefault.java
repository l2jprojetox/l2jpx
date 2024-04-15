package com.px.gameserver.skills.l2skills;

import com.px.commons.util.StatsSet;

import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.network.serverpackets.ActionFailed;

public class L2SkillDefault extends L2Skill
{
	public L2SkillDefault(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		caster.sendPacket(ActionFailed.STATIC_PACKET);
		caster.sendMessage("Skill " + getId() + " [" + getSkillType() + "] isn't implemented.");
	}
}