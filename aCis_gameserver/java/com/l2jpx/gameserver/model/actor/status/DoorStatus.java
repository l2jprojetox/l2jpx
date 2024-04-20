package com.l2jpx.gameserver.model.actor.status;

import com.l2jpx.gameserver.data.manager.SevenSignsManager;
import com.l2jpx.gameserver.enums.SealType;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.instance.Door;
import com.l2jpx.gameserver.network.serverpackets.DoorStatusUpdate;
import com.l2jpx.gameserver.skills.L2Skill;

public class DoorStatus extends CreatureStatus<Door>
{
	private int _upgradeHpRatio = 1;
	
	public DoorStatus(Door actor)
	{
		super(actor);
	}
	
	@Override
	public final int getLevel()
	{
		return _actor.getTemplate().getLevel();
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		_actor.broadcastPacket(new DoorStatusUpdate(_actor));
	}
	
	@Override
	public int getMDef(Creature target, L2Skill skill)
	{
		double defense = _actor.getTemplate().getBaseMDef();
		
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
		double defense = _actor.getTemplate().getBasePDef();
		
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