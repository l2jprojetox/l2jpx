package net.l2jpx.gameserver.model.entity.olympiad;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author GodKratos
 */

class OlympiadStadium
{
	private boolean freeToUse = true;
	private int[] coords = new int[3];
	private List<L2PcInstance> spectators;
	
	public boolean isFreeToUse()
	{
		return freeToUse;
	}
	
	public void setStadiaBusy()
	{
		freeToUse = false;
	}
	
	public void setStadiaFree()
	{
		freeToUse = true;
		clearSpectators();
	}
	
	public int[] getCoordinates()
	{
		return coords;
	}
	
	public OlympiadStadium(int x, int y, int z)
	{
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
		spectators = new CopyOnWriteArrayList<>();
	}
	
	protected void addSpectator(int id, L2PcInstance spec, boolean storeCoords)
	{
		spec.enterOlympiadObserverMode(getCoordinates()[0], getCoordinates()[1], getCoordinates()[2], id, storeCoords);
		spectators.add(spec);
	}
	
	protected List<L2PcInstance> getSpectators()
	{
		return spectators;
	}
	
	protected void removeSpectator(L2PcInstance spec)
	{
		if (spectators != null && spectators.contains(spec))
		{
			spectators.remove(spec);
		}
	}
	
	private void clearSpectators()
	{
		spectators.clear();
	}
}
