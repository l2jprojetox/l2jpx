package net.l2jpx.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.l2jpx.Config;
import net.l2jpx.gameserver.managers.DimensionalRiftManager;
import net.l2jpx.gameserver.model.L2Party;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.templates.DimensionalRiftRoom;
import net.l2jpx.util.random.Rnd;

/**
 * Thanks to L2Fortress and balancer.ru - kombat
 * @author ReynalDev
 */
public class DimensionalRift
{
	protected byte type;
	protected L2Party party;
	protected List<Byte> completedRooms = new ArrayList<>();
	private static final long SECONDS_5 = 5000;
	protected byte jumps_current = 0;
	
	private Timer teleporterTimer;
	private TimerTask teleporterTimerTask;
	private Timer spawnTimer;
	private TimerTask spawnTimerTask;
	
	protected byte choosenRoom = -1;
	private boolean hasJumped = false;
	protected List<L2PcInstance> deadPlayers = new ArrayList<>();
	protected List<L2PcInstance> revivedInWaitingRoom = new ArrayList<>();
	private boolean isBossRoom = false;
	
	public DimensionalRift(L2Party party, byte type, byte room)
	{
		this.type = type;
		this.party = party;
		choosenRoom = room;
		int[] coords = getRoomCoord(room);
		party.setDimensionalRift(this);
		
		for (L2PcInstance p : party.getPartyMembers())
		{
			p.teleToLocation(coords[0], coords[1], coords[2]);
		}
		
		createSpawnTimer(choosenRoom);
		createTeleporterTimer(true);
	}
	
	public byte getType()
	{
		return type;
	}
	
	public byte getCurrentRoom()
	{
		return choosenRoom;
	}
	
	protected void createTeleporterTimer(boolean reasonTP)
	{
		if (teleporterTimerTask != null)
		{
			teleporterTimerTask.cancel();
			teleporterTimerTask = null;
		}
		
		if (teleporterTimer != null)
		{
			teleporterTimer.cancel();
			teleporterTimer = null;
		}
		
		teleporterTimer = new Timer();
		teleporterTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if (choosenRoom > -1)
				{
					DimensionalRiftManager.getInstance().getRoom(type, choosenRoom).unspawn();
				}
				
				if (reasonTP && jumps_current < getMaxJumps() && party.getMemberCount() > deadPlayers.size())
				{
					jumps_current++;
					
					completedRooms.add(choosenRoom);
					choosenRoom = -1;
					
					for (L2PcInstance p : party.getPartyMembers())
					{
						if (!revivedInWaitingRoom.contains(p))
						{
							teleportToNextRoom(p);
						}
					}
					
					createTeleporterTimer(true);
					createSpawnTimer(choosenRoom);
				}
				else
				{
					for (L2PcInstance p : party.getPartyMembers())
					{
						if (!revivedInWaitingRoom.contains(p))
						{
							teleportToWaitingRoom(p);
						}
					}
					
					killRift();
					cancel();
				}
			}
		};
		
		if (reasonTP)
		{
			teleporterTimer.schedule(teleporterTimerTask, calcTimeToNextJump()); // Teleporter task, 8-10 minutes
		}
		else
		{
			teleporterTimer.schedule(teleporterTimerTask, SECONDS_5); // incorrect party member invited.
		}
	}
	
	public void createSpawnTimer(byte room)
	{
		if (spawnTimerTask != null)
		{
			spawnTimerTask.cancel();
			spawnTimerTask = null;
		}
		
		if (spawnTimer != null)
		{
			spawnTimer.cancel();
			spawnTimer = null;
		}
		
		DimensionalRiftRoom riftRoom = DimensionalRiftManager.getInstance().getRoom(type, room);
		riftRoom.setUsed();
		
		spawnTimer = new Timer();
		spawnTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				riftRoom.spawn();
			}
		};
		
		spawnTimer.schedule(spawnTimerTask, Config.RIFT_SPAWN_DELAY);
	}
	
	public void partyMemberInvited()
	{
		createTeleporterTimer(false);
	}
	
	public void partyMemberExited(L2PcInstance player)
	{
		if (deadPlayers.contains(player))
		{
			deadPlayers.remove(player);
		}
		
		if (revivedInWaitingRoom.contains(player))
		{
			revivedInWaitingRoom.remove(player);
		}
		
		if (party.getMemberCount() < Config.RIFT_MIN_PARTY_SIZE || party.getMemberCount() == 1)
		{
			for (L2PcInstance p : party.getPartyMembers())
			{
				teleportToWaitingRoom(p);
			}
			
			killRift();
		}
	}
	
	public void manualTeleport(L2PcInstance player, L2NpcInstance npc)
	{
		if (!player.isInParty() || !player.getParty().isInDimensionalRift())
		{
			return;
		}
		
		if (player.getObjectId() != player.getParty().getPartyLeaderOID())
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/seven_signs/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		if (hasJumped)
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/seven_signs/rift/AlreadyTeleported.htm", npc);
			return;
		}
		
		hasJumped = true;
		
		DimensionalRiftManager.getInstance().getRoom(type, choosenRoom).unspawn();
		completedRooms.add(choosenRoom);
		choosenRoom = -1;
		
		for (L2PcInstance p : party.getPartyMembers())
		{
			teleportToNextRoom(p);
		}
		
		createSpawnTimer(choosenRoom);
		createTeleporterTimer(true);
	}
	
	public void manualExitRift(L2PcInstance player, L2NpcInstance npc)
	{
		if (!player.isInParty() || !player.getParty().isInDimensionalRift())
		{
			return;
		}
		
		if (player.getObjectId() != player.getParty().getPartyLeaderOID())
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/seven_signs/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		for (L2PcInstance p : player.getParty().getPartyMembers())
		{
			teleportToWaitingRoom(p);
		}
		
		killRift();
	}
	
	protected void teleportToNextRoom(L2PcInstance player)
	{
		if (choosenRoom == -1)
		{ // Do not tp in the same room a second time and do not tp in the busy room
			do
			{
				choosenRoom = (byte) Rnd.get(1, 9);
			}
			while (completedRooms.contains(choosenRoom) && !DimensionalRiftManager.getInstance().isRoomAvailable(type, choosenRoom));
		}
		
		checkBossRoom(choosenRoom);
		
		int[] coords = getRoomCoord(choosenRoom);
		
		player.teleToLocation(coords[0], coords[1], coords[2]);
	}
	
	protected void teleportToWaitingRoom(L2PcInstance player)
	{
		DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
	}
	
	public void killRift()
	{
		completedRooms = null;
		
		if (party != null)
		{
			party.setDimensionalRift(null);
		}
		
		party = null;
		revivedInWaitingRoom = null;
		deadPlayers = null;
		DimensionalRiftManager.getInstance().getRoom(type, choosenRoom).unspawn();
		DimensionalRiftManager.getInstance().killRift(this);
	}
	
	public Timer getTeleportTimer()
	{
		return teleporterTimer;
	}
	
	public TimerTask getTeleportTimerTask()
	{
		return teleporterTimerTask;
	}
	
	public Timer getSpawnTimer()
	{
		return spawnTimer;
	}
	
	public TimerTask getSpawnTimerTask()
	{
		return spawnTimerTask;
	}
	
	public void setTeleportTimer(Timer t)
	{
		teleporterTimer = t;
	}
	
	public void setTeleportTimerTask(TimerTask tt)
	{
		teleporterTimerTask = tt;
	}
	
	public void setSpawnTimer(Timer t)
	{
		spawnTimer = t;
	}
	
	public void setSpawnTimerTask(TimerTask st)
	{
		spawnTimerTask = st;
	}
	
	private long calcTimeToNextJump()
	{
		int time = Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_MIN, Config.RIFT_AUTO_JUMPS_TIME_MAX) * 1000;
		
		if (isBossRoom)
		{
			return (long) (time * Config.RIFT_BOSS_ROOM_TIME_MUTIPLY);
		}
		return time;
	}
	
	public void memberDead(L2PcInstance player)
	{
		if (!deadPlayers.contains(player))
		{
			deadPlayers.add(player);
		}
	}
	
	public void memberRessurected(L2PcInstance player)
	{
		if (deadPlayers.contains(player))
		{
			deadPlayers.remove(player);
		}
	}
	
	public void usedTeleport(L2PcInstance player)
	{
		if (!revivedInWaitingRoom.contains(player))
		{
			revivedInWaitingRoom.add(player);
		}
		
		if (!deadPlayers.contains(player))
		{
			deadPlayers.add(player);
		}
		
		if (party.getMemberCount() - revivedInWaitingRoom.size() < Config.RIFT_MIN_PARTY_SIZE)
		{
			// int pcm = party.getMemberCount();
			// int rev = revivedInWaitingRoom.size();
			// int min = Config.RIFT_MIN_PARTY_SIZE;
			
			for (L2PcInstance p : party.getPartyMembers())
			{
				if (!revivedInWaitingRoom.contains(p))
				{
					teleportToWaitingRoom(p);
				}
			}
			
			killRift();
		}
	}
	
	public List<L2PcInstance> getDeadMemberList()
	{
		return deadPlayers;
	}
	
	public List<L2PcInstance> getRevivedAtWaitingRoom()
	{
		return revivedInWaitingRoom;
	}
	
	public void checkBossRoom(byte room)
	{
		isBossRoom = DimensionalRiftManager.getInstance().getRoom(type, room).isBossRoom();
	}
	
	public int[] getRoomCoord(byte room)
	{
		return DimensionalRiftManager.getInstance().getRoom(type, room).getTeleportCoords();
	}
	
	public byte getMaxJumps()
	{
		if (Config.RIFT_MAX_JUMPS <= 8 && Config.RIFT_MAX_JUMPS >= 1)
		{
			return (byte) Config.RIFT_MAX_JUMPS;
		}
		return 4;
	}
}
