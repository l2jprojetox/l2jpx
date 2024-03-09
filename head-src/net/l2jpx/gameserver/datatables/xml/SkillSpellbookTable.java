package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.gameserver.datatables.sql.SkillTreeTable;
import net.l2jpx.gameserver.model.L2Skill;

/**
 * @author l2jserver
 * @author ReynalDev
 */
public class SkillSpellbookTable
{
	private final static Logger LOGGER = Logger.getLogger(SkillTreeTable.class);
	
	private static SkillSpellbookTable instance;
	
	private static Map<Integer, Integer> skillSpellbooks;
	
	public static SkillSpellbookTable getInstance()
	{
		if (instance == null)
		{
			instance = new SkillSpellbookTable();
		}
		
		return instance;
	}
	
	private SkillSpellbookTable()
	{
		skillSpellbooks = new HashMap<>();
		
		String path = "data/xml/skill_spellbooks.xml";
		try
		{
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("book");
			
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) nNode;
					
					int skillId = Integer.parseInt(element.getAttribute("skillId"));
					int itemId = Integer.parseInt(element.getAttribute("itemId"));
					
					skillSpellbooks.put(skillId, itemId);
				}
			}
			
			LOGGER.info("SkillSpellbookTable: Loaded " + skillSpellbooks.size() + " spellbooks");
			
		}
		catch (Exception e)
		{
			LOGGER.error("Could not read " + path, e);
		}
	}
	
	public int getBookForSkill(int skillId, int level)
	{
		if (skillId == L2Skill.SKILL_DIVINE_INSPIRATION && level != -1)
		{
			switch (level)
			{
				case 1:
					return 8618; // Ancient Book - Divine Inspiration (Modern Language Version)
				case 2:
					return 8619; // Ancient Book - Divine Inspiration (Original Language Version)
				case 3:
					return 8620; // Ancient Book - Divine Inspiration (Manuscript)
				case 4:
					return 8621; // Ancient Book - Divine Inspiration (Original Version)
				default:
					return -1;
			}
		}
		
		return skillSpellbooks.getOrDefault(skillId, -1);
	}
	
	public int getBookForSkill(L2Skill skill)
	{
		return getBookForSkill(skill.getId(), -1);
	}
	
	public int getBookForSkill(L2Skill skill, int level)
	{
		return getBookForSkill(skill.getId(), level);
	}
}
