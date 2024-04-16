package com.px.gameserver.model.location;

import java.util.List;

/**
 * A datatype extending {@link SpawnLocation}, which handles a single Control Tower spawn point and its parameters (such as guards npcId List), npcId to spawn and upgrade level.
 */
public class TowerSpawnLocation extends SpawnLocation
{
	private final int _npcId;
	
	private List<Integer> _zoneIds;
	private int _upgradeLevel;
	
	public TowerSpawnLocation(int npcId, Location loc)
	{
		super(loc.getX(), loc.getY(), loc.getZ(), -1);
		
		_npcId = npcId;
	}
	
	public TowerSpawnLocation(int npcId, Location loc, List<Integer> zoneIds)
	{
		this(npcId, loc);
		
		_zoneIds = zoneIds;
	}
	
	public int getId()
	{
		return _npcId;
	}
	
	public List<Integer> getZoneIds()
	{
		return _zoneIds;
	}
	
	public int getUpgradeLevel()
	{
		return _upgradeLevel;
	}
	
	public void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
	}
}