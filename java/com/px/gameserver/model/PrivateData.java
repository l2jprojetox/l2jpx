package com.px.gameserver.model;

import com.px.commons.data.StatSet;
import com.px.commons.lang.StringUtil;

/**
 * This class defines the spawn data of a Private type.<BR>
 * In a NPC group, there are one leader and several privates.
 */
public class PrivateData
{
	private final int _id;
	private final int _weight;
	private final int _respawnTime;
	
	public PrivateData(StatSet set)
	{
		_id = set.getInteger("id");
		_weight = set.getInteger("weight");
		_respawnTime = StringUtil.getTimeStamp(set.getString("respawn"));
	}
	
	@Override
	public String toString()
	{
		return "PrivateData [_id=" + _id + ", _weight=" + _weight + ", _respawnTime=" + _respawnTime + "]";
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getWeight()
	{
		return _weight;
	}
	
	public int getRespawnTime()
	{
		return _respawnTime;
	}
}