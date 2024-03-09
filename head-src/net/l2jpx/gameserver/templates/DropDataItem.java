package net.l2jpx.gameserver.templates;

import net.l2jpx.util.random.Rnd;

/**
 * @author ReynalDev
 */
public class DropDataItem
{
	private int itemId;
	private int min;
	private int max;
	private double chance;
	
	public int getItemId()
	{
		return itemId;
	}
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	public int getMin()
	{
		return min;
	}
	public void setMin(int min)
	{
		this.min = min;
	}
	public int getMax()
	{
		return max;
	}
	public void setMax(int max)
	{
		this.max = max;
	}
	public double getChance()
	{
		return chance;
	}
	public void setChance(double chance)
	{
		this.chance = chance;
	}
	
	public int getRandomAmount()
	{
		return Rnd.get(min,max);
	}
}
