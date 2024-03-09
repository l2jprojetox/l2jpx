package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2EffectPointInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.gameserver.skills.l2skills.L2SkillSignet;
import net.l2jpx.gameserver.skills.l2skills.L2SkillSignetCasttime;

/**
 * @author L2jFrozen
 */
public final class EffectSignet extends L2Effect
{
	private L2Skill skill;
	private L2EffectPointInstance actor;
	
	public EffectSignet(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_EFFECT;
	}
	
	@Override
	public void onStart()
	{
		if (getSkill() instanceof L2SkillSignet)
		{
			skill = SkillTable.getInstance().getInfo(((L2SkillSignet) getSkill()).effectId, getLevel());
		}
		else if (getSkill() instanceof L2SkillSignetCasttime)
		{
			skill = SkillTable.getInstance().getInfo(((L2SkillSignetCasttime) getSkill()).effectId, getLevel());
		}
		actor = (L2EffectPointInstance) getEffected();
	}
	
	@Override
	public boolean onActionTime()
	{
		// if (getCount() == getTotalCount() - 1) return true; // do nothing first time
		if (skill == null)
		{
			return true;
		}
		final int mpConsume = skill.getMpConsume();
		final L2PcInstance caster = (L2PcInstance) getEffector();
		
		if (mpConsume > getEffector().getStatus().getCurrentMp())
		{
			getEffector().sendPacket(new SystemMessage(SystemMessageId.YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP));
			return false;
		}
		
		getEffector().reduceCurrentMp(mpConsume);
		
		for (final L2Character cha : actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius()))
		{
			if (cha == null || cha == caster || cha.isDead())
			{
				continue;
			}
			
			if (skill.isOffensive())
			{
				if (cha instanceof L2PcInstance)
				{
					
					if ((((L2PcInstance) cha).getClanId() > 0 && caster.getClanId() > 0 && ((L2PcInstance) cha).getClanId() != caster.getClanId()) || (((L2PcInstance) cha).getAllyId() > 0 && caster.getAllyId() > 0 && ((L2PcInstance) cha).getAllyId() != caster.getAllyId()) || (cha.getParty() != null && caster.getParty() != null && !cha.getParty().equals(caster.getParty())))
					{
						skill.getEffects(actor, cha, false, false, false);
						continue;
					}
				}
			}
			else
			{
				if (cha instanceof L2PcInstance)
				{
					if ((cha.getParty() != null && caster.getParty() != null && cha.getParty().equals(caster.getParty())) || (((L2PcInstance) cha).getClanId() > 0 && caster.getClanId() > 0 && ((L2PcInstance) cha).getClanId() == caster.getClanId()) || (((L2PcInstance) cha).getAllyId() > 0 && caster.getAllyId() > 0 && ((L2PcInstance) cha).getAllyId() == caster.getAllyId()))
					{
						skill.getEffects(actor, cha, false, false, false);
						skill.getEffects(actor, caster, false, false, false); // Affect caster too.
						continue;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (actor != null)
		{
			actor.deleteMe();
		}
	}
}