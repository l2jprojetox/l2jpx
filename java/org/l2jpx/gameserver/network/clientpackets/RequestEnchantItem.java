/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.network.clientpackets;

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.commons.util.Rnd;
import org.l2jpx.gameserver.data.xml.EnchantItemData;
import org.l2jpx.gameserver.enums.ItemSkillType;
import org.l2jpx.gameserver.enums.UserInfoType;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jpx.gameserver.model.item.ItemTemplate;
import org.l2jpx.gameserver.model.item.enchant.EnchantResultType;
import org.l2jpx.gameserver.model.item.enchant.EnchantScroll;
import org.l2jpx.gameserver.model.item.enchant.EnchantSupportItem;
import org.l2jpx.gameserver.model.item.instance.Item;
import org.l2jpx.gameserver.model.skill.CommonSkill;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.EnchantResult;
import org.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jpx.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.util.Util;

public class RequestEnchantItem extends ClientPacket
{
	protected static final Logger LOGGER_ENCHANT = Logger.getLogger("enchant.items");
	
	private int _objectId;
	private int _supportId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_supportId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final EnchantItemRequest request = player.getRequest(EnchantItemRequest.class);
		if ((request == null) || request.isProcessing())
		{
			return;
		}
		
		request.setEnchantingItem(_objectId);
		request.setProcessing(true);
		
		if (!player.isOnline() || getClient().isDetached())
		{
			player.removeRequest(request.getClass());
			return;
		}
		
		if (player.isProcessingTransaction() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.removeRequest(request.getClass());
			return;
		}
		
		final Item item = request.getEnchantingItem();
		final Item scroll = request.getEnchantingScroll();
		final Item support = request.getSupportItem();
		if ((item == null) || (scroll == null))
		{
			player.removeRequest(request.getClass());
			return;
		}
		
		// template for scroll
		final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
		if (scrollTemplate == null)
		{
			return;
		}
		
		// template for support item, if exist
		EnchantSupportItem supportTemplate = null;
		if (support != null)
		{
			if (support.getObjectId() != _supportId)
			{
				player.removeRequest(request.getClass());
				return;
			}
			supportTemplate = EnchantItemData.getInstance().getSupportItem(support);
		}
		
		// first validation check - also over enchant check
		if (!scrollTemplate.isValid(item, supportTemplate) || (Config.DISABLE_OVER_ENCHANTING && ((item.getEnchantLevel() == scrollTemplate.getMaxEnchantLevel()) || (!(item.getTemplate().getEnchantLimit() == 0) && (item.getEnchantLevel() == item.getTemplate().getEnchantLimit())))))
		{
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
			player.removeRequest(request.getClass());
			player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
			return;
		}
		
		// fast auto-enchant cheat check
		if ((request.getTimestamp() == 0) || ((System.currentTimeMillis() - request.getTimestamp()) < 2000))
		{
			Util.handleIllegalPlayerAction(player, player + " use autoenchant program ", Config.DEFAULT_PUNISH);
			player.removeRequest(request.getClass());
			player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
			return;
		}
		
		// attempting to destroy scroll
		if (player.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, player, item) == null)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			Util.handleIllegalPlayerAction(player, player + " tried to enchant with a scroll he doesn't have", Config.DEFAULT_PUNISH);
			player.removeRequest(request.getClass());
			player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
			return;
		}
		
		// attempting to destroy support if exist
		if ((support != null) && (player.getInventory().destroyItem("Enchant", support.getObjectId(), 1, player, item) == null))
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			Util.handleIllegalPlayerAction(player, player + " tried to enchant with a support item he doesn't have", Config.DEFAULT_PUNISH);
			player.removeRequest(request.getClass());
			player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
			return;
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		synchronized (item)
		{
			// last validation check
			if ((item.getOwnerId() != player.getObjectId()) || !item.isEnchantable())
			{
				player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.removeRequest(request.getClass());
				player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
				return;
			}
			
			final EnchantResultType resultType = scrollTemplate.calculateSuccess(player, item, supportTemplate);
			switch (resultType)
			{
				case ERROR:
				{
					player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
					player.removeRequest(request.getClass());
					player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
					break;
				}
				case SUCCESS:
				{
					final ItemTemplate it = item.getTemplate();
					// Increase enchant level only if scroll's base template has chance, some armors can success over +20 but they shouldn't have increased.
					if (scrollTemplate.getChance(player, item) > 0)
					{
						if (supportTemplate != null)
						{
							item.setEnchantLevel(Math.min(item.getEnchantLevel() + Rnd.get(supportTemplate.getRandomEnchantMin(), supportTemplate.getRandomEnchantMax()), supportTemplate.getMaxEnchantLevel()));
						}
						else
						{
							item.setEnchantLevel(Math.min(item.getEnchantLevel() + Rnd.get(scrollTemplate.getRandomEnchantMin(), scrollTemplate.getRandomEnchantMax()), scrollTemplate.getMaxEnchantLevel()));
						}
						item.updateDatabase();
					}
					player.sendPacket(new EnchantResult(EnchantResult.SUCCESS, item));
					if (Config.LOG_ITEM_ENCHANTS)
					{
						final StringBuilder sb = new StringBuilder();
						if (item.getEnchantLevel() > 0)
						{
							if (support == null)
							{
								LOGGER_ENCHANT.info(sb.append("Success, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
							}
							else
							{
								LOGGER_ENCHANT.info(sb.append("Success, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
							}
						}
						else if (support == null)
						{
							LOGGER_ENCHANT.info(sb.append("Success, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
						}
						else
						{
							LOGGER_ENCHANT.info(sb.append("Success, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
						}
					}
					
					// announce the success
					final int minEnchantAnnounce = item.isArmor() ? 6 : 7;
					final int maxEnchantAnnounce = item.isArmor() ? 0 : 15;
					if ((item.getEnchantLevel() == minEnchantAnnounce) || (item.getEnchantLevel() == maxEnchantAnnounce))
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3);
						sm.addString(player.getName());
						sm.addInt(item.getEnchantLevel());
						sm.addItemName(item);
						player.broadcastPacket(sm);
						
						final Skill skill = CommonSkill.FIREWORK.getSkill();
						if (skill != null)
						{
							player.broadcastPacket(new MagicSkillUse(player, player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
						}
					}
					
					if (item.isEquipped())
					{
						if (item.isArmor())
						{
							it.forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
							{
								// add skills bestowed from +4 armor
								if (item.getEnchantLevel() >= holder.getValue())
								{
									player.addSkill(holder.getSkill(), false);
									player.sendSkillList();
								}
							});
						}
						player.broadcastUserInfo(); // update user info
					}
					break;
				}
				case FAILURE:
				{
					if (scrollTemplate.isSafe())
					{
						// safe enchant - remain old value
						player.sendPacket(SystemMessageId.ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED);
						player.sendPacket(new EnchantResult(EnchantResult.SAFE_FAIL, item));
						if (Config.LOG_ITEM_ENCHANTS)
						{
							final StringBuilder sb = new StringBuilder();
							if (item.getEnchantLevel() > 0)
							{
								if (support == null)
								{
									LOGGER_ENCHANT.info(sb.append("Safe Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
								}
								else
								{
									LOGGER_ENCHANT.info(sb.append("Safe Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
								}
							}
							else if (support == null)
							{
								LOGGER_ENCHANT.info(sb.append("Safe Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
							}
							else
							{
								LOGGER_ENCHANT.info(sb.append("Safe Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
							}
						}
					}
					else
					{
						// unequip item on enchant failure to avoid item skills stack
						if (item.isEquipped())
						{
							if (item.getEnchantLevel() > 0)
							{
								final SystemMessage sm = new SystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
								sm.addInt(item.getEnchantLevel());
								sm.addItemName(item);
								player.sendPacket(sm);
							}
							else
							{
								final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
								sm.addItemName(item);
								player.sendPacket(sm);
							}
							
							for (Item itm : player.getInventory().unEquipItemInSlotAndRecord(item.getLocationSlot()))
							{
								iu.addModifiedItem(itm);
							}
							player.sendInventoryUpdate(iu);
							player.broadcastUserInfo();
						}
						
						if (scrollTemplate.isBlessed() || scrollTemplate.isBlessedDown() || ((supportTemplate != null) && supportTemplate.isBlessed()))
						{
							// blessed enchant - enchant value down by 1
							if (scrollTemplate.isBlessedDown())
							{
								item.setEnchantLevel(item.getEnchantLevel() - 1);
							}
							else // blessed enchant - clear enchant value
							{
								player.sendPacket(SystemMessageId.THE_BLESSED_ENCHANT_FAILED_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);
								item.setEnchantLevel(0);
							}
							item.updateDatabase();
							player.sendPacket(new EnchantResult(EnchantResult.BLESSED_FAIL, 0, 0));
							if (Config.LOG_ITEM_ENCHANTS)
							{
								final StringBuilder sb = new StringBuilder();
								if (item.getEnchantLevel() > 0)
								{
									if (support == null)
									{
										LOGGER_ENCHANT.info(sb.append("Blessed Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
									}
									else
									{
										LOGGER_ENCHANT.info(sb.append("Blessed Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
									}
								}
								else if (support == null)
								{
									LOGGER_ENCHANT.info(sb.append("Blessed Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
								}
								else
								{
									LOGGER_ENCHANT.info(sb.append("Blessed Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
								}
							}
						}
						else
						{
							// enchant failed, destroy item
							if (player.getInventory().destroyItem("Enchant", item, player, null) == null)
							{
								// unable to destroy item, cheater ?
								Util.handleIllegalPlayerAction(player, "Unable to delete item on enchant failure from " + player + ", possible cheater !", Config.DEFAULT_PUNISH);
								player.removeRequest(request.getClass());
								player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
								if (Config.LOG_ITEM_ENCHANTS)
								{
									final StringBuilder sb = new StringBuilder();
									if (item.getEnchantLevel() > 0)
									{
										if (support == null)
										{
											LOGGER_ENCHANT.info(sb.append("Unable to destroy, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
										}
										else
										{
											LOGGER_ENCHANT.info(sb.append("Unable to destroy, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
										}
									}
									else if (support == null)
									{
										LOGGER_ENCHANT.info(sb.append("Unable to destroy, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
									}
									else
									{
										LOGGER_ENCHANT.info(sb.append("Unable to destroy, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
									}
								}
								return;
							}
							
							World.getInstance().removeObject(item);
							
							int count = 0;
							if (item.getTemplate().isCrystallizable())
							{
								count = Math.max(0, item.getCrystalCount() - ((item.getTemplate().getCrystalCount() + 1) / 2));
							}
							
							Item crystals = null;
							final int crystalId = item.getTemplate().getCrystalItemId();
							if (count > 0)
							{
								crystals = player.getInventory().addItem("Enchant", crystalId, count, player, item);
								
								final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
								sm.addItemName(crystals);
								sm.addLong(count);
								player.sendPacket(sm);
							}
							
							// if (crystals != null)
							// {
							// iu.addItem(crystals); // FIXME: Packet never sent?
							// }
							
							if ((crystalId == 0) || (count == 0))
							{
								player.sendPacket(new EnchantResult(EnchantResult.NO_CRYSTAL, 0, 0));
							}
							else
							{
								player.sendPacket(new EnchantResult(EnchantResult.FAIL, crystalId, count));
							}
							
							if (Config.LOG_ITEM_ENCHANTS)
							{
								final StringBuilder sb = new StringBuilder();
								if (item.getEnchantLevel() > 0)
								{
									if (support == null)
									{
										LOGGER_ENCHANT.info(sb.append("Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
									}
									else
									{
										LOGGER_ENCHANT.info(sb.append("Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", +").append(item.getEnchantLevel()).append(" ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
									}
								}
								else if (support == null)
								{
									LOGGER_ENCHANT.info(sb.append("Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("]").toString());
								}
								else
								{
									LOGGER_ENCHANT.info(sb.append("Fail, Character:").append(player.getName()).append(" [").append(player.getObjectId()).append("] Account:").append(player.getAccountName()).append(" IP:").append(player.getIPAddress()).append(", ").append(item.getName()).append("(").append(item.getCount()).append(") [").append(item.getObjectId()).append("], ").append(scroll.getName()).append("(").append(scroll.getCount()).append(") [").append(scroll.getObjectId()).append("], ").append(support.getName()).append("(").append(support.getCount()).append(") [").append(support.getObjectId()).append("]").toString());
								}
							}
						}
					}
					break;
				}
			}
			
			player.sendItemList(true);
			
			request.setProcessing(false);
			player.broadcastUserInfo(UserInfoType.ENCHANTLEVEL);
		}
	}
}
