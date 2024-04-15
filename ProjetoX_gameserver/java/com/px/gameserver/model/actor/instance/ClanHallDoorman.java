package com.px.gameserver.model.actor.instance;

import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.data.sql.ClanTable;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.clanhall.ClanHall;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.network.serverpackets.ActionFailed;
import com.px.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * An instance type extending {@link Doorman}, used by clan hall doorman. The clan hall is linked during NPC spawn, based on distance.<br>
 * <br>
 * isOwnerClan() checks if the user is part of clan owning the clan hall.
 */
public class ClanHallDoorman extends Doorman
{
	private ClanHall _clanHall;
	
	public ClanHallDoorman(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		if (_clanHall == null)
			return;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		final Clan owner = ClanTable.getInstance().getClan(_clanHall.getOwnerId());
		if (isOwnerClan(player))
		{
			html.setFile("data/html/clanHallDoormen/doormen.htm");
			html.replace("%clanname%", owner.getName());
		}
		else
		{
			if (owner != null && owner.getLeader() != null)
			{
				html.setFile("data/html/clanHallDoormen/doormen-no.htm");
				html.replace("%leadername%", owner.getLeaderName());
				html.replace("%clanname%", owner.getName());
			}
			else
			{
				html.setFile("data/html/clanHallDoormen/emptyowner.htm");
				html.replace("%hallname%", _clanHall.getName());
			}
		}
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		showChatWindow(player);
	}
	
	@Override
	protected final void openDoors(Player player, String command)
	{
		_clanHall.openCloseDoors(true);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/clanHallDoormen/doormen-opened.htm");
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	protected final void closeDoors(Player player, String command)
	{
		_clanHall.openCloseDoors(false);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/clanHallDoormen/doormen-closed.htm");
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	protected final boolean isOwnerClan(Player player)
	{
		return _clanHall != null && player.getClan() != null && player.getClanId() == _clanHall.getOwnerId();
	}
	
	@Override
	public void onSpawn()
	{
		_clanHall = ClanHallManager.getInstance().getNearestClanHall(getX(), getY(), 500);
		if (_clanHall == null)
			LOGGER.warn("Couldn't find the nearest ClanHall for {}.", toString());
		
		super.onSpawn();
	}
}