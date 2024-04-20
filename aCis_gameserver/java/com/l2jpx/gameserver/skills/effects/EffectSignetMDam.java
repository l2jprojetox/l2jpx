package com.l2jpx.gameserver.skills.effects;

import java.util.List;

import com.l2jpx.gameserver.data.xml.NpcData;
import com.l2jpx.gameserver.enums.ZoneId;
import com.l2jpx.gameserver.enums.items.ShotType;
import com.l2jpx.gameserver.enums.skills.EffectType;
import com.l2jpx.gameserver.enums.skills.ShieldDefense;
import com.l2jpx.gameserver.enums.skills.SkillTargetType;
import com.l2jpx.gameserver.idfactory.IdFactory;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.Summon;
import com.l2jpx.gameserver.model.actor.instance.Door;
import com.l2jpx.gameserver.model.actor.instance.EffectPoint;
import com.l2jpx.gameserver.model.actor.template.NpcTemplate;
import com.l2jpx.gameserver.model.location.Location;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.MagicSkillLaunched;
import com.l2jpx.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jpx.gameserver.skills.AbstractEffect;
import com.l2jpx.gameserver.skills.Formulas;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.l2skills.L2SkillSignetCasttime;

public class EffectSignetMDam extends AbstractEffect
{
	private EffectPoint _actor;
	
	public EffectSignetMDam(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_GROUND;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(_skill instanceof L2SkillSignetCasttime))
			return false;
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(((L2SkillSignetCasttime) getSkill()).effectNpcId);
		if (template == null)
			return false;
		
		final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, getEffector());
		effectPoint.getStatus().setMaxHpMp();
		
		Location worldPosition = null;
		if (getEffector() instanceof Player && getSkill().getTargetType() == SkillTargetType.GROUND)
			worldPosition = ((Player) getEffector()).getCast().getSignetLocation();
		
		effectPoint.setInvul(true);
		effectPoint.spawnMe((worldPosition != null) ? worldPosition : getEffector().getPosition());
		
		_actor = effectPoint;
		return true;
		
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getCount() >= getTemplate().getCounter() - 2)
			return true; // do nothing first 2 times
			
		final Player caster = (Player) getEffector();
		final int mpConsume = getSkill().getMpConsume();
		
		if (mpConsume > caster.getStatus().getMp())
		{
			caster.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		caster.getStatus().reduceMp(mpConsume);
		
		final List<Creature> list = _actor.getKnownTypeInRadius(Creature.class, _skill.getSkillRadius(), creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE));
		if (list.isEmpty())
			return true;
		
		final Creature[] targets = list.toArray(new Creature[list.size()]);
		for (Creature target : targets)
		{
			final boolean isCrit = Formulas.calcMCrit(caster, target, getSkill());
			final ShieldDefense sDef = Formulas.calcShldUse(caster, target, getSkill(), false);
			final boolean sps = caster.isChargedShot(ShotType.SPIRITSHOT);
			final boolean bsps = caster.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
			final int damage = (int) Formulas.calcMagicDam(caster, target, getSkill(), sDef, sps, bsps, isCrit);
			
			if (target instanceof Summon)
				target.getStatus().broadcastStatusUpdate();
			
			if (damage > 0)
			{
				// Manage cast break of the target (calculating rate, sending message...)
				Formulas.calcCastBreak(target, damage);
				
				caster.sendDamageMessage(target, damage, isCrit, false, false);
				target.reduceCurrentHp(damage, caster, getSkill());
			}
			
			_actor.broadcastPacket(new MagicSkillUse(_actor, target, _skill.getId(), _skill.getLevel(), 0, 0));
		}
		_actor.broadcastPacket(new MagicSkillLaunched(_actor, _skill, targets));
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}