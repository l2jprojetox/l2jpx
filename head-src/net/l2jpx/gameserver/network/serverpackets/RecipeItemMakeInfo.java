package net.l2jpx.gameserver.network.serverpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.xml.RecipeTable;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.templates.Recipe;

/**
 * format dddd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeItemMakeInfo extends L2GameServerPacket
{
	private static Logger LOGGER = Logger.getLogger(RecipeItemMakeInfo.class);
	
	private final int id;
	private final L2PcInstance activeChar;
	private final boolean success;
	
	public RecipeItemMakeInfo(final int id, final L2PcInstance player, final boolean success)
	{
		this.id = id;
		activeChar = player;
		this.success = success;
	}
	
	public RecipeItemMakeInfo(final int id, final L2PcInstance player)
	{
		this.id = id;
		activeChar = player;
		success = true;
	}
	
	@Override
	protected final void writeImpl()
	{
		final Recipe recipe = RecipeTable.getInstance().getRecipeById(id);
		
		if (recipe != null)
		{
			writeC(0xD7);
			
			writeD(id);
			writeD(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
			writeD((int) activeChar.getCurrentMp());
			writeD(activeChar.getMaxMp());
			writeD(success ? 1 : 0); // item creation success/failed
		}
		else if (Config.DEBUG)
		{
			LOGGER.info("No recipe found with ID = " + id);
		}
	}
	
	@Override
	public String getType()
	{
		return "[S] D7 RecipeItemMakeInfo";
	}
}
