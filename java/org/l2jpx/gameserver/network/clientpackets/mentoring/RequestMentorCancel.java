/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.network.clientpackets.mentoring;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.sql.CharInfoTable;
import org.l2jpx.gameserver.instancemanager.MentorManager;
import org.l2jpx.gameserver.model.Mentee;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.events.EventDispatcher;
import org.l2jpx.gameserver.model.events.EventType;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerMenteeLeft;
import org.l2jpx.gameserver.model.events.impl.creature.player.OnPlayerMenteeRemove;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author UnAfraid
 */
public class RequestMentorCancel extends ClientPacket
{
	private int _confirmed;
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_confirmed = readInt();
		_name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		if (_confirmed != 1)
		{
			return;
		}
		
		final Player player = getPlayer();
		final int objectId = CharInfoTable.getInstance().getIdByName(_name);
		if (player != null)
		{
			if (player.isMentor())
			{
				final Mentee mentee = MentorManager.getInstance().getMentee(player.getObjectId(), objectId);
				if (mentee != null)
				{
					MentorManager.getInstance().cancelAllMentoringBuffs(mentee.getPlayer());
					
					if (MentorManager.getInstance().isAllMenteesOffline(player.getObjectId(), mentee.getObjectId()))
					{
						MentorManager.getInstance().cancelAllMentoringBuffs(player);
					}
					
					player.sendPacket(new SystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS).addString(_name));
					MentorManager.getInstance().setPenalty(player.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
					MentorManager.getInstance().deleteMentor(player.getObjectId(), mentee.getObjectId());
					
					// Notify to scripts
					if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_MENTEE_REMOVE, player))
					{
						EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeRemove(player, mentee), player);
					}
				}
			}
			else if (player.isMentee())
			{
				final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
				if ((mentor != null) && (mentor.getObjectId() == objectId))
				{
					MentorManager.getInstance().cancelAllMentoringBuffs(player);
					
					if (MentorManager.getInstance().isAllMenteesOffline(mentor.getObjectId(), player.getObjectId()))
					{
						MentorManager.getInstance().cancelAllMentoringBuffs(mentor.getPlayer());
					}
					
					MentorManager.getInstance().setPenalty(mentor.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_LEAVE);
					MentorManager.getInstance().deleteMentor(mentor.getObjectId(), player.getObjectId());
					
					// Notify to scripts
					if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_MENTEE_LEFT, player))
					{
						EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeLeft(mentor, player), player);
					}
					
					mentor.getPlayer().sendPacket(new SystemMessage(SystemMessageId.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_TWO_DAYS).addString(_name));
				}
			}
		}
	}
}
