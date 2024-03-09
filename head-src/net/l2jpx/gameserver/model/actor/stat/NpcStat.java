package net.l2jpx.gameserver.model.actor.stat;

import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.skills.Stats;

/**
 * @author programmos
 */

public class NpcStat extends CharStat
{
	public NpcStat(final L2NpcInstance activeChar)
	{
		super(activeChar);
		
		setLevel(getActiveChar().getTemplate().level);
	}
	
	@Override
	public L2NpcInstance getActiveChar()
	{
		return (L2NpcInstance) super.getActiveChar();
	}
	
	@Override
	public final int getMaxHp()
	{
		return (int) calcStat(Stats.MAX_HP, getActiveChar().getTemplate().baseHpMax, null, null);
	}
}
