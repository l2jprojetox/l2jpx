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

import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.clientpackets.ClientPacket;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;
import org.l2jpx.gameserver.network.serverpackets.mentoring.ExMentorAdd;

/**
 * @author Gnacik, UnAfraid
 */
public class RequestMenteeAdd extends ClientPacket
{
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_target = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player mentor = getPlayer();
		if (mentor == null)
		{
			return;
		}
		
		final Player mentee = World.getInstance().getPlayer(_target);
		if (mentee == null)
		{
			return;
		}
		
		if (ConfirmMenteeAdd.validate(mentor, mentee))
		{
			mentor.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_OFFERED_TO_BECOME_S1_S_MENTOR).addString(mentee.getName()));
			mentee.sendPacket(new ExMentorAdd(mentor));
		}
	}
}