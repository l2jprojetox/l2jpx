package net.l2jpx.gameserver.model;

/**
 * @author ReynalDev
 */
public class L2TeleportLocation
{
	private int teleId;
	private int locX;
	private int locY;
	private int locZ;
	private int itemId;
	private int price;
	private boolean forNoble;
	
	public void setTeleId(final int id)
	{
		teleId = id;
	}
	
	public void setLocX(int locX)
	{
		this.locX = locX;
	}
	
	public void setLocY(int locY)
	{
		this.locY = locY;
	}
	
	public void setLocZ(int locZ)
	{
		this.locZ = locZ;
	}
	
	public void setItemid(int itemId)
	{
		this.itemId = itemId;
	}
	
	public void setPrice(int price)
	{
		this.price = price;
	}
	
	public void setIsForNoble(boolean val)
	{
		forNoble = val;
	}
	
	public int getTeleId()
	{
		return teleId;
	}
	
	public int getLocX()
	{
		return locX;
	}
	
	public int getLocY()
	{
		return locY;
	}
	
	public int getLocZ()
	{
		return locZ;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public boolean isForNoble()
	{
		return forNoble;
	}
}
