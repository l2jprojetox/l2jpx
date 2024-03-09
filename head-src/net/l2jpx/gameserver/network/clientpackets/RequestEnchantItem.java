package net.l2jpx.gameserver.network.clientpackets;

import engine.EngineModsManager;
import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.ScrollEnchantTable;
import net.l2jpx.gameserver.datatables.sql.ItemTable;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.base.Race;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.EnchantResult;
import net.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.network.serverpackets.StatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.L2Item;
import net.l2jpx.gameserver.templates.L2WeaponType;
import net.l2jpx.gameserver.util.IllegalPlayerAction;
import net.l2jpx.gameserver.util.Util;
import net.l2jpx.util.L2Log;
import net.l2jpx.util.random.Rnd;

/**
 * @author ReynalDev
 */
public final class RequestEnchantItem extends L2GameClientPacket
{
	private int objectId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null || objectId == 0)
		{
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.cancelActiveTrade();
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_ATTEMP_TO_TRADE_HAS_FAILED));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Fix enchant transactions
		if (activeChar.isProcessingTransaction())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!activeChar.isOnline())
		{
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (activeChar.getPrivateStoreType() != 0 || activeChar.isInStoreMode())
		{
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
		L2ItemInstance scroll = activeChar.getActiveEnchantItem();
		activeChar.setActiveEnchantItem(null);
		
		if (item == null || scroll == null)
		{
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// can't enchant rods and shadow items
		if (item.getItem().getItemType() == L2WeaponType.ROD || item.isShadowItem())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (!Config.ENCHANT_HERO_WEAPON && item.getItemId() >= 6611 && item.getItemId() <= 6621)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (scroll.getItem().getCrystalType() != item.getItem().getCrystalType())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (item.isWear())
		{
			activeChar.setActiveEnchantItem(null);
			Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to enchant a weared Item", IllegalPlayerAction.PUNISH_KICK);
			return;
		}
		
		boolean blessedScroll = false;
		boolean crystalScroll = false;
		
		if (ScrollEnchantTable.getBlessedScrollEnchantIds().contains(scroll.getItemId()))
		{
			blessedScroll = true;
		}
		else if(ScrollEnchantTable.getCrystalScrollEnchantIds().contains(scroll.getItemId()))
		{
			crystalScroll = true;
		}
		
		SystemMessage sm;
		
		int oldEnchantLevel = item.getEnchantLevel();
		int chance = 0;
		int maxEnchantLevel = 0;
		int minEnchantLevel = 0;
		
		if (item.getItem().getType2() == L2Item.TYPE2_WEAPON)
		{
			if (blessedScroll)
			{
				if (item.getEnchantLevel() >= Config.BLESS_WEAPON_ENCHANT_LEVEL.size())
				{
					chance = Config.BLESS_WEAPON_ENCHANT_LEVEL.get(Config.BLESS_WEAPON_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.BLESS_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_WEAPON_MAX;
			}
			else if (crystalScroll)
			{
				if (item.getEnchantLevel() >= Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.size())
				{
					chance = Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.get(Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
				maxEnchantLevel = Config.CRYSTAL_ENCHANT_MAX;
			}
			else
			{ 
				// normal scrolls
				
				if (item.getEnchantLevel() >= Config.NORMAL_WEAPON_ENCHANT_LEVEL.size())
				{
					chance = Config.NORMAL_WEAPON_ENCHANT_LEVEL.get(Config.NORMAL_WEAPON_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.NORMAL_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_WEAPON_MAX;
			}
			
		}
		else if (item.getItem().getType2() == L2Item.TYPE2_SHIELD_ARMOR)
		{
			if (blessedScroll)
			{
				if (item.getEnchantLevel() >= Config.BLESS_ARMOR_ENCHANT_LEVEL.size())
				{
					chance = Config.BLESS_ARMOR_ENCHANT_LEVEL.get(Config.BLESS_ARMOR_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.BLESS_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_ARMOR_MAX;
			}
			else if (crystalScroll)
			{
				if (item.getEnchantLevel() >= Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.size())
				{
					chance = Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.get(Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
				maxEnchantLevel = Config.CRYSTAL_ENCHANT_MAX;
			}
			else
			{ 
				if (item.getEnchantLevel() >= Config.NORMAL_ARMOR_ENCHANT_LEVEL.size())
				{
					chance = Config.NORMAL_ARMOR_ENCHANT_LEVEL.get(Config.NORMAL_ARMOR_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.NORMAL_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_ARMOR_MAX;
			}
		}
		else if (item.getItem().getType2() == L2Item.TYPE2_ACCESSORY)
		{
			if (blessedScroll)
			{
				if (item.getEnchantLevel() >= Config.BLESS_JEWELRY_ENCHANT_LEVEL.size())
				{
					chance = Config.BLESS_JEWELRY_ENCHANT_LEVEL.get(Config.BLESS_JEWELRY_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.BLESS_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_JEWELRY_MAX;
			}
			else if (crystalScroll)
			{
				if (item.getEnchantLevel() >= Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.size())
				{
					chance = Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.get(Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
				maxEnchantLevel = Config.CRYSTAL_ENCHANT_MAX;
			}
			else
			{
				if (item.getEnchantLevel() >= Config.NORMAL_JEWELRY_ENCHANT_LEVEL.size())
				{
					chance = Config.NORMAL_JEWELRY_ENCHANT_LEVEL.get(Config.NORMAL_JEWELRY_ENCHANT_LEVEL.size());
				}
				else
				{
					chance = Config.NORMAL_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
				}
				
				maxEnchantLevel = Config.ENCHANT_JEWELRY_MAX;
			}
		}
		
		if (maxEnchantLevel != 0 && item.getEnchantLevel() >= maxEnchantLevel || item.getEnchantLevel() < minEnchantLevel)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
			return;
		}
		
		boolean destroyed = activeChar.destroyItemByItemId("Enchant", scroll.getItemId(), 1, activeChar, true);
		
		if (!destroyed)
		{
			Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar + " tried to enchant with a scroll he doesnt have", Config.DEFAULT_PUNISH);
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_ITEM_COUNT_2));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX || item.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR && item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX_FULL)
		{
			chance = 100;
		}
		
		int rndValue = Rnd.get(100);
		
		if (Config.ENABLE_DWARF_ENCHANT_BONUS && activeChar.getRace() == Race.dwarf)
		{
			if (activeChar.getLevel() >= Config.DWARF_ENCHANT_MIN_LEVEL)
			{
				rndValue -= Config.DWARF_ENCHANT_BONUS;
			}
		}
		
		final Object aChance = item.fireEvent("calcEnchantChance", new Object[chance]);
		if (aChance != null)
		{
			chance = (Integer) aChance;
		}
		synchronized (item)
		{
			if (rndValue < chance)
			{
				if (item.getOwnerId() != activeChar.getObjectId())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
					return;
				}
				
				if (item.getLocation() != L2ItemInstance.ItemLocation.INVENTORY && item.getLocation() != L2ItemInstance.ItemLocation.PAPERDOLL)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION));
					return;
				}
				
				if (item.getEnchantLevel() == 0)
				{
					sm = new SystemMessage(SystemMessageId.YOU_S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED);
					sm.addItemName(item.getItemId());
					activeChar.sendPacket(sm);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.YOUR_PLUS_S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getItemId());
					activeChar.sendPacket(sm);
				}
				
				item.setEnchantLevel(item.getEnchantLevel() + Config.CUSTOM_ENCHANT_VALUE);
				item.updateDatabase();
				
				String message = "[Successful] Player " + activeChar + ", item " + ItemTable.getInstance().getTemplate(item.getItemId()) + " from +" + oldEnchantLevel + " to +" + item.getEnchantLevel();
				L2Log.add(message, "enchant_item", "enchant_item");
			}
			else
			{
				if (blessedScroll)
				{
					sm = new SystemMessage(SystemMessageId.BLESSED_ENCHANT_FAILED);
					activeChar.sendPacket(sm);
					item.setEnchantLevel(Config.ENCHANT_LEVEL_WHEN_BLESSED_SCROLL_FAIL);
					item.updateDatabase();
				}
				else if (crystalScroll)
				{
					activeChar.sendMessage("Failed in Crystal Enchant. The enchant value of the item become to +" + Config.CRYSTAL_ENCHANT_MIN);
					item.setEnchantLevel(Config.CRYSTAL_ENCHANT_MIN);
					item.updateDatabase();
				}
				else
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = new SystemMessage(SystemMessageId.THE_ENCHANTMENT_HAS_FAILED_YOU_PLUS_S1_S2_HAS_BEEN_CRYSTALLIZED);
						sm.addNumber(item.getEnchantLevel());
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
						
						if(item.isEquipped())
						{
							sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
							sm.addNumber(item.getEnchantLevel());
							sm.addItemName(item.getItemId());
							activeChar.sendPacket(sm);
						}
					}
					else
					{
						sm = new SystemMessage(SystemMessageId.THE_ENCHANTMENT_HAS_FAILED_YOU_S1_HAS_BEEN_CRYSTALLIZED);
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
						
						if(item.isEquipped())
						{
							sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_DISARMED);
							sm.addItemName(item.getItemId());
							activeChar.sendPacket(sm);
						}
					}
					
					if (item.isEquipped())
					{
						if (item.isAugmented())
						{
							item.getAugmentation().removeBoni(activeChar);
						}
						
						L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getEquipSlot());
						
						InventoryUpdate iu = new InventoryUpdate();
						
						for (L2ItemInstance element : unequiped)
						{
							iu.addModifiedItem(element);
						}
						
						activeChar.sendPacket(iu);
						activeChar.broadcastUserInfo();
					}
					
					int count = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
					if (count < 1)
					{
						count = 1;
					}
					
					if (item.fireEvent("enchantFail", new Object[] {}) != null)
					{
						return;
					}
					
					L2ItemInstance destroyItem = activeChar.getInventory().destroyItem("Enchant", item, activeChar, null);
					
					if (destroyItem == null)
					{
						return;
					}
					
					L2ItemInstance crystals = activeChar.getInventory().addItem("Enchant", item.getItem().getCrystalItemId(), count, activeChar, destroyItem);
					
					sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1S);
					sm.addItemName(crystals.getItemId());
					sm.addNumber(count);
					activeChar.sendPacket(sm);
					
					if (!Config.FORCE_INVENTORY_UPDATE)
					{
						InventoryUpdate iu = new InventoryUpdate();
						
						if (destroyItem.getCount() == 0)
						{
							iu.addRemovedItem(destroyItem);
						}
						else
						{
							iu.addModifiedItem(destroyItem);
						}
						
						iu.addItem(crystals);
						activeChar.sendPacket(iu);
					}
					else
					{
						activeChar.sendPacket(new ItemList(activeChar, true));
					}
					
					L2World world = L2World.getInstance();
					world.removeObject(destroyItem);
				}
				
				String message = "[Fail] Player " + activeChar + ", item " + ItemTable.getInstance().getTemplate(item.getItemId()) + " from +" + oldEnchantLevel;
				L2Log.add(message, "enchant_item", "enchant_item");
			}
		}
		
		StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);
		
		EngineModsManager.onEnchant(activeChar);
		
		activeChar.sendPacket(new EnchantResult(item.getEnchantLevel())); // FIXME i'm really not sure about this...
		activeChar.sendPacket(new ItemList(activeChar, false)); // TODO update only the enchanted item
		activeChar.broadcastUserInfo();
	}
	
	@Override
	public String getType()
	{
		return "[C] 58 RequestEnchantItem";
	}
}
