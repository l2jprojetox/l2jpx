package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.AskJoinFriend;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendInvite extends L2GameClientPacket
{
	private String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		SystemMessage sm;
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		final L2PcInstance friend = L2World.getInstance().getPlayerByName(name);
		
		if (friend == null)
		{
			// Target is not found in the game.
			sm = new SystemMessage(SystemMessageId.THE_USER_WHO_YOU_REQUESTED_TO_BECOME_FRIEDS_IS_NOT_FOUND_IN_THE_GAME);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		
		if (friend == activeChar)
		{
			// You cannot add yourself to your own friend list.
			sm = new SystemMessage(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		
		if (activeChar.getBlockList().isInBlockList(name))
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
			activeChar.sendPacket(sm);
			return;
		}
		
		if (friend.getBlockList().isInBlockList(activeChar.getName()))
		{
			sm = new SystemMessage(SystemMessageId.S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
			sm.addString(friend.getName());
			activeChar.sendPacket(sm);
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
			activeChar.sendPacket(sm);
			return;
		}
		
		if (activeChar.isInCombat() || friend.isInCombat())
		{
			sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		
		if (activeChar.getFriendList().contains(friend.getName()))
		{
			// Player already is in your friendlist
			sm = new SystemMessage(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_IN_YOUR_FRIENDS_LIST);
			sm.addString(name);
			activeChar.sendPacket(sm);
			return;
		}
		
		if (!friend.isProcessingRequest())
		{
			// requets to become friend
			activeChar.onTransactionRequest(friend);
			sm = new SystemMessage(SystemMessageId.S1_HAS_REQUESTED_TO_BECOME_FRIENDS);
			sm.addString(name);
			final AskJoinFriend ajf = new AskJoinFriend(activeChar.getName());
			friend.sendPacket(ajf);
		}
		else
		{
			sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_PLEASE_TRY_AGAIN_LATER);
		}
		
		friend.sendPacket(sm);
		
	}
	
	@Override
	public String getType()
	{
		return "[C] 5E RequestFriendInvite";
	}
}