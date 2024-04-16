package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.data.xml.NpcData;
import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.EffectPoint;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.skills.L2Skill;

public final class L2SkillSignet extends L2Skill
{
	public final int effectNpcId;
	public final int effectId;
	
	public L2SkillSignet(StatSet set)
	{
		super(set);
		effectNpcId = set.getInteger("effectNpcId", -1);
		effectId = set.getInteger("effectId", -1);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(effectNpcId);
		if (template == null)
			return;
		
		final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, caster);
		effectPoint.getStatus().setMaxHpMp();
		
		Location worldPosition = null;
		if (caster instanceof Player && getTargetType() == SkillTargetType.GROUND)
			worldPosition = ((Player) caster).getCast().getSignetLocation();
		
		getEffects(caster, effectPoint);
		
		effectPoint.setInvul(true);
		effectPoint.spawnMe((worldPosition != null) ? worldPosition : caster.getPosition());
	}
}