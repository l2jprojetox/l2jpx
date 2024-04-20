package com.l2jpx.gameserver.skills.l2skills;

import com.l2jpx.commons.data.StatSet;

import com.l2jpx.gameserver.data.xml.NpcData;
import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.idfactory.IdFactory;
import com.l2jpx.gameserver.model.WorldObject;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.EffectPoint;
import com.l2jpx.gameserver.model.actor.template.NpcTemplate;
import com.l2jpx.gameserver.model.location.Location;
import com.l2jpx.gameserver.skills.L2Skill;

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