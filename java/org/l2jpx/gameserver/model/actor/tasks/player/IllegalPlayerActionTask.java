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
package org.l2jpx.gameserver.model.actor.tasks.player;

import java.util.logging.Logger;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.AdminData;
import org.l2jpx.gameserver.enums.IllegalActionPunishmentType;
import org.l2jpx.gameserver.instancemanager.PunishmentManager;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.punishment.PunishmentAffect;
import org.l2jpx.gameserver.model.punishment.PunishmentTask;
import org.l2jpx.gameserver.model.punishment.PunishmentType;
import org.l2jpx.gameserver.network.Disconnection;
import org.l2jpx.gameserver.network.serverpackets.LeaveWorld;

/**
 * Task that handles illegal player actions.
 */
public class IllegalPlayerActionTask implements Runnable
{
	private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");
	
	private final String _message;
	private final IllegalActionPunishmentType _punishment;
	private final Player _actor;
	
	public IllegalPlayerActionTask(Player actor, String message, IllegalActionPunishmentType punishment)
	{
		_message = message;
		_punishment = punishment;
		_actor = actor;
		
		switch (punishment)
		{
			case KICK:
			{
				_actor.sendMessage("You will be kicked for illegal action, GM informed.");
				break;
			}
			case KICKBAN:
			{
				if (!_actor.isGM())
				{
					_actor.setAccessLevel(-1, false, true);
					_actor.setAccountAccesslevel(-1);
				}
				_actor.sendMessage("You are banned for illegal action, GM informed.");
				break;
			}
			case JAIL:
			{
				_actor.sendMessage("Illegal action performed!");
				_actor.sendMessage("You will be teleported to GM Consultation Service area and jailed.");
				break;
			}
		}
	}
	
	@Override
	public void run()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("AUDIT, ");
		sb.append(_message);
		sb.append(", ");
		sb.append(_actor);
		sb.append(", ");
		sb.append(_punishment);
		AUDIT_LOGGER.info(sb.toString());
		
		if (!_actor.isGM())
		{
			switch (_punishment)
			{
				case BROADCAST:
				{
					AdminData.getInstance().broadcastMessageToGMs(_message);
					return;
				}
				case KICK:
				{
					Disconnection.of(_actor).defaultSequence(LeaveWorld.STATIC_PACKET);
					break;
				}
				case KICKBAN:
				{
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(_actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN, System.currentTimeMillis() + (Config.DEFAULT_PUNISH_PARAM * 1000), _message, getClass().getSimpleName()));
					break;
				}
				case JAIL:
				{
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(_actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + (Config.DEFAULT_PUNISH_PARAM * 1000), _message, getClass().getSimpleName()));
					break;
				}
			}
		}
	}
}
