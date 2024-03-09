package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.xml.HennaTable;
import net.l2jpx.gameserver.model.actor.instance.L2HennaInstance;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.L2Henna;
import net.l2jpx.gameserver.util.Util;

public final class RequestHennaEquip extends L2GameClientPacket
{
	private int symbolId;
	
	/**
	 * packet type id 0xbb format: cd
	 */
	@Override
	protected void readImpl()
	{
		symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("HennaEquip"))
		{
			return;
		}
		
		final L2Henna template = HennaTable.getInstance().getTemplate(symbolId);
		
		if (template == null)
		{
			return;
		}
		
		final L2HennaInstance temp = new L2HennaInstance(template);
		int count = 0;
		
		/*
		 * Prevents henna drawing exploit: 1) talk to L2SymbolMakerInstance 2) RequestHennaList 3) Don't close the window and go to a GrandMaster and change your subclass 4) Get SymbolMaker range again and press draw You could draw any kind of henna just having the required subclass...
		 */
		boolean cheater = true;
		
		for (final L2HennaInstance h : HennaTable.getInstance().getAvailableHenna(activeChar.getClassId()))
		{
			if (h.getSymbolId() == temp.getSymbolId())
			{
				cheater = false;
				break;
			}
		}
		
		if (activeChar.getInventory() != null && activeChar.getInventory().getItemByItemId(temp.getItemIdDye()) != null)
		{
			count = activeChar.getInventory().getItemByItemId(temp.getItemIdDye()).getCount();
		}
		
		if (!cheater && count >= temp.getAmountDyeRequire() && activeChar.getAdena() >= temp.getPrice() && activeChar.addHenna(temp))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_SYMBOL_HAS_BEEN_ADDED));
			
			// HennaInfo hi = new HennaInfo(temp,activeChar);
			// activeChar.sendPacket(hi);
			
			activeChar.getInventory().reduceAdena("Henna", temp.getPrice(), activeChar, activeChar.getLastFolkNPC());
			final L2ItemInstance dyeToUpdate = activeChar.getInventory().destroyItemByItemId("Henna", temp.getItemIdDye(), temp.getAmountDyeRequire(), activeChar, activeChar.getLastFolkNPC());
			
			// update inventory
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(activeChar.getInventory().getAdenaInstance());
			iu.addModifiedItem(dyeToUpdate);
			activeChar.sendPacket(iu);
			
			final ItemList il = new ItemList(getClient().getActiveChar(), true);
			sendPacket(il);
			
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_SYMBOL_CANNOT_BE_DRAWN));
			
			if (!activeChar.isGM() && cheater)
			{
				Util.handleIllegalPlayerAction(activeChar, "Exploit attempt: Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tryed to add a forbidden henna.", Config.DEFAULT_PUNISH);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] bc RequestHennaEquip";
	}
}
