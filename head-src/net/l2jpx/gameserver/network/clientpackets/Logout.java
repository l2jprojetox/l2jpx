package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import engine.EngineModsManager;
import engine.data.memory.ObjectData;
import engine.holders.objects.PlayerHolder;
import net.l2jpx.Config;
import net.l2jpx.gameserver.autofarm.AutofarmPlayerRoutine;
import net.l2jpx.gameserver.communitybbs.Manager.RegionBBSManager;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.sevensigns.SevenSignsFestival;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.LeaveWorld;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.taskmanager.AttackStanceTaskManager;

public final class Logout extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(Logout.class);
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		// Dont allow leaving if player is fighting
		final L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		if (player.isAway())
		{
			player.sendMessage("You can't restart in Away mode.");
			return;
		}
		
		if (EngineModsManager.onExitWorld(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.getInventory().updateDatabase();
		
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player) && !(player.isGM() && Config.GM_RESTART_FIGHTING))
		{
			if (Config.DEBUG)
			{
				LOGGER.debug(getType() + ": Player " + player.getName() + " tried to logout while Fighting");
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_EXIT_WHILE_IN_COMBAT));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Dont allow leaving if player is in combat
		if (player.isInCombat() && !player.isGM())
		{
			player.sendMessage("You cannot Logout while is in Combat mode.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Dont allow leaving if player is teleporting
		if (player.isTeleporting() && !player.isGM())
		{
			player.sendMessage("You cannot Logout while is Teleporting.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You can not logout in Olympiad mode.");
			return;
		}
		
		// Prevent player from logging out if they are a festival participant nd it is in progress,
		// otherwise notify party members that the player is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage("You cannot Logout while you are a participant in a Festival.");
				return;
			}
			
			final L2Party playerParty = player.getParty();
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming Festival."));
			}
		}
		
		if (player.isFlying())
		{
			player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
		}
		
		if (player.isStored())
		{
			player.store();
			player.closeNetConnection();
			
			if (player.getOfflineStartTime() == 0)
			{
				player.setOfflineStartTime(System.currentTimeMillis());
			}
			return;
		}
		else if (ObjectData.get(PlayerHolder.class, player).isSellBuff())
		{
			getClient().close(LeaveWorld.STATIC_PACKET);
		}
		
		if (player.isCastingNow())
		{
			player.abortCast();
			player.sendPacket(new ActionFailed());
		}
		
		if (player.isAutoFarm())
		{
			if(AutofarmPlayerRoutine.isIpAllowed(player.getIP())) 
			{
				AutofarmPlayerRoutine.removeIpEntry(player.getObjectId());
			}
			
		    
		}
		
		RegionBBSManager.getInstance().changeCommunityBoard();
		player.deleteMe();
	}
	
	@Override
	public String getType()
	{
		return "[C] 09 Logout";
	}
}