package com.px.gameserver.skills.l2skills;

import com.px.commons.util.StatsSet;

import com.px.gameserver.data.xml.NpcData;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.EffectPoint;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.location.Location;

public final class L2SkillSignet extends L2Skill
{
	private final int _effectNpcId;
	public int effectId;
	
	public L2SkillSignet(StatsSet set)
	{
		super(set);
		_effectNpcId = set.getInteger("effectNpcId", -1);
		effectId = set.getInteger("effectId", -1);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		NpcTemplate template = NpcData.getInstance().getTemplate(_effectNpcId);
		EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, caster);
		effectPoint.setCurrentHp(effectPoint.getMaxHp());
		effectPoint.setCurrentMp(effectPoint.getMaxMp());
		
		Location worldPosition = null;
		if (caster instanceof Player && getTargetType() == L2Skill.SkillTargetType.TARGET_GROUND)
			worldPosition = ((Player) caster).getCurrentSkillWorldPosition();
		
		getEffects(caster, effectPoint);
		
		effectPoint.setIsInvul(true);
		effectPoint.spawnMe((worldPosition != null) ? worldPosition : caster.getPosition());
	}
}