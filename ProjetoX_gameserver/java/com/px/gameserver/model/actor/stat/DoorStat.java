package com.px.gameserver.model.actor.stat;

import com.px.gameserver.data.manager.SevenSignsManager;
import com.px.gameserver.enums.SealType;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.instance.Door;

public class DoorStat extends CreatureStat
{
	private int _upgradeHpRatio;
	
	public DoorStat(Door activeChar)
	{
		super(activeChar);
		
		_upgradeHpRatio = 1;
	}
	
	@Override
	public Door getActiveChar()
	{
		return (Door) super.getActiveChar();
	}
	
	@Override
	public int getMDef(Creature target, L2Skill skill)
	{
		double defense = getActiveChar().getTemplate().getBaseMDef();
		
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				defense *= 1.2;
				break;
			
			case DUSK:
				defense *= 0.3;
				break;
		}
		
		return (int) defense;
	}
	
	@Override
	public int getPDef(Creature target)
	{
		double defense = getActiveChar().getTemplate().getBasePDef();
		
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				defense *= 1.2;
				break;
			
			case DUSK:
				defense *= 0.3;
				break;
		}
		
		return (int) defense;
	}
	
	@Override
	public int getMaxHp()
	{
		return super.getMaxHp() * _upgradeHpRatio;
	}
	
	public final void setUpgradeHpRatio(int hpRatio)
	{
		_upgradeHpRatio = hpRatio;
	}
	
	public final int getUpgradeHpRatio()
	{
		return _upgradeHpRatio;
	}
}