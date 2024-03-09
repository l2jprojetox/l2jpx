package net.l2jpx.gameserver.model.zone.type;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.zone.L2ZoneType;

/**
 * A peaceful zone
 * @author durgus
 */
public class L2PeaceZone extends L2ZoneType
{
	public L2PeaceZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PEACE, true);
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PEACE, false);
	}
	
	@Override
	protected void onDieInside(final L2Character character)
	{
	}
	
	@Override
	protected void onReviveInside(final L2Character character)
	{
	}
	
}
