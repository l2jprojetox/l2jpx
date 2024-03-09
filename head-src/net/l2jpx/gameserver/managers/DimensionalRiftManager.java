package net.l2jpx.gameserver.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.NpcTable;
import net.l2jpx.gameserver.datatables.sql.SpawnTable;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.DimensionalRift;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.gameserver.network.serverpackets.NpcHtmlMessage;
import net.l2jpx.gameserver.templates.DimensionalRiftRoom;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.gameserver.util.Util;
import net.l2jpx.util.random.Rnd;

/**
 * Thanks to L2Fortress and balancer.ru - kombat
 * @author ReynalDev
 */
public class DimensionalRiftManager
{
	protected static final Logger LOGGER = Logger.getLogger(DimensionalRiftManager.class);
	
	private static DimensionalRiftManager instance;
	private Map<Byte, Map<Byte, DimensionalRiftRoom>> rooms = new HashMap<>();
	private static final short DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;
	private static final int MAX_PARTY_PER_AREA = 3;
	
	public static DimensionalRiftManager getInstance()
	{
		if (instance == null)
		{
			instance = new DimensionalRiftManager();
		}
		
		return instance;
	}
	
	private DimensionalRiftManager()
	{
		loadData();
	}
	
	public DimensionalRiftRoom getRoom(byte type, byte room)
	{
		return rooms.get(type) == null ? null : rooms.get(type).get(room);
	}
	
	public boolean isAreaAvailable(byte area)
	{
		Map<Byte, DimensionalRiftRoom> tmap = rooms.get(area);
		if (tmap == null)
		{
			return false;
		}
		int used = 0;
		for (DimensionalRiftRoom room : tmap.values())
		{
			if (room.isUsed())
			{
				used++;
			}
		}
		return used <= MAX_PARTY_PER_AREA;
	}
	
	public boolean isRoomAvailable(byte area, byte room)
	{
		if (rooms.get(area) == null || rooms.get(area).get(room) == null)
		{
			return false;
		}
		return !rooms.get(area).get(room).isUsed();
	}
	
	public void loadData()
	{
		int countGood = 0;
		
		String path = "data/xml/dimensionalRift.xml";
		
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			
			File file = new File(path);
			
			if (!file.exists())
			{
				LOGGER.error(path + "does not exist");
			}
			
			Document doc = factory.newDocumentBuilder().parse(file);
			
			NamedNodeMap attrs;
			
			for (Node rift = doc.getFirstChild(); rift != null; rift = rift.getNextSibling())
			{
				if ("list".equalsIgnoreCase(rift.getNodeName()))
				{
					for (Node area = rift.getFirstChild(); area != null; area = area.getNextSibling())
					{
						if ("area".equalsIgnoreCase(area.getNodeName()))
						{
							attrs = area.getAttributes();
							byte type = Byte.parseByte(attrs.getNamedItem("type").getNodeValue());
							
							for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
							{
								if ("room".equalsIgnoreCase(room.getNodeName()))
								{
									attrs = room.getAttributes();
									byte roomId = Byte.parseByte(attrs.getNamedItem("id").getNodeValue());
									int xMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int xMax = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int yMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int yMax = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int z1 = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int z2 = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int xT = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int yT = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									int zT = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
									boolean isBossRoom = false;
									
									if(attrs.getNamedItem("isBossRoom") != null)
									{
										isBossRoom = Boolean.parseBoolean(attrs.getNamedItem("isBossRoom").getNodeValue());
									}
									
									if (!rooms.containsKey(type))
									{
										rooms.put(type, new HashMap<Byte, DimensionalRiftRoom>());
									}
									
									rooms.get(type).put(roomId, new DimensionalRiftRoom(type, roomId, xMin, xMax, yMin, yMax, z1, z2, xT, yT, zT, isBossRoom));
									
									for (Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
									{
										if ("spawn".equalsIgnoreCase(spawn.getNodeName()))
										{
											attrs = spawn.getAttributes();
											int mobId = Integer.parseInt(attrs.getNamedItem("mobId").getNodeValue());
											int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
											int count = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
											
											L2NpcTemplate template = NpcTable.getInstance().getTemplate(mobId);
											
											if (template == null)
											{
												LOGGER.error("Template " + mobId + " not found!");
											}
											if (!rooms.containsKey(type))
											{
												LOGGER.error("Type " + type + " not found!");
											}
											else if (!rooms.get(type).containsKey(roomId))
											{
												LOGGER.error("Room " + roomId + " in Type " + type + " not found!");
											}
											
											for (int i = 0; i < count; i++)
											{
												DimensionalRiftRoom riftRoom = rooms.get(type).get(roomId);
												int x = riftRoom.getRandomX();
												int y = riftRoom.getRandomY();
												int z = riftRoom.getTeleportCoords()[2];
												
												if (template != null && rooms.containsKey(type) && rooms.get(type).containsKey(roomId))
												{
													L2Spawn spawnDat = new L2Spawn(template);
													spawnDat.setAmount(1);
													spawnDat.setLocx(x);
													spawnDat.setLocy(y);
													spawnDat.setLocz(z);
													spawnDat.setHeading(-1);
													spawnDat.setRespawnDelay(delay);
													SpawnTable.getInstance().addNewSpawn(spawnDat, false);
													rooms.get(type).get(roomId).getSpawns().add(spawnDat);
													countGood++;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("DimensionalRiftManager.loadSpawns : Error on loading dimensional rift spawns", e);
		}
		
		int typeSize = rooms.keySet().size();
		int roomSize = 0;
		
		for (Byte b : rooms.keySet())
		{
			roomSize += rooms.get(b).keySet().size();
		}
		
		LOGGER.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
		
		LOGGER.info("DimensionalRiftManager: Loaded " + countGood + " dimensional rift spawns.");
	}
	
	public boolean checkIfInRiftZone(int x, int y, int z, boolean ignorePeaceZone)
	{
		if (ignorePeaceZone)
		{
			return rooms.get((byte) 0).get((byte) 1).checkIfInZone(x, y, z);
		}
		return rooms.get((byte) 0).get((byte) 1).checkIfInZone(x, y, z) && !rooms.get((byte) 0).get((byte) 0).checkIfInZone(x, y, z);
	}
	
	public boolean checkIfInPeaceZone(int x, int y, int z)
	{
		return rooms.get((byte) 0).get((byte) 0).checkIfInZone(x, y, z);
	}
	
	public void teleportToWaitingRoom(L2PcInstance player)
	{
		int[] coords = getRoom((byte) 0, (byte) 0).getTeleportCoords();
		player.teleToLocation(coords[0], coords[1], coords[2]);
	}
	
	public void start(L2PcInstance player, byte type, L2NpcInstance npc)
	{
		boolean canPass = true;
		if (!player.isInParty())
		{
			showHtmlFile(player, "data/html/seven_signs/rift/NoParty.htm", npc);
			return;
		}
		
		if (player.getParty().getPartyLeaderOID() != player.getObjectId())
		{
			showHtmlFile(player, "data/html/seven_signs/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		if (player.getParty().isInDimensionalRift())
		{
			handleCheat(player, npc);
			return;
		}
		
		if (!isAreaAvailable(type))
		{
			player.sendMessage("This rift area is full. Try later.");
			return;
		}
		
		if (player.getParty().getMemberCount() < Config.RIFT_MIN_PARTY_SIZE)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/seven_signs/rift/SmallParty.htm");
			html.replace("%npc_name%", npc.getName());
			html.replace("%count%", String.valueOf(Config.RIFT_MIN_PARTY_SIZE));
			player.sendPacket(html);
			return;
		}
		
		for (L2PcInstance p : player.getParty().getPartyMembers())
		{
			if (!checkIfInPeaceZone(p.getX(), p.getY(), p.getZ()))
			{
				canPass = false;
			}
		}
		
		if (!canPass)
		{
			showHtmlFile(player, "data/html/seven_signs/rift/NotInWaitingRoom.htm", npc);
			return;
		}
		
		L2ItemInstance item;
		for (L2PcInstance p : player.getParty().getPartyMembers())
		{
			item = p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID);
			
			if (item == null)
			{
				canPass = false;
				break;
			}
			
			if (item.getCount() > 0)
			{
				if (item.getCount() < getNeededItems(type))
				{
					canPass = false;
				}
			}
		}
		
		if (!canPass)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/seven_signs/rift/NoFragments.htm");
			html.replace("%npc_name%", npc.getName());
			html.replace("%count%", String.valueOf(getNeededItems(type)));
			player.sendPacket(html);
			return;
		}
		
		for (L2PcInstance p : player.getParty().getPartyMembers())
		{
			item = p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID);
			p.destroyItem("RiftEntrance", item.getObjectId(), getNeededItems(type), null, false);
		}
		
		byte room;
		do
		{
			room = (byte) Rnd.get(1, 9);
		}
		while (!isRoomAvailable(type, room));
		
		new DimensionalRift(player.getParty(), type, room);
	}
	
	public void killRift(DimensionalRift d)
	{
		if (d.getTeleportTimerTask() != null)
		{
			d.getTeleportTimerTask().cancel();
		}
		d.setTeleportTimerTask(null);
		
		if (d.getTeleportTimer() != null)
		{
			d.getTeleportTimer().cancel();
		}
		d.setTeleportTimer(null);
		
		if (d.getSpawnTimerTask() != null)
		{
			d.getSpawnTimerTask().cancel();
		}
		d.setSpawnTimerTask(null);
		
		if (d.getSpawnTimer() != null)
		{
			d.getSpawnTimer().cancel();
		}
		d.setSpawnTimer(null);
	}
	
	private int getNeededItems(byte type)
	{
		switch (type)
		{
			case 1:
				return Config.RIFT_ENTER_COST_RECRUIT;
			case 2:
				return Config.RIFT_ENTER_COST_SOLDIER;
			case 3:
				return Config.RIFT_ENTER_COST_OFFICER;
			case 4:
				return Config.RIFT_ENTER_COST_CAPTAIN;
			case 5:
				return Config.RIFT_ENTER_COST_COMMANDER;
			case 6:
				return Config.RIFT_ENTER_COST_HERO;
			default:
				return 999999;
		}
	}
	
	public void showHtmlFile(L2PcInstance player, String file, L2NpcInstance npc)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(file);
		html.replace("%npc_name%", npc.getName());
		player.sendPacket(html);
	}
	
	public void handleCheat(L2PcInstance player, L2NpcInstance npc)
	{
		showHtmlFile(player, "data/html/seven_signs/rift/Cheater.htm", npc);
		if (!player.isGM())
		{
			LOGGER.warn("Player " + player.getName() + "(" + player.getObjectId() + ") was cheating in dimension rift area!");
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " tried to cheat in dimensional rift.", Config.DEFAULT_PUNISH);
		}
	}
}
