package com.px.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import com.px.gameserver.data.manager.PetitionManager;
import com.px.gameserver.enums.PetitionState;
import com.px.gameserver.enums.PetitionType;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.CreatureSay;
import com.px.gameserver.network.serverpackets.L2GameServerPacket;
import com.px.gameserver.network.serverpackets.SystemMessage;

/**
 * A Petition is a report, generally made by a {@link Player} to a Game Master. The categories of report are multiple.
 */
public class Petition
{
	private final List<CreatureSay> _messageLog = new ArrayList<>();
	
	private final int _id;
	private final PetitionType _type;
	private final Player _petitioner;
	private final long _submitTime = System.currentTimeMillis();
	private final String _content;
	
	private PetitionState _state = PetitionState.PENDING;
	private Player _responder;
	
	public Petition(Player petitioner, String content, int type)
	{
		type--;
		
		_id = IdFactory.getInstance().getNextId();
		_type = PetitionType.values()[type];
		_content = content;
		_petitioner = petitioner;
	}
	
	public boolean addLogMessage(CreatureSay cs)
	{
		return _messageLog.add(cs);
	}
	
	public List<CreatureSay> getLogMessages()
	{
		return _messageLog;
	}
	
	public boolean endPetitionConsultation(PetitionState endState)
	{
		setState(endState);
		
		if (_responder != null && _responder.isOnline())
		{
			if (endState == PetitionState.RESPONDER_REJECT)
				_petitioner.sendMessage("Your petition was rejected. Please try again later.");
			else
			{
				// Ending petition consultation with <Player>.
				_responder.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_ENDED_WITH_S1).addCharName(_petitioner));
				
				// Receipt No. <ID> petition cancelled.
				if (endState == PetitionState.PETITIONER_CANCEL)
					_responder.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RECENT_NO_S1_CANCELED).addNumber(getId()));
			}
		}
		
		// End petition consultation and inform them, if they are still online.
		if (_petitioner != null && _petitioner.isOnline())
			_petitioner.sendPacket(SystemMessageId.THIS_END_THE_PETITION_PLEASE_PROVIDE_FEEDBACK);
		
		PetitionManager.getInstance().getCompletedPetitions().put(getId(), this);
		return PetitionManager.getInstance().getPendingPetitions().remove(getId()) != null;
	}
	
	public String getContent()
	{
		return _content;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public Player getPetitioner()
	{
		return _petitioner;
	}
	
	public Player getResponder()
	{
		return _responder;
	}
	
	public long getSubmitTime()
	{
		return _submitTime;
	}
	
	public PetitionState getState()
	{
		return _state;
	}
	
	public String getTypeAsString()
	{
		return _type.toString().replace("_", " ");
	}
	
	public void sendPetitionerPacket(L2GameServerPacket responsePacket)
	{
		if (_petitioner == null || !_petitioner.isOnline())
			return;
		
		_petitioner.sendPacket(responsePacket);
	}
	
	public void sendResponderPacket(L2GameServerPacket responsePacket)
	{
		if (_responder == null || !_responder.isOnline())
		{
			endPetitionConsultation(PetitionState.RESPONDER_MISSING);
			return;
		}
		
		_responder.sendPacket(responsePacket);
	}
	
	public void setState(PetitionState state)
	{
		_state = state;
	}
	
	public void setResponder(Player respondingAdmin)
	{
		if (_responder != null)
			return;
		
		_responder = respondingAdmin;
	}
}