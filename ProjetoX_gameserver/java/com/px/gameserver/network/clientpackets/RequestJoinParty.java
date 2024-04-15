package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.LootRule;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.player.BlockList;
import com.px.gameserver.model.group.Party;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.AskJoinParty;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestJoinParty extends L2GameClientPacket
{
	private String _name;
	private int _itemDistribution;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_itemDistribution = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player requestor = getClient().getPlayer();
		if (requestor == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_name);
		if (target == null)
		{
			requestor.sendPacket(SystemMessageId.FIRST_SELECT_USER_TO_INVITE_TO_PARTY);
			return;
		}
		
		if (BlockList.isBlocked(target, requestor))
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addCharName(target));
			return;
		}
		
		if (target.equals(requestor) || target.isCursedWeaponEquipped() || requestor.isCursedWeaponEquipped() || target.getAppearance().getInvisible())
		{
			requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}
		
		if (target.isInParty())
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_IN_PARTY).addCharName(target));
			return;
		}
		
		if (target.getClient().isDetached())
		{
			requestor.sendMessage("The player you tried to invite is in offline mode.");
			return;
		}
		
		if (target.isInJail() || requestor.isInJail())
		{
			requestor.sendMessage("The player you tried to invite is currently jailed.");
			return;
		}
		
		if (target.isInOlympiadMode() || requestor.isInOlympiadMode())
			return;
		
		if (requestor.isProcessingRequest())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (target.isProcessingRequest())
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
			return;
		}
		
		final Party party = requestor.getParty();
		if (party != null)
		{
			if (!party.isLeader(requestor))
			{
				requestor.sendPacket(SystemMessageId.ONLY_LEADER_CAN_INVITE);
				return;
			}
			
			if (party.getMembersCount() >= 9)
			{
				requestor.sendPacket(SystemMessageId.PARTY_FULL);
				return;
			}
			
			if (party.getPendingInvitation() && !party.isInvitationRequestExpired())
			{
				requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
				return;
			}
			
			party.setPendingInvitation(true);
		}
		else
			requestor.setLootRule(LootRule.VALUES[_itemDistribution]);
		
		requestor.onTransactionRequest(target);
		requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY).addCharName(target));
		
		target.sendPacket(new AskJoinParty(requestor.getName(), (party != null) ? party.getLootRule().ordinal() : _itemDistribution));
	}
}