package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.controllers.RecipeController;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.util.Util;

/**
 * @author L2JFrozen
 */
public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private int id;
	private int recipeId;
	@SuppressWarnings("unused")
	private int unknow;
	
	@Override
	protected void readImpl()
	{
		id = readD();
		recipeId = readD();
		unknow = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getManufacture().tryPerformAction("RecipeShopMake"))
		{
			return;
		}
		
		final L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().findObject(id);
		if (manufacturer == null)
		{
			return;
		}
		
		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendMessage("Cannot make items while trading");
			return;
		}
		
		if (manufacturer.getPrivateStoreType() != 5)
		{
			// activeChar.sendMessage("Cannot make items while trading");
			return;
		}
		
		if (activeChar.isInCraftMode() || manufacturer.isInCraftMode())
		{
			activeChar.sendMessage("Currently in Craft Mode");
			return;
		}
		
		if (manufacturer.isInDuel() || activeChar.isInDuel())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_CRAFT_DURING_COMBAT));
			return;
		}
		
		if (Util.checkIfInRange(150, activeChar, manufacturer, true))
		{
			RecipeController.getInstance().requestManufactureItem(manufacturer, recipeId, activeChar);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] B6 RequestRecipeShopMakeItem";
	}
	
}
