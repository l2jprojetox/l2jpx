package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class L2SkillDrain extends L2Skill
{
	private final float _absorbPart;
	private final int _absorbAbs;
	
	public L2SkillDrain(StatSet set)
	{
		super(set);
		
		_absorbPart = set.getFloat("absorbPart", 0.f);
		_absorbAbs = set.getInteger("absorbAbs", 0);
	}
	
	@Override
	public void useSkill(Creature activeChar, WorldObject[] targets)
	{
		if (activeChar.isAlikeDead())
			return;
		
		final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		final boolean isPlayable = activeChar instanceof Playable;
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.CORPSE_MOB)
				continue;
			
			if (activeChar != target && target.isInvul())
				continue; // No effect on invulnerable chars unless they cast it themselves.
				
			final boolean isCrit = Formulas.calcMCrit(activeChar, target, this);
			final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, this, false);
			final int damage = (int) Formulas.calcMagicDam(activeChar, target, this, sDef, sps, bsps, isCrit);
			
			if (damage > 0)
			{
				int targetCp = 0;
				if (target instanceof Player)
					targetCp = (int) ((Player) target).getStatus().getCp();
				
				final int targetHp = (int) target.getStatus().getHp();
				
				int drain = 0;
				if (isPlayable && targetCp > 0)
				{
					if (damage < targetCp)
						drain = 0;
					else
						drain = damage - targetCp;
				}
				else if (damage > targetHp)
					drain = targetHp;
				else
					drain = damage;
				
				activeChar.getStatus().addHp(_absorbAbs + _absorbPart * drain);
				
				// That section is launched for drain skills made on ALIVE targets.
				if (!target.isDead() || getTargetType() != SkillTargetType.CORPSE_MOB)
				{
					// Manage cast break of the target (calculating rate, sending message...)
					Formulas.calcCastBreak(target, damage);
					
					activeChar.sendDamageMessage(target, damage, isCrit, false, false);
					
					if (hasEffects() && getTargetType() != SkillTargetType.CORPSE_MOB)
					{
						// ignoring vengance-like reflections
						if ((Formulas.calcSkillReflect(target, this) & Formulas.SKILL_REFLECT_SUCCEED) > 0)
						{
							activeChar.stopSkillEffects(getId());
							getEffects(target, activeChar);
						}
						else
						{
							// activate attacked effects, if any
							target.stopSkillEffects(getId());
							if (Formulas.calcSkillSuccess(activeChar, target, this, sDef, bsps))
								getEffects(activeChar, target);
							else
								activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(getId()));
						}
					}
					target.reduceCurrentHp(damage, activeChar, this);
				}
			}
		}
		
		if (hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			getEffectsSelf(activeChar);
		}
		
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
	}
	
	public float getAbsorbPart()
	{
		return _absorbPart;
	}
	
	public int getAbsorbAbs()
	{
		return _absorbAbs;
	}
}