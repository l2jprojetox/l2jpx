package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.controllers.RecipeController;

public final class RequestRecipeBookOpen extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestRecipeBookOpen.class);
	
	private boolean isDwarvenCraft;
	
	@Override
	protected void readImpl()
	{
		isDwarvenCraft = readD() == 0;
		if (Config.DEBUG)
		{
			LOGGER.info("RequestRecipeBookOpen : " + (isDwarvenCraft ? "dwarvenCraft" : "commonCraft"));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		if (getClient().getActiveChar().getPrivateStoreType() != 0)
		{
			getClient().getActiveChar().sendMessage("Cannot use recipe book while trading");
			return;
		}
		
		RecipeController.getInstance().requestBookOpen(getClient().getActiveChar(), isDwarvenCraft);
	}
	
	@Override
	public String getType()
	{
		return "[C] AC RequestRecipeBookOpen";
	}
}
