package net.l2jpx.gameserver.model.actor.instance;

import net.l2jpx.gameserver.datatables.xml.HennaTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.HennaEquipList;
import net.l2jpx.gameserver.network.serverpackets.HennaRemoveList;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

public class L2SymbolMakerInstance extends L2FolkInstance
{
	@Override
	public void onBypassFeedback(final L2PcInstance player, final String command)
	{
		if (command.equals("Draw"))
		{
			final L2HennaInstance[] henna = HennaTable.getInstance().getAvailableHenna(player.getClassId());
			final HennaEquipList hel = new HennaEquipList(player, henna);
			player.sendPacket(hel);
			
			player.sendPacket(new ItemList(player, false));
		}
		else if (command.equals("RemoveList"))
		{
			boolean hasHennas = false;
			for (int i = 1; i <= 3; i++)
			{
				if (player.getHennas(i) != null)
					hasHennas = true;
			}
			
			if (hasHennas)
				player.sendPacket(new HennaRemoveList(player));
			else
				player.sendPacket(SystemMessageId.THE_SYMBOL_INFORMATION_CANNOT_BE_FOUND);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	public L2SymbolMakerInstance(final int objectID, final L2NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public String getHtmlPath(final int npcId, final int val)
	{
		return "data/html/symbolmaker/SymbolMaker.htm";
	}
	
	@Override
	public boolean isAutoAttackable(final L2Character attacker)
	{
		return false;
	}
}
