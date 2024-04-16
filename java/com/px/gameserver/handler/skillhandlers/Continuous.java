package com.px.gameserver.handler.skillhandlers;

import com.px.commons.util.ArraysUtil;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.data.manager.DuelManager;
import com.px.gameserver.enums.AiEventType;
import com.px.gameserver.enums.items.ShotType;
import com.px.gameserver.enums.skills.EffectType;
import com.px.gameserver.enums.skills.ShieldDefense;
import com.px.gameserver.enums.skills.SkillType;
import com.px.gameserver.handler.ISkillHandler;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Attackable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Playable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.ai.type.PlayableAI;
import com.px.gameserver.model.actor.instance.ClanHallManagerNpc;
import com.px.gameserver.model.item.instance.ItemInstance;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.AbstractEffect;
import com.px.gameserver.skills.Formulas;
import com.px.gameserver.skills.L2Skill;
import com.px.gameserver.skills.effects.EffectFear;

public class Continuous implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.BUFF,
		SkillType.DEBUFF,
		SkillType.DOT,
		SkillType.MDOT,
		SkillType.POISON,
		SkillType.BLEED,
		SkillType.HOT,
		SkillType.MPHOT,
		SkillType.FEAR,
		SkillType.CONT,
		SkillType.WEAKNESS,
		SkillType.REFLECT,
		SkillType.AGGDEBUFF,
		SkillType.FUSION
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets, ItemInstance itemInstance)
	{
		final Player player = activeChar.getActingPlayer();
		
		if (skill.getEffectId() != 0)
		{
			L2Skill sk = SkillTable.getInstance().getInfo(skill.getEffectId(), skill.getEffectLvl() == 0 ? 1 : skill.getEffectLvl());
			if (sk != null)
				skill = sk;
		}
		
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			Creature target = ((Creature) obj);
			if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
				target = activeChar;
			
			switch (skill.getSkillType())
			{
				case BUFF:
					// Target under buff immunity.
					if (target.getFirstEffect(EffectType.BLOCK_BUFF) != null)
						continue;
					
					// Player holding a cursed weapon can't be buffed and can't buff
					if (!(activeChar instanceof ClanHallManagerNpc) && target != activeChar)
					{
						if (target instanceof Player)
						{
							if (((Player) target).isCursedWeaponEquipped())
								continue;
						}
						else if (player != null && player.isCursedWeaponEquipped())
							continue;
					}
					break;
				
				case HOT:
				case MPHOT:
					if (activeChar.isInvul())
						continue;
					break;
				case FEAR:
					if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId()))
						continue;
			}
			
			// Target under debuff immunity.
			if (skill.isOffensive() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) != null)
				continue;
			
			boolean acted = true;
			ShieldDefense sDef = ShieldDefense.FAILED;
			
			if (skill.isOffensive() || skill.isDebuff())
			{
				sDef = Formulas.calcShldUse(activeChar, target, skill, false);
				acted = Formulas.calcSkillSuccess(activeChar, target, skill, sDef, bsps);
			}
			
			if (acted)
			{
				// TODO Not necessary
				if (skill.isToggle())
					target.stopSkillEffects(skill.getId());
					
				// if this is a debuff let the duel manager know about it so the debuff
				// can be removed after the duel (player & target must be in the same duel)
				if (target instanceof Player && ((Player) target).isInDuel() && (skill.getSkillType() == SkillType.DEBUFF || skill.getSkillType() == SkillType.BUFF) && player != null && player.getDuelId() == ((Player) target).getDuelId())
				{
					for (AbstractEffect buff : skill.getEffects(activeChar, target, sDef, bsps))
						if (buff != null)
							DuelManager.getInstance().onBuff(((Player) target), buff);
				}
				else
					skill.getEffects(activeChar, target, sDef, bsps);
				
				if (skill.getSkillType() == SkillType.AGGDEBUFF)
				{
					if (target instanceof Attackable)
						target.getAI().notifyEvent(AiEventType.AGGRESSION, activeChar, (int) skill.getPower());
					else if (target instanceof Playable)
					{
						if (target.getTarget() == activeChar)
							((PlayableAI<?>) target.getAI()).tryToAttack(activeChar, false, false);
						else
							target.setTarget(activeChar);
					}
				}
			}
			else
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
			
			// Possibility of a lethal strike
			Formulas.calcLethalHit(activeChar, target, skill);
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (!skill.isPotion() && !skill.isToggle())
			activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}