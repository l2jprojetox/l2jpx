package net.l2jpx.gameserver.handler.itemhandlers.custom;

import net.l2jpx.Config;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.network.serverpackets.RadarControl;
import net.l2jpx.gameserver.network.serverpackets.ShowMiniMap;

public class QuestHelp implements IItemHandler
{
	private static final int[] ITEM_IDS =
			{
					Config.ID_QUESTHELP
	};
	
	@Override
	public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		final int itemId = item.getItemId();

		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/questhelp" + itemId + ".htm");
		activeChar.sendPacket(html);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
