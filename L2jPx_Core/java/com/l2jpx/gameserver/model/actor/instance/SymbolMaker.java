package com.l2jpx.gameserver.model.actor.instance;

import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.template.NpcTemplate;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.HennaEquipList;
import com.l2jpx.gameserver.network.serverpackets.HennaUnequipList;

public class SymbolMaker extends Folk
{
	public SymbolMaker(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.equals("Draw"))
			player.sendPacket(new HennaEquipList(player));
		else if (command.equals("RemoveList"))
		{
			if (player.getHennaList().isEmpty())
			{
				player.sendPacket(SystemMessageId.SYMBOL_NOT_FOUND);
				return;
			}
			
			player.sendPacket(new HennaUnequipList(player));
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/symbolmaker/SymbolMaker.htm";
	}
}