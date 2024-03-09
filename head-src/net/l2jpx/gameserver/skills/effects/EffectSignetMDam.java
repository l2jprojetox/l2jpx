package net.l2jpx.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.ai.CtrlEvent;
import net.l2jpx.gameserver.datatables.sql.NpcTable;
import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.L2Attackable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Summon;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2EffectPointInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillLaunched;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Env;
import net.l2jpx.gameserver.skills.Formulas;
import net.l2jpx.gameserver.skills.l2skills.L2SkillSignetCasttime;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.util.Point3D;

/**
 * @author Shyla
 */
public final class EffectSignetMDam extends L2Effect
{
	private L2EffectPointInstance actor;
	private boolean bss;
	private boolean sps;
	
	// private SigmetMDAMTask skill_task;
	
	public EffectSignetMDam(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_GROUND;
	}
	
	@Override
	public void onStart()
	{
		L2NpcTemplate template;
		if (getSkill() instanceof L2SkillSignetCasttime)
		{
			template = NpcTable.getInstance().getTemplate(((L2SkillSignetCasttime) getSkill()).effectNpcId);
		}
		else
		{
			return;
		}
		
		final L2EffectPointInstance effectPoint = new L2EffectPointInstance(IdFactory.getInstance().getNextId(), template, getEffector());
		effectPoint.getStatus().setCurrentHp(effectPoint.getMaxHp());
		effectPoint.getStatus().setCurrentMp(effectPoint.getMaxMp());
		
		L2World.getInstance().storeObject(effectPoint);
		
		int x = getEffector().getX();
		int y = getEffector().getY();
		int z = getEffector().getZ();
		
		if (getEffector() instanceof L2PcInstance && getSkill().getTargetType() == L2Skill.SkillTargetType.TARGET_GROUND)
		{
			final Point3D wordPosition = ((L2PcInstance) getEffector()).getCurrentSkillWorldPosition();
			
			if (wordPosition != null)
			{
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		}
		effectPoint.setIsInvul(true);
		effectPoint.spawnMe(x, y, z);
		
		actor = effectPoint;
		
		// skill_task = new SigmetMDAMTask();
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getCount() >= getTotalCount() - 2)
		{
			return true; // do nothing first 2 times
		}
		
		final int mpConsume = getSkill().getMpConsume();
		final L2PcInstance caster = (L2PcInstance) getEffector();
		
		sps = caster.checkSps();
		bss = caster.checkBss();
		
		List<L2Character> targets = new ArrayList<>();
		
		for (final L2Character cha : actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius()))
		{
			if (cha == null || cha == caster)
			{
				continue;
			}
			
			if (cha instanceof L2Attackable || cha instanceof L2PlayableInstance)
			{
				if (cha.isAlikeDead())
				{
					continue;
				}
				
				if (mpConsume > caster.getStatus().getCurrentMp())
				{
					caster.sendPacket(new SystemMessage(SystemMessageId.YOUR_SKILL_WAS_REMOVED_DUE_TO_A_LACK_OF_MP));
					return false;
				}
				
				caster.reduceCurrentMp(mpConsume);
				
				if (cha instanceof L2PlayableInstance)
				{
					if (!(cha instanceof L2Summon && ((L2Summon) cha).getOwner() == caster))
					{
						caster.updatePvPStatus(cha);
					}
				}
				
				targets.add(cha);
			}
		}
		
		if (!targets.isEmpty())
		{
			caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill().getDisplayId(), getSkill().getLevel(), targets.toArray(new L2Character[targets.size()])));
			for (final L2Character target : targets)
			{
				final boolean mcrit = Formulas.calcMCrit(caster.getMCriticalHit(target, getSkill()));
				final int mdam = (int) Formulas.calcMagicDam(caster, target, getSkill(), sps, bss, mcrit);
				
				if (target instanceof L2Summon)
				{
					target.broadcastStatusUpdate();
				}
				
				if (mdam > 0)
				{
					if (!target.isRaid() && Formulas.calcAtkBreak(target, mdam))
					{
						target.breakAttack();
						target.breakCast();
					}
					caster.sendDamageMessage(target, mdam, mcrit, false, false);
					target.reduceCurrentHp(mdam, caster);
				}
				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
			}
		}
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (actor != null)
		{
			final L2PcInstance caster = (L2PcInstance) getEffector();
			
			// remove shots
			if (bss)
			{
				caster.removeBss();
				
			}
			else if (sps)
			{
				caster.removeSps();
			}
			actor.deleteMe();
		}
	}
	
}
