package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.PartyMatchRoom;
import net.l2jpx.gameserver.model.PartyMatchRoomList;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.ExClosePartyRoom;
import net.l2jpx.gameserver.network.serverpackets.ExPartyRoomMember;
import net.l2jpx.gameserver.network.serverpackets.PartyMatchDetail;

public final class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2Party party = player.getParty();
		
		if (party != null)
		{
			if (party.isInDimensionalRift() && !party.getDimensionalRift().getRevivedAtWaitingRoom().contains(player))
			{
				player.sendMessage("You can't exit party when you are in Dimensional Rift.");
			}
			else
			{
				party.removePartyMember(player);
				
				if (player.isInPartyMatchRoom())
				{
					final PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
					if (room != null)
					{
						player.sendPacket(new PartyMatchDetail(player, room));
						player.sendPacket(new ExPartyRoomMember(player, room, 0));
						player.sendPacket(new ExClosePartyRoom());
						
						room.deleteMember(player);
					}
					player.setPartyRoom(0);
					player.broadcastUserInfo();
				}
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 2B RequestWithDrawalParty";
	}
}