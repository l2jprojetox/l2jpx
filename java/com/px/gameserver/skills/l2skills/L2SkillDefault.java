package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.skills.L2Skill;

public class L2SkillDefault extends L2Skill
{
	public L2SkillDefault(StatSet set)
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