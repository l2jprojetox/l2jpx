package com.px.gameserver.network.clientpackets;

import com.px.Config;
import com.px.gameserver.data.manager.PetitionManager;
import com.px.gameserver.data.xml.AdminData;
import com.px.gameserver.enums.SayType;
import com.px.gameserver.enums.petitions.PetitionState;
import com.px.gameserver.model.Petition;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.CreatureSay;
import com.px.gameserver.network.serverpackets.SystemMessage;

public final class RequestPetitionCancel extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
		if (petition != null)
		{
			// Regular Player can't end the Petition.
			if (petition.getPetitionerObjectId() == player.getObjectId())
				player.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
			// Part of responders - leave conversation properly or end active petition.
			else if (petition.getResponders().contains(player.getObjectId()))
			{
				if (player.isGM())
					petition.endConsultation(PetitionState.CLOSED);
				else
					petition.removeAdditionalResponder(player);
			}
			return;
		}
		
		if (!PetitionManager.getInstance().cancelPendingPetition(player))
		{
			player.sendPacket(SystemMessageId.PETITION_NOT_SUBMITTED);
			return;
		}
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_CANCELED_SUBMIT_S1_MORE_TODAY).addNumber(Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPetitionsCount(player)));
		
		// Notify all GMs that the player's pending petition has been cancelled.
		AdminData.getInstance().broadcastToGMs(new CreatureSay(player.getObjectId(), SayType.HERO_VOICE, "Petition System", player.getName() + " has canceled a pending petition."));
	}
}