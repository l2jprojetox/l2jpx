package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.gameserver.model.L2DropData;
import net.l2jpx.gameserver.templates.DropData;
import net.l2jpx.gameserver.templates.DropDataItem;

/**
 * @author ReynalDev
 * Add extra drop to L2Attackable npcs types
 * This drop respects the level rules, for example, if a player is 10 levels higher than an NPC the drop chance will be reduced considerably
 */
public class GlobalDropData
{
	private static final Logger LOGGER = Logger.getLogger(GlobalDropData.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static GlobalDropData instance;
	
	private List<DropData> globalDrops = new ArrayList<>();
	
	public static GlobalDropData getInstance()
	{
		if (instance == null)
		{
			instance = new GlobalDropData();
		}
		
		return instance;
	}
	
	private GlobalDropData()
	{
		loadData();
	}
	
	private void loadData()
	{
		String filePath = "data/xml/globalDrop.xml";
		
		try
		{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList dropList = doc.getElementsByTagName("drop");
			
			for (int i = 0; i < dropList.getLength(); i++)
			{	
				String startDate = "";
				String endDate = "";
				Node dropNode = dropList.item(i);
				NamedNodeMap dropAttributes = dropNode.getAttributes();
				
				startDate = dropAttributes.getNamedItem("startDate").getNodeValue();
				endDate = dropAttributes.getNamedItem("endDate").getNodeValue();
				
				DropData dropData = new DropData();
				
				try
				{
					Calendar startCalendar = Calendar.getInstance();
					startCalendar.setTime(DATE_FORMAT.parse(startDate));
					
					Calendar endCalendar = Calendar.getInstance();
					endCalendar.setTime(DATE_FORMAT.parse(endDate));
					
					dropData.setStartDate(startCalendar);
					dropData.setEndDate(endCalendar);
				}
				catch (Exception e)
				{
					LOGGER.error("Invalid startDate or endDate format", e);
					continue;
				}
				
				List<DropDataItem> itemList = new ArrayList<>();
				
				for (Node itemInfo = dropNode.getFirstChild(); itemInfo != null; itemInfo = itemInfo.getNextSibling())
				{
					NamedNodeMap itemAttributes = itemInfo.getAttributes();
					
					switch (itemInfo.getNodeName())
					{
						case "item":
							DropDataItem item = new DropDataItem();
							int itemId = Integer.parseInt(itemAttributes.getNamedItem("id").getNodeValue());
							item.setItemId(itemId);
							item.setMin(Integer.parseInt(itemAttributes.getNamedItem("min").getNodeValue()));
							item.setMax(Integer.parseInt(itemAttributes.getNamedItem("max").getNodeValue()));
							double chance = Double.parseDouble(itemAttributes.getNamedItem("chance").getNodeValue());
							chance *= L2DropData.MAX_CHANCE;
							chance /= 100;
							item.setChance(chance);
							
							if(itemId > 0)
							{
								itemList.add(item);
							}
							break;
					}
					
				}
				
				dropData.setItemList(itemList);
				
				addGlobalDrop(dropData);
			}
			
			if(globalDrops.size() > 0)
			{
				LOGGER.info("GlobalDropData: Loaded " + globalDrops.size() + " global drops");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("HennaTable.loadData : Error while creating table. ", e);
		}
	}
	
	/**
	 * A random item will be chosen to be dropped<BR>
	 * @param itemIds list of item IDs
	 * @param min  minimun amount to drop (this will affect all the item IDs)
	 * @param max  maximum amount to drop (this will affect all the item IDs)
	 * @param chance 1,000,000 is 100%
	 * @param startDate date when items will start to be dropped
	 * @param endDate date when items will end to be dropped
	 */
	public void addGlobalDrop(DropData dropData)
	{
		if(dropData.getItemList().isEmpty())
			return;
		
		globalDrops.add(dropData);
	}
	
	/**
	 * @return All drops that are within the range of start date and end date
	 */
	public List<DropData> getAllDrops()
	{
		List<DropData> list = new ArrayList<>();
		Date currentDate = new Date();
		
		for (DropData drop : globalDrops)
		{
			// If current date is greater than drop startDate and current date is lower than drop endDate
			if (currentDate.after(drop.getStartDate().getTime()) && currentDate.before(drop.getEndDate().getTime()))
			{
				list.add(drop);
			}
		}
		
		return list;
	}
}
