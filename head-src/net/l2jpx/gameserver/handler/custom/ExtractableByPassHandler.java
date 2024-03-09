package net.l2jpx.gameserver.handler.custom;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.handler.ItemHandler;
import net.l2jpx.gameserver.handler.itemhandlers.ExtractableItems;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Nick
 */
public class ExtractableByPassHandler implements ICustomByPassHandler
{
	protected static final Logger LOGGER = Logger.getLogger(ExtractableByPassHandler.class);
	private static final String[] IDS =
	{
		"extractOne",
		"extractAll"
	};
	
	@Override
	public void handleCommand(String command, L2PcInstance player, String parameters)
	{
		try
		{
			int objId = Integer.parseInt(parameters);
			L2ItemInstance item = player.getInventory().getItemByObjectId(objId);
			
			if (item == null)
			{
				return;
			}
			
			IItemHandler ih = ItemHandler.getInstance().getItemHandler(item.getItemId());
			
			if (ih == null || !(ih instanceof ExtractableItems))
			{
				return;
			}
			
			if (command.equalsIgnoreCase("extractOne"))
			{
				((ExtractableItems) ih).doExtract(player, item, 1);
			}
			else if (command.equalsIgnoreCase("extractAll"))
			{
				((ExtractableItems) ih).doExtract(player, item, item.getCount());
			}
		}
		catch (Exception e)
		{
			LOGGER.error("ExtractableByPassHandler.handleCommand : Error while running ", e);
		}
	}
	
	@Override
	public String[] getByPassCommands()
	{
		return IDS;
	}
}
