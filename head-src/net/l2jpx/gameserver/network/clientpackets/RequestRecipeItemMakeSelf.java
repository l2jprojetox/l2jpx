package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.controllers.RecipeController;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2JFrozen
 */
public final class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int id;
	
	@Override
	protected void readImpl()
	{
		id = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf"))
		{
			return;
		}
		
		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendMessage("Cannot make items while trading");
			return;
		}
		
		if (activeChar.isInCraftMode())
		{
			activeChar.sendMessage("Currently in Craft Mode");
			return;
		}
		
		RecipeController.getInstance().requestMakeItem(activeChar, id);
	}
	
	@Override
	public String getType()
	{
		return "[C] AF RequestRecipeItemMakeSelf";
	}
	
}
