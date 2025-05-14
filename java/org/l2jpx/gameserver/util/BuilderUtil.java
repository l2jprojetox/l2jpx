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
package org.l2jpx.gameserver.util;

import org.l2jpx.Config;
import org.l2jpx.gameserver.data.xml.SendMessageLocalisationData;
import org.l2jpx.gameserver.enums.ChatType;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.network.serverpackets.CreatureSay;
import org.l2jpx.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;

/**
 * @author lord_rex
 */
public class BuilderUtil
{
	private BuilderUtil()
	{
		// utility class
	}
	
	/**
	 * Sends builder system message to the player.
	 * @param player
	 * @param message
	 */
	public static void sendSysMessage(Player player, String message)
	{
		if (Config.GM_STARTUP_BUILDER_HIDE)
		{
			player.sendPacket(new CreatureSay(null, ChatType.GENERAL, "SYS", SendMessageLocalisationData.getLocalisation(player, message)));
		}
		else
		{
			player.sendMessage(message);
		}
	}
	
	/**
	 * Sends builder html message to the player.
	 * @param player
	 * @param message
	 */
	public static void sendHtmlMessage(Player player, String message)
	{
		player.sendPacket(new CreatureSay(null, ChatType.GENERAL, "HTML", message));
	}
	
	/**
	 * Changes player's hiding state.
	 * @param player
	 * @param hide
	 * @return {@code true} if hide state was changed, otherwise {@code false}
	 */
	public static boolean setHiding(Player player, boolean hide)
	{
		if (player.hasEnteredWorld())
		{
			if (player.isInvisible() && hide)
			{
				// already hiding
				return false;
			}
			
			if (!player.isInvisible() && !hide)
			{
				// already visible
				return false;
			}
		}
		
		player.setSilenceMode(hide);
		player.setInvul(hide);
		player.setInvisible(hide);
		
		player.broadcastUserInfo();
		player.sendPacket(new ExUserInfoAbnormalVisualEffect(player));
		return true;
	}
}
