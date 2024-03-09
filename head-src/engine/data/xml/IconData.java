package engine.data.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.l2jpx.gameserver.util.StringUtil;
import net.l2jpx.util.random.Rnd;

import engine.data.XmlParser;
import engine.enums.ItemIconType;
import engine.util.builders.html.Icon;

/**
 * @author fissban
 */
public class IconData extends XmlParser
{
	protected static final Logger LOG = Logger.getLogger(IconData.class.getName());
	
	private final static Map<Integer, String> items = new HashMap<>();
	
	@Override
	public void load()
	{
		// Duplicate data is prevented if this method is reloaded
		items.clear();
		loadFile("./data/xml/engine/icons.xml");
		StringUtil.printSection("IconData: Loaded " + items.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("item"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			int id = parseInt(attrs, "id");
			String icon = parseString(attrs, "icon");
			items.put(id, icon);
		}
	}
	
	/**
	 * You get an icon of a specific item id
	 * @param  itemId
	 * @return
	 */
	public static String getIconByItemId(int itemId)
	{
		return items.get(itemId);
	}
	
	/**
	 * You get an icon of a random type of a specific type of item
	 * @param  itemIconType
	 * @param  rnd
	 * @return
	 */
	public static String getRandomItemType(ItemIconType itemIconType, int rnd)
	{
		String returnIcon = "";
		
		while (returnIcon.equals(""))
		{
			for (String icon : items.values())
			{
				if (icon.startsWith(itemIconType.getSearchItem()))
				{
					// it leaves aside the icons that can generate confusion.
					if ((Rnd.get(rnd) == 0) && !icon.equals(Icon.noimage) && !icon.equals(Icon.weapon_monster_i00))
					{
						returnIcon = icon;
					}
				}
			}
		}
		return returnIcon;
	}
	
	public static IconData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IconData INSTANCE = new IconData();
	}
}
