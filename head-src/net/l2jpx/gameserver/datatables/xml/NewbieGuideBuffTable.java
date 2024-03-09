package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.holder.NewbieGuideBuffHolder;

/**
 * @author Ayor
 * @author ReynalDev
 */
public class NewbieGuideBuffTable
{
	private final static Logger LOGGER = Logger.getLogger(NewbieGuideBuffTable.class);
	private static NewbieGuideBuffTable instance;
	
	public List<NewbieGuideBuffHolder> newbieGuideBuffList = new ArrayList<>();
	
	private int lowerLevel = 100;
	private int highestLevel = 1;
	
	public static NewbieGuideBuffTable getInstance()
	{
		if (instance == null)
		{
			instance = new NewbieGuideBuffTable();
		}
		
		return instance;
	}
	
	public static void reload()
	{
		instance = null;
		getInstance();
	}
	
	private NewbieGuideBuffTable()
	{
		loadData();
	}
	
	private void loadData()
	{
		String filePath = "data/xml/newbieGuideBuffs.xml";
		try
		{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("buff");
			
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					
					int skillId = Integer.parseInt(eElement.getAttribute("skillId"));
					int skillLevel = Integer.parseInt(eElement.getAttribute("skillLevel"));
					int minLevel = Integer.parseInt(eElement.getAttribute("minLevel"));
					int maxLevel = Integer.parseInt(eElement.getAttribute("maxLevel"));
					boolean giveToMage = Boolean.parseBoolean(eElement.getAttribute("giveToMage"));
					boolean giveToWarrior = Boolean.parseBoolean(eElement.getAttribute("giveToWarrior"));
					
					if(minLevel < lowerLevel)
					{
						lowerLevel = minLevel;
					}
					
					if(maxLevel > highestLevel)
					{
						highestLevel = maxLevel;
					}
					
					newbieGuideBuffList.add(new NewbieGuideBuffHolder(skillId, skillLevel, minLevel, maxLevel, giveToMage, giveToWarrior));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("NewbieGuideBuffTable: Could not read " + filePath + " file.", e);
		}
		
		LOGGER.info("NewbieGuideBuffTable: Loaded " + newbieGuideBuffList.size() + " templates");
	}
	
	public int getLowerLevel()
	{
		return lowerLevel;
	}
	
	public int getHighestLevel()
	{
		return highestLevel;
	}
	
	public List<NewbieGuideBuffHolder> getNewbieHelperBuffsForPlayer(L2PcInstance player)
	{
		List<NewbieGuideBuffHolder> buffList = new ArrayList<>();
		
		if(player.isMageClass())
		{
			for(NewbieGuideBuffHolder buff : newbieGuideBuffList)
			{
				if(buff.isForMage() && player.getLevel() >= buff.getMinLevel() && player.getLevel() <= buff.getMaxLevel())
				{
					buffList.add(buff);
				}
			}
		}
		else
		{
			for(NewbieGuideBuffHolder buff : newbieGuideBuffList)
			{
				if(buff.isForWarrior() && player.getLevel() >= buff.getMinLevel() && player.getLevel() <= buff.getMaxLevel())
				{
					buffList.add(buff);
				}
			}
		}
		
		return buffList;
	}
}
