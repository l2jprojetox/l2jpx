package net.l2jpx.gameserver.model.actor.knownlist;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Maktakien
 */
public class BoatKnownList extends CharKnownList
{
	
	/**
	 * @param activeChar
	 */
	public BoatKnownList(final L2Character activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public int getDistanceToForgetObject(final L2Object object)
	{
		if (!(object instanceof L2PcInstance))
		{
			return 0;
		}
		
		return 8000;
	}
	
	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		if (!(object instanceof L2PcInstance))
		{
			return 0;
		}
		
		return 4000;
	}
	
}
