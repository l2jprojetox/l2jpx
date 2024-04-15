package com.px.gameserver.network.clientpackets;

import com.px.Config;
import com.px.gameserver.data.manager.PetitionManager;
import com.px.gameserver.data.xml.AdminData;
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
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		if (PetitionManager.getInstance().isPlayerInConsultation(activeChar))
		{
			if (activeChar.isGM())
				PetitionManager.getInstance().endActivePetition(activeChar);
			else
				activeChar.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
		}
		else
		{
			if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
			{
				if (PetitionManager.getInstance().cancelActivePetition(activeChar))
				{
					int numRemaining = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_CANCELED_SUBMIT_S1_MORE_TODAY).addNumber(numRemaining));
					
					// Notify all GMs that the player's pending petition has been cancelled.
					String msgContent = activeChar.getName() + " has canceled a pending petition.";
					AdminData.getInstance().broadcastToGMs(new CreatureSay(activeChar.getObjectId(), 17, "Petition System", msgContent));
				}
				else
					activeChar.sendPacket(SystemMessageId.FAILED_CANCEL_PETITION_TRY_LATER);
			}
			else
				activeChar.sendPacket(SystemMessageId.PETITION_NOT_SUBMITTED);
		}
	}
}