package com.l2jpx.gameserver.handler.itemhandlers;

import com.l2jpx.gameserver.data.manager.CastleManager;
import com.l2jpx.gameserver.data.manager.SevenSignsManager;
import com.l2jpx.gameserver.enums.SealType;
import com.l2jpx.gameserver.handler.IItemHandler;
import com.l2jpx.gameserver.model.actor.Playable;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.entity.Castle;
import com.l2jpx.gameserver.model.item.MercenaryTicket;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.network.SystemMessageId;
import com.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * Handler to use mercenary tickets.<br>
 * <br>
 * Check constraints:
 * <ul>
 * <li>Only specific tickets may be used in each castle (different tickets for each castle)</li>
 * <li>Only the owner of that castle may use them</li>
 * <li>tickets cannot be used during siege</li>
 * <li>Check if max number of tickets from this ticket's TYPE has been reached</li>
 * </ul>
 * If allowed, spawn the item in the world and remove it from the player's inventory.
 */
public class MercenaryTickets implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final Player player = (Player) playable;
		if (player == null)
			return;
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle == null)
			return;
		
		final int castleId = castle.getCastleId();
		
		// Castle lord check.
		if (!player.isCastleLord(castleId))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_AUTHORITY_TO_POSITION_MERCENARIES);
			return;
		}
		
		final int itemId = item.getItemId();
		final MercenaryTicket ticket = castle.getTicket(itemId);
		
		// Valid ticket for castle check.
		if (ticket == null)
		{
			player.sendPacket(SystemMessageId.MERCENARIES_CANNOT_BE_POSITIONED_HERE);
			return;
		}
		
		// Siege in progress check.
		if (castle.getSiege().isInProgress())
		{
			player.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return;
		}
		
		// Seal validation check.
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			player.sendPacket(SystemMessageId.MERC_CAN_BE_ASSIGNED);
			return;
		}
		
		// Seal of Strife owner check.
		if (!ticket.isSsqType(SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)))
		{
			player.sendPacket(SystemMessageId.MERC_CANT_BE_ASSIGNED_USING_STRIFE);
			return;
		}
		
		// Max amount check.
		if (castle.getDroppedTicketsCount(itemId) >= ticket.getMaxAmount())
		{
			player.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return;
		}
		
		// Distance check.
		if (castle.isTooCloseFromDroppedTicket(player.getX(), player.getY(), player.getZ()))
		{
			player.sendPacket(SystemMessageId.POSITIONING_CANNOT_BE_DONE_BECAUSE_DISTANCE_BETWEEN_MERCENARIES_TOO_SHORT);
			return;
		}
		
		final ItemInstance droppedTicket = player.dropItem("Consume", item.getObjectId(), 1, player.getX(), player.getY(), player.getZ(), null, false);
		if (droppedTicket == null)
			return;
		
		castle.addDroppedTicket(droppedTicket);
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PLACE_S1_IN_CURRENT_LOCATION_AND_DIRECTION).addItemName(itemId));
	}
}