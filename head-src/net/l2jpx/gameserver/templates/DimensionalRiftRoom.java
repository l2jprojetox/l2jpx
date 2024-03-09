package net.l2jpx.gameserver.templates;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.util.random.Rnd;

/**
 * @author ReynalDev
 */
public class DimensionalRiftRoom
	{
		protected byte type;
		protected byte room;
		private int xMin;
		private int xMax;
		private int yMin;
		private int yMax;
		private int zMin;
		private int zMax;
		private int[] teleportCoords;
		private Shape s;
		private boolean isBossRoom;
		private List<L2Spawn> roomSpawns;
		protected List<L2NpcInstance> roomMobs;
		private boolean isUsed = false;
		
		public DimensionalRiftRoom(byte type, byte room, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, int xT, int yT, int zT, boolean isBossRoom)
		{
			this.type = type;
			this.room = room;
			this.xMin = xMin + 128;
			this.xMax = xMax - 128;
			this.yMin = yMin + 128;
			this.yMax = yMax - 128;
			this.zMin = zMin;
			this.zMax = zMax;
			teleportCoords = new int[]
			{
				xT,
				yT,
				zT
			};
			this.isBossRoom = isBossRoom;
			roomSpawns = new ArrayList<>();
			roomMobs = new ArrayList<>();
			s = new Polygon(new int[]
			{
				xMin,
				xMax,
				xMax,
				xMin
			}, new int[]
			{
				yMin,
				yMin,
				yMax,
				yMax
			}, 4);
		}
		
		public int getRandomX()
		{
			return Rnd.get(xMin, xMax);
		}
		
		public int getRandomY()
		{
			return Rnd.get(yMin, yMax);
		}
		
		public int[] getTeleportCoords()
		{
			return teleportCoords;
		}
		
		public boolean checkIfInZone(int x, int y, int z)
		{
			return s.contains(x, y) && z >= zMin && z <= zMax;
		}
		
		public boolean isBossRoom()
		{
			return isBossRoom;
		}
		
		public List<L2Spawn> getSpawns()
		{
			return roomSpawns;
		}
		
		public void spawn()
		{
			for (L2Spawn spawn : roomSpawns)
			{
				spawn.doSpawn();
				if(!isBossRoom())
				{
					spawn.startRespawn();
				}
			}
		}
		
		public void unspawn()
		{
			for (L2Spawn spawn : roomSpawns)
			{
				spawn.stopRespawn();
				if (spawn.getLastSpawn() != null)
				{
					spawn.getLastSpawn().deleteMe();
				}
			}
			isUsed = false;
		}
		
		public void setUsed()
		{
			isUsed = true;
		}
		
		public boolean isUsed()
		{
			return isUsed;
		}
	}