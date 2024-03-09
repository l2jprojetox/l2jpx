package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import engine.EngineModsManager;
import net.l2jpx.Config;
import net.l2jpx.gameserver.GameServer;
import net.l2jpx.gameserver.autofarm.AutofarmPlayerRoutine;
import net.l2jpx.gameserver.communitybbs.Manager.RegionBBSManager;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.Inventory;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.sevensigns.SevenSignsFestival;
import net.l2jpx.gameserver.network.L2GameClient;
import net.l2jpx.gameserver.network.L2GameClient.GameClientState;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.CharSelectInfo;
import net.l2jpx.gameserver.network.serverpackets.RestartResponse;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

public final class RequestRestart extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestRestart.class);
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		
		// Check if player is == null
		if (player == null)
		{
			LOGGER.warn("[RequestRestart] activeChar null!?");
			return;
		}
		
		if (EngineModsManager.onExitWorld(player))
		{
			return;
		}
		
		// Check if player is enchanting
		if (player.getActiveEnchantItem() != null)
		{
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		// Check if player are changing class
		if (player.isLocked())
		{
			LOGGER.warn("Player " + player.getName() + " tried to restart during class change.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		player.getInventory().updateDatabase();
		
		// Check if player is in private store
		if (player.getPrivateStoreType() != 0)
		{
			player.sendMessage("Cannot restart while trading.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isAutoFarm())
		{
			if(AutofarmPlayerRoutine.isIpAllowed(player.getIP())) {
				AutofarmPlayerRoutine.removeIpEntry(player.getObjectId());
			}
			
		    
		}
		
		// Check if player is in combat
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player) && !(player.isGM() && Config.GM_RESTART_FIGHTING))
		{
			if (Config.DEBUG)
			{
				LOGGER.debug("Player " + player.getName() + " tried to logout while fighting.");
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_RESTART_WHILE_IN_COMBAT));
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		// Check if player is registred on olympiad
		if (player.getOlympiadGameId() > 0 || player.isInOlympiadMode())
		{
			player.sendMessage("You can't restart while in Olympiad.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		// Check if player is in away mode
		if (player.isAway())
		{
			player.sendMessage("You can't restart in Away mode.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		// Prevent player from restarting if they are a festival participant
		// and it is in progress, otherwise notify party members that the player
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendPacket(SystemMessage.sendString("You cannot restart while you are a participant in a festival."));
				player.sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(RestartResponse.valueOf(false));
				return;
			}
			
			final L2Party playerParty = player.getParty();
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		
		// Fix against exploit anti-target
		if (player.isCastingNow())
		{
			player.abortCast();
			player.sendPacket(new ActionFailed());
		}
		
		// Check if player is teleporting
		if (player.isTeleporting())
		{
			player.abortCast();
			player.setIsTeleporting(false);
		}
		
		// Check if player is trading
		if (player.getActiveRequester() != null)
		{
			player.getActiveRequester().onTradeCancel(player);
			player.onTradeCancel(player.getActiveRequester());
		}
		
		// Check if player are flying
		if (player.isFlying())
		{
			player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
		}
		
		if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null && player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).isAugmented())
		{
			player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getAugmentation().removeBoni(player);
		}
		
		final L2GameClient client = getClient();
		
		// detach the client from the char so that the connection isnt closed in the deleteMe
		player.setClient(null);
		
		RegionBBSManager.getInstance().changeCommunityBoard();
		
		// removing player from the world
		player.deleteMe();
		player.store();
		
		getClient().setActiveChar(null);
		
		// return the client to the authed status
		client.setState(GameClientState.AUTHED);
		
		// before the char selection, check shutdown status
		if (GameServer.getSelectorThread().isShutdown())
		{
			getClient().closeNow();
			return;
		}
		
		// Restart true
		sendPacket(RestartResponse.valueOf(true));
		
		// send char list
		final CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
	
	@Override
	public String getType()
	{
		return "[C] 46 RequestRestart";
	}
}
