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

import org.l2jpx.Config;
import org.l2jpx.commons.threads.ThreadPool;
import org.l2jpx.gameserver.data.BotReportTable;
import org.l2jpx.gameserver.data.xml.FakePlayerData;
import org.l2jpx.gameserver.model.BlockList;
import org.l2jpx.gameserver.model.World;
import org.l2jpx.gameserver.model.WorldObject;
import org.l2jpx.gameserver.model.actor.Npc;
import org.l2jpx.gameserver.model.actor.Player;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.model.skill.AbnormalType;
import org.l2jpx.gameserver.model.skill.BuffInfo;
import org.l2jpx.gameserver.network.SystemMessageId;
import org.l2jpx.gameserver.network.serverpackets.ActionFailed;
import org.l2jpx.gameserver.network.serverpackets.SendTradeRequest;
import org.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * This packet manages the trade request.
 */
public class TradeRequest extends ClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
	}
	
	private void scheduleDeny(Player player, String name)
	{
		if (player != null)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE);
			sm.addString(name);
			player.sendPacket(sm);
			player.onTransactionResponse();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendMessage("Transactions are disabled for your current Access Level.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(BotReportTable.TRADE_ACTION_BLOCK_ID))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		final WorldObject target = World.getInstance().findObject(_objectId);
		// If there is no target, target is far away or
		// they are in different instances
		// trade request is ignored and there is no system message.
		if ((target == null) || !player.isInSurroundingRegion(target) || (target.getInstanceWorld() != player.getInstanceWorld()))
		{
			return;
		}
		
		// If target and acting player are the same, trade request is ignored
		// and the following system message is sent to acting player.
		if (target.getObjectId() == player.getObjectId())
		{
			player.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}
		
		if (FakePlayerData.getInstance().isTalkable(target.getName()))
		{
			final String name = FakePlayerData.getInstance().getProperName(target.getName());
			boolean npcInRange = false;
			for (Npc npc : World.getInstance().getVisibleObjectsInRange(player, Npc.class, 150))
			{
				if (npc.getName().equals(name))
				{
					npcInRange = true;
				}
			}
			if (!npcInRange)
			{
				player.sendPacket(SystemMessageId.YOUR_TARGET_IS_OUT_OF_RANGE);
				return;
			}
			if (!player.isProcessingRequest())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1);
				sm.addString(name);
				player.sendPacket(sm);
				ThreadPool.schedule(() -> scheduleDeny(player, name), 10000);
				player.blockRequest();
			}
			else
			{
				player.sendPacket(SystemMessageId.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
			}
			return;
		}
		
		if (!target.isPlayer())
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Player partner = target.getActingPlayer();
		if (partner.isInOlympiadMode() || player.isInOlympiadMode())
		{
			player.sendMessage("A user currently participating in the Olympiad cannot accept or request a trade.");
			return;
		}
		
		info = partner.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(BotReportTable.TRADE_ACTION_BLOCK_ID))
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_IS_CURRENTLY_BEING_INVESTIGATED);
					sm.addString(partner.getName());
					player.sendPacket(sm);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// L2J Customs: Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getReputation() < 0))
		{
			player.sendMessage("You cannot trade while you are in a chaotic state.");
			return;
		}
		
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (partner.getReputation() < 0))
		{
			player.sendMessage("You cannot request a trade while your target is in a chaotic state.");
			return;
		}
		
		if (Config.JAIL_DISABLE_TRANSACTION && (player.isJailed() || partner.isJailed()))
		{
			player.sendMessage("You cannot trade while you are in in Jail.");
			return;
		}
		
		if (player.isInStoreMode() || partner.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
			return;
		}
		
		SystemMessage sm;
		if (partner.isProcessingRequest() || partner.isProcessingTransaction())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
			sm.addString(partner.getName());
			player.sendPacket(sm);
			return;
		}
		
		if (partner.getTradeRefusal())
		{
			player.sendMessage("That person is in trade refusal mode.");
			return;
		}
		
		if (BlockList.isBlocked(partner, player))
		{
			sm = new SystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
			sm.addString(partner.getName());
			player.sendPacket(sm);
			return;
		}
		
		if (player.calculateDistance3D(partner) > 150)
		{
			player.sendPacket(SystemMessageId.YOUR_TARGET_IS_OUT_OF_RANGE);
			return;
		}
		
		player.onTransactionRequest(partner);
		partner.sendPacket(new SendTradeRequest(player.getObjectId()));
		sm = new SystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1);
		sm.addString(partner.getName());
		player.sendPacket(sm);
	}
}
