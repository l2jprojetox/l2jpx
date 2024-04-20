package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.data.manager.PetitionManager;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.model.Petition;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.SystemMessageId;

public class ChatPetition implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PETITION_PLAYER,
		SayType.PETITION_GM
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
		if (petition == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_PETITION_CHAT);
			return;
		}
		
		petition.sendMessage(player, text);
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}