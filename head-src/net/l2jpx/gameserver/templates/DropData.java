package net.l2jpx.gameserver.templates;

import java.util.Calendar;
import java.util.List;

/**
 * @author ReynalDev
 */
public class DropData
{
	private List<DropDataItem> itemList;
	private Calendar startDate;
	private Calendar endDate;
	
	public List<DropDataItem> getItemList()
	{
		return itemList;
	}
	public void setItemList(List<DropDataItem> itemList)
	{
		this.itemList = itemList;
	}
	public Calendar getStartDate()
	{
		return startDate;
	}
	public void setStartDate(Calendar startDate)
	{
		this.startDate = startDate;
	}
	public Calendar getEndDate()
	{
		return endDate;
	}
	public void setEndDate(Calendar endDate)
	{
		this.endDate = endDate;
	}
}
