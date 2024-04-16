package com.px.gameserver.model.holder;

import java.util.Objects;

/**
 * This is an int/int container that holds a pair of player IDs. ID1 is always the lowest value, while ID2 is the highest.
 */
public class PlayerPair
{
	private final int _id1;
	private final int _id2;
	
	public PlayerPair(int id1, int id2)
	{
		_id1 = Math.min(id1, id2);
		_id2 = Math.max(id1, id2);
	}
	
	public int getID1()
	{
		return _id1;
	}
	
	public int getID2()
	{
		return _id2;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		final PlayerPair that = (PlayerPair) obj;
		
		return _id1 == that._id1 && _id2 == that._id2;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_id1, _id2);
	}
	
	public boolean contains(int playerId)
	{
		return _id1 == playerId || _id2 == playerId;
	}
}