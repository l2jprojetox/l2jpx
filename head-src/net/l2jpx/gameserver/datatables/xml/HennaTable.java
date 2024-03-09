package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.gameserver.model.actor.instance.L2HennaInstance;
import net.l2jpx.gameserver.model.base.ClassId;
import net.l2jpx.gameserver.templates.L2Henna;
import net.l2jpx.gameserver.templates.StatsSet;

/**
 * @author fissban
 * @author ReynalDev
 */
public class HennaTable
{
	private static final Logger LOGGER = Logger.getLogger(HennaTable.class);
	
	private static HennaTable instance;
	
	private Map<Integer, L2Henna> hennaTemplates = new HashMap<>();
	private Map<ClassId, List<L2HennaInstance>> hennaTrees = new HashMap<>();
	
	public static HennaTable getInstance()
	{
		if (instance == null)
		{
			instance = new HennaTable();
		}
		
		return instance;
	}
	
	private HennaTable()
	{
		loadData();
	}
	
	private void loadData()
	{
		String filePath = "data/xml/hennas.xml";
		
		try
		{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList hennaList = doc.getElementsByTagName("henna");
			
			int counter = 0;
			for (int i = 0; i < hennaList.getLength(); i++)
			{	
				int symbol_id = 0;
				int dye_id = 0;
				int dye_amount = 0;
				int price = 0;
				int cancel_fee = 0;
				int stat_str = 0;
				int stat_dex = 0;
				int stat_con = 0;
				int stat_wit = 0;
				int stat_int = 0;
				int stat_men = 0;
				List<ClassId> availableClassList = new ArrayList<>();
				
				Node hennaNode = hennaList.item(i);
				
				for (Node hennaInfo = hennaNode.getFirstChild(); hennaInfo != null; hennaInfo = hennaInfo.getNextSibling())
				{
					NamedNodeMap attrs = hennaInfo.getAttributes();
					
					switch (hennaInfo.getNodeName())
					{
						case "symbol_id":
							symbol_id = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
							break;
						case "dye_id":
							dye_id = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
							break;
						case "dye_amount":
							dye_amount = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
							break;
						case "price":
							price = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
							break;
						case "cancel_fee":
							cancel_fee = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
							break;
						case "stat":
							stat_str = Integer.parseInt(attrs.getNamedItem("str").getNodeValue());
							stat_dex = Integer.parseInt(attrs.getNamedItem("dex").getNodeValue());
							stat_con = Integer.parseInt(attrs.getNamedItem("con").getNodeValue());
							stat_wit = Integer.parseInt(attrs.getNamedItem("wit").getNodeValue());
							stat_int = Integer.parseInt(attrs.getNamedItem("int").getNodeValue());
							stat_men = Integer.parseInt(attrs.getNamedItem("men").getNodeValue());
							break;
						case "availableClass":
							String availableClass = attrs.getNamedItem("val").getNodeValue();
							
							for (String classIdString : availableClass.split(" "))
							{
								ClassId classId = ClassId.getClassIdById(Integer.parseInt(classIdString));
								availableClassList.add(classId);
							}
							break;
					}
					
				}
				
				StatsSet hennaDat = new StatsSet();
				hennaDat.set("symbol_id", symbol_id);
				hennaDat.set("dye", dye_id);
				hennaDat.set("amount", dye_amount);
				hennaDat.set("price", price);
				hennaDat.set("cancel_fee", cancel_fee);
				hennaDat.set("stat_INT", stat_int);
				hennaDat.set("stat_STR", stat_str);
				hennaDat.set("stat_CON", stat_con);
				hennaDat.set("stat_MEN", stat_men);
				hennaDat.set("stat_DEX", stat_dex);
				hennaDat.set("stat_WIT", stat_wit);
				
				L2Henna template = new L2Henna(hennaDat);
				
				hennaTemplates.put(symbol_id, template);
				
				L2HennaInstance henna = new L2HennaInstance(template);
				
				for (ClassId classId : availableClassList)
				{
					if (!hennaTrees.containsKey(classId))
					{
						hennaTrees.put(classId, new ArrayList<>());
					}
					
					hennaTrees.get(classId).add(henna);
				}
				
				counter += availableClassList.size();
			}
			
			LOGGER.info("HennaTable: Loaded " + hennaTemplates.size() + " Templates.");
			LOGGER.info("HennaTable: Loaded " + counter + " Henna Tree.");
		}
		catch (Exception e)
		{
			LOGGER.error("HennaTable.loadData : Error while creating table. ", e);
		}
	}
	
	public L2Henna getTemplate(int id)
	{
		return hennaTemplates.get(id);
	}
	
	public L2HennaInstance[] getAvailableHenna(ClassId classId)
	{
		List<L2HennaInstance> henna = hennaTrees.get(classId);
		
		if (henna == null)
		{
			LOGGER.error("Hennatree for class id " + classId + " is not defined!");
			return new L2HennaInstance[0];
		}
		
		return henna.toArray(new L2HennaInstance[henna.size()]);
	}
}
