package net.l2jpx.gameserver.skills.effects;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author kombat
 */
final class EffectBestowSkill extends L2Effect
{
	public EffectBestowSkill(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#getEffectType()
	 */
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onStart()
	 */
	@Override
	public void onStart()
	{
		final L2Skill tempSkill = SkillTable.getInstance().getInfo(getSkill().getTriggeredId(), getSkill().getTriggeredLevel());
		if (tempSkill != null)
		{
			getEffected().addSkill(tempSkill);
		}
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onExit()
	 */
	@Override
	public void onExit()
	{
		getEffected().removeSkill(getSkill().getTriggeredId());
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Effect#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
