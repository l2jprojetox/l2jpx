// Hero Custom Item , Created By Stefoulis15
// Added From Stefoulis15 Into The Core.
// Visit www.MaxCheaters.com For Support 
// Source File Name:   HeroCustomItem.java
// Modded by programmos, sword dev
package net.l2jpx.gameserver.handler.itemhandlers;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.SocialAction;

public class HeroCustomItem implements IItemHandler
{
	protected static final Logger LOGGER = Logger.getLogger(HeroCustomItem.class);
	
	private static final int ITEM_IDS[] =
	{
		Config.HERO_CUSTOM_ITEM_ID
	};
	
	@Override
	public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("This item cannot be used on Olympiad Games.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isHero())
		{
			activeChar.sendMessage("You are already hero!");
		}
		else
		{
			activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 16));
			activeChar.setHero(true);
			
			long heroEndDate = 0;
			
			if (Config.HERO_CUSTOM_DAY == 0)
			{
				activeChar.setHeroEndDate(heroEndDate);
			}
			else if (Config.HERO_CUSTOM_DAY > 0)
			{
				heroEndDate = (Config.HERO_CUSTOM_DAY * 24L * 60L * 60L * 1000L) + System.currentTimeMillis();
				activeChar.setHeroEndDate(heroEndDate);
			}
			
			activeChar.broadcastUserInfo();
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			activeChar.getInventory().addItem("Wings", 6842, 1, activeChar, null);
			
			activeChar.setVariable(L2PcInstance.HERO_END, heroEndDate, true);
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}