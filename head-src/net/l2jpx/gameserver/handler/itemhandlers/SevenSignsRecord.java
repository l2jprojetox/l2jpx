package net.l2jpx.gameserver.handler.itemhandlers;

import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.serverpackets.SSQStatus;

/**
 * @author Tempy
 * @author ReynalDev
 */
public class SevenSignsRecord implements IItemHandler
{
	private static final int[] ITEM_IDS =
	{
		5707
	};
	
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (playable instanceof L2PcInstance)
		{
			L2PcInstance activeChar = (L2PcInstance) playable;
			SSQStatus ssqs = new SSQStatus(activeChar, 1);
			activeChar.sendPacket(ssqs);
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
