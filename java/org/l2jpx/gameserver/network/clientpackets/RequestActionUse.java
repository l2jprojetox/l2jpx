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
package org.l2jpx.gameserver.network.clientpackets;

import java.util.Arrays;

import org.l2jpx.gameserver.data.xml.ActionData;
import org.l2jpx.gameserver.enums.PrivateStoreType;
import org.l2jpx.gameserver.handler.IPlayerActionHandler;
import org.l2jpx.gameserver.handler.PlayerActionHandler;
import org.l2jpx.gameserver.model.ActionDataHolder;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.actor.transform.TransformTemplate;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.model.skill.AbnormalType;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.network.PacketLogger;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.RecipeShopManageList;

/**
 * This class manages the action use request 
 * @author Zoey76
 */
public class RequestActionUse extends ClientPacket
{
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_actionId = readInt();
		_ctrlPressed = readInt() == 1;
		_shiftPressed = readByte() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Don't do anything if player is dead or confused
		if ((player.isFakeDeath() && (_actionId != 0)) || player.isDead() || player.isControlBlocked())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(_actionId))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// Don't allow to do some action if player is transformed
		if (player.isTransformed())
		{
			final TransformTemplate transformTemplate = player.getTransformation().get().getTemplate(player);
			final int[] allowedActions = transformTemplate.getBasicActionList();
			if ((allowedActions == null) || (Arrays.binarySearch(allowedActions, _actionId) < 0))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				PacketLogger.warning(player + " used action which he does not have! Id = " + _actionId + " transform: " + player.getTransformation().get().getId());
				return;
			}
		}
		
		final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(_actionId);
		if (actionHolder != null)
		{
			final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
			if (actionHandler != null)
			{
				actionHandler.useAction(player, actionHolder, _ctrlPressed, _shiftPressed);
				return;
			}
			PacketLogger.warning("Couldn't find handler with name: " + actionHolder.getHandler());
			return;
		}
		
		switch (_actionId)
		{
			case 51: // General Manufacture
			{
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (player.isAlikeDead())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (player.isSellingBuffs())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (player.isInStoreMode())
				{
					player.setPrivateStoreType(PrivateStoreType.NONE);
					player.broadcastUserInfo();
				}
				if (player.isSitting())
				{
					player.standUp();
				}
				
				player.sendPacket(new RecipeShopManageList(player, false));
				break;
			}
			default:
			{
				PacketLogger.warning(player.getName() + ": unhandled action type " + _actionId);
				break;
			}
		}
	}
}
