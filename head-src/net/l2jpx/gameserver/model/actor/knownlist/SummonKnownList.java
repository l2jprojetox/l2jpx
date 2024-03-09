package net.l2jpx.gameserver.model.actor.knownlist;

import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Summon;

public class SummonKnownList extends PlayableKnownList
{
	public SummonKnownList(final L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public final L2Summon getActiveChar()
	{
		return (L2Summon) super.getActiveChar();
	}
	
	@Override
	public int getDistanceToForgetObject(final L2Object object)
	{
		if (object == getActiveChar().getOwner() || object == getActiveChar().getTarget())
		{
			return 6000;
		}
		
		return 3000;
	}
	
	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		return 1500;
	}
}
