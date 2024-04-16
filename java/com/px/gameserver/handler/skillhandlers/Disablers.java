package com.px.gameserver.handler.skillhandlers;

import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillTargetType;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.enums.skills.Stats;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.Summon;
import com.px.gameserver.model.actor.instance.SiegeSummon;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;

public class Disablers implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.STUN,
		SkillType.ROOT,
		SkillType.SLEEP,
		SkillType.CONFUSION,
		SkillType.AGGDAMAGE,
		SkillType.AGGREDUCE,
		SkillType.AGGREDUCE_CHAR,
		SkillType.AGGREMOVE,
		SkillType.MUTE,
		SkillType.FAKE_DEATH,
		SkillType.NEGATE,
		SkillType.CANCEL_DEBUFF,
		SkillType.PARALYZE,
		SkillType.ERASE,
		SkillType.BETRAY
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		final SkillType type = skill.getSkillType();
		
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			Creature target = (Creature) obj;
			if (target.isDead() || (target.isInvul() && !target.isParalyzed())) // bypass if target is dead or invul (excluding invul from Petrification)
				continue;
			
			if (skill.isOffensive() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) != null)
				continue;
			
			final ShieldDefense sDef = Formulas.calcShldUse(activeChar, target, skill, false);
			
			switch (type)
			{
				case BETRAY:
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						skill.getEffects(activeChar, target, sDef, bsps);
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					break;
				
				case FAKE_DEATH:
					skill.getEffects(activeChar, target, sDef, bsps);
					break;
				
				case ROOT:
				case STUN:
					if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						target = activeChar;
					
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						skill.getEffects(activeChar, target, sDef, bsps);
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
					break;
				
				case SLEEP:
				case PARALYZE: // use same as root for now
					if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						target = activeChar;
					
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						skill.getEffects(activeChar, target, sDef, bsps);
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
					break;
				
				case MUTE:
					if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						target = activeChar;
					
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
					{
						// stop same type effect if available
						for (AbstractEffect effect : target.getAllEffects())
						{
							if (effect.getTemplate().getStackOrder() == 99)
								continue;
							
							if (effect.getSkill().getSkillType() == type)
								effect.exit();
						}
						skill.getEffects(activeChar, target, sDef, bsps);
					}
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill.getId()));
					break;
				
				case CONFUSION:
					// do nothing if not on mob
					if (target instanceof Attackable)
					{
						if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						{
							for (AbstractEffect effect : target.getAllEffects())
							{
								if (effect.getTemplate().getStackOrder() == 99)
									continue;
								
								if (effect.getSkill().getSkillType() == type)
									effect.exit();
							}
							skill.getEffects(activeChar, target, sDef, bsps);
						}
						else if (activeChar instanceof Player)
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					}
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET));
					break;
				
				case AGGDAMAGE:
					if (target instanceof Attackable)
						target.getAI().notifyEvent(AiEventType.AGGRESSION, activeChar, (int) (skill.getPower() / (target.getStatus().getLevel() + 7) * 150));
					
					skill.getEffects(activeChar, target, sDef, bsps);
					break;
				
				case AGGREDUCE:
					// TODO these skills needs to be rechecked
					if (target instanceof Attackable)
					{
						skill.getEffects(activeChar, target, sDef, bsps);
						
						if (skill.getPower() > 0)
							((Attackable) target).getAI().getAggroList().reduceAllHate((int) skill.getPower());
						else
						{
							final double hate = ((Attackable) target).getAI().getAggroList().getHate(activeChar);
							final double diff = hate - target.getStatus().calcStat(Stats.AGGRESSION, hate, target, skill);
							if (diff > 0)
								((Attackable) target).getAI().getAggroList().reduceAllHate((int) diff);
						}
					}
					break;
				
				case AGGREDUCE_CHAR:
					// TODO these skills need to be rechecked
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
					{
						if (target instanceof Attackable)
						{
							((Attackable) target).getAI().getAggroList().stopHate(activeChar);
							((Attackable) target).getAI().getHateList().stopHate(activeChar);
						}
						
						skill.getEffects(activeChar, target, sDef, bsps);
					}
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					break;
				
				case AGGREMOVE:
					// TODO these skills needs to be rechecked
					if (target instanceof Attackable && !target.isRaidRelated())
					{
						if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
						{
							if (skill.getTargetType() == SkillTargetType.UNDEAD)
							{
								if (target.isUndead())
								{
									((Attackable) target).getAI().getAggroList().cleanAllHate();
									((Attackable) target).getAI().getHateList().cleanAllHate();
								}
							}
							else
							{
								((Attackable) target).getAI().getAggroList().cleanAllHate();
								((Attackable) target).getAI().getHateList().cleanAllHate();
							}
						}
						else if (activeChar instanceof Player)
							activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					}
					break;
				
				case ERASE:
					if (Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps))
					{
						// Doesn't affect anything, except Summons which aren't Siege Summons.
						if (target instanceof Summon && !(target instanceof SiegeSummon))
						{
							final Player summonOwner = target.getActingPlayer();
							if (summonOwner != null)
							{
								((Summon) target).unSummon(summonOwner);
								
								summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
							}
						}
					}
					else if (activeChar instanceof Player)
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					break;
				
				case CANCEL_DEBUFF:
					final AbstractEffect[] effects = target.getAllEffects();
					if (effects == null || effects.length == 0)
						break;
					
					int count = (skill.getMaxNegatedEffects() > 0) ? 0 : -2;
					for (AbstractEffect effect : effects)
					{
						if (!effect.getSkill().isDebuff() || !effect.getSkill().canBeDispeled() || effect.getTemplate().getStackOrder() == 99)
							continue;
						
						effect.exit();
						
						if (count > -1)
						{
							count++;
							if (count >= skill.getMaxNegatedEffects())
								break;
						}
					}
					break;
				
				case NEGATE:
					if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						target = activeChar;
					
					// Skills with negateId (skillId)
					if (skill.getNegateId().length != 0)
					{
						for (int id : skill.getNegateId())
						{
							if (id != 0)
								target.stopSkillEffects(id);
						}
					}
					// All others negate type skills
					else
					{
						for (AbstractEffect effect : target.getAllEffects())
						{
							if (effect.getTemplate().getStackOrder() == 99)
								continue;
							
							final L2Skill effectSkill = effect.getSkill();
							for (SkillType skillType : skill.getNegateStats())
							{
								// If power is -1 the effect is always removed without lvl check
								if (skill.getNegateLvl() == -1)
								{
									if (effectSkill.getSkillType() == skillType || (effectSkill.getEffectType() != null && effectSkill.getEffectType() == skillType))
										effect.exit();
								}
								// Remove the effect according to its power.
								else
								{
									if (effectSkill.getEffectType() != null && effectSkill.getEffectAbnormalLvl() >= 0)
									{
										if (effectSkill.getEffectType() == skillType && effectSkill.getEffectAbnormalLvl() <= skill.getNegateLvl())
											effect.exit();
									}
									else if (effectSkill.getSkillType() == skillType && effectSkill.getAbnormalLvl() <= skill.getNegateLvl())
										effect.exit();
								}
							}
						}
					}
					skill.getEffects(activeChar, target, sDef, bsps);
					break;
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}