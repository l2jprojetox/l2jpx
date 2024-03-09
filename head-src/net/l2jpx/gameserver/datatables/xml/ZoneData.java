package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.l2jpx.Config;
import net.l2jpx.gameserver.managers.FishingZoneManager;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.L2WorldRegion;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.model.zone.shape.ZoneCuboid;
import net.l2jpx.gameserver.model.zone.shape.ZoneCylinder;
import net.l2jpx.gameserver.model.zone.shape.ZoneNPoly;
import net.l2jpx.gameserver.model.zone.type.L2FishingZone;
import net.l2jpx.gameserver.model.zone.type.L2WaterZone;

/**
 * @author programmos
 * @author scoria dev
 * @author ReynalDev
 */
public class ZoneData
{
	private static final Logger LOGGER = Logger.getLogger(ZoneData.class);
	private static Map<Integer, L2ZoneType> zones = new HashMap<>();
	
	private static ZoneData instance;
	
	public static final ZoneData getInstance()
	{
		if (instance == null)
		{
			instance = new ZoneData();
		}
		
		return instance;
	}
	
	public ZoneData()
	{
		load();
	}
	
	public void reload()
	{
		synchronized (instance)
		{
			instance = null;
			instance = new ZoneData();
		}
	}
	
	private void load()
	{
		final L2WorldRegion[][] worldRegions = L2World.getInstance().getAllWorldRegions();
		
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			
			File file = new File(Config.DATAPACK_ROOT + "/data/xml/zone.xml");
			
			if (!file.exists())
			{
				LOGGER.error("The zone.xml file is missing in the path /data/xml/");
			}
			else
			{
				Document doc = factory.newDocumentBuilder().parse(file);
				
				for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("list".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("zone".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								
								int zoneId = 0;
								
								if(attrs.getNamedItem("id").getNodeValue() == null)
								{
									LOGGER.warn("No id specified for the zone, check your zone.xml file!");
									continue;
								}
								
								zoneId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
								
								String zoneName = "No zone name";
								if (attrs.getNamedItem("name") != null)
								{
									zoneName = attrs.getNamedItem("name").getNodeValue();
								}
								
								int minZ = Integer.parseInt(attrs.getNamedItem("minZ").getNodeValue());
								int maxZ = Integer.parseInt(attrs.getNamedItem("maxZ").getNodeValue());
								
								String zoneType = attrs.getNamedItem("type").getNodeValue();
								String zoneShape = attrs.getNamedItem("shape").getNodeValue();
								
								// Create the zone
								Class<?> newZone = null;
								Constructor<?> zoneConstructor = null;
								L2ZoneType temp = null;
								
								try
								{
									newZone = Class.forName("net.l2jpx.gameserver.model.zone.type." + zoneType);
									zoneConstructor = newZone.getConstructor(int.class);
									temp = (L2ZoneType) zoneConstructor.newInstance(zoneId);
								}
								catch (Exception e)
								{
									LOGGER.error("ZoneData.load: No such zone type: " + zoneType + " in the core for zone with ID: " + zoneId, e);
									continue;
								}
								
								temp.setZoneName(zoneName);
								
								zoneType = null;
								
								// get the zone shape from file if any
								
								int[][] coords = null;
								int[] point;
								List<int[]> rs = new ArrayList<>();
								
								// loading from XML first
								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									if ("node".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();
										point = new int[2];
										point[0] = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
										point[1] = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
										rs.add(point);
									}
								}
								
								coords = rs.toArray(new int[rs.size()][]);
								
								if (coords == null || coords.length == 0) 
								{
									LOGGER.error("No NODES defined for zone id" + zoneId);
								}
								else
								{ 
									if (zoneShape.equalsIgnoreCase("Cuboid"))
									{
										if (coords.length == 2)
										{
											temp.setZoneShape(new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], minZ, maxZ));
										}
										else
										{
											LOGGER.warn("ZoneData: Missing cuboid vertex in sql data for zone: " + zoneId);
											continue;
										}
									}
									else if (zoneShape.equalsIgnoreCase("NPoly"))
									{
										// nPoly needs to have at least 3 vertices
										if (coords.length > 2)
										{
											final int[] aX = new int[coords.length];
											final int[] aY = new int[coords.length];
											for (int i = 0; i < coords.length; i++)
											{
												aX[i] = coords[i][0];
												aY[i] = coords[i][1];
											}
											temp.setZoneShape(new ZoneNPoly(aX, aY, minZ, maxZ));
										}
										else
										{
											LOGGER.warn("ZoneData: Bad data for zone: " + zoneId);
											continue;
										}
									}
									else if (zoneShape.equalsIgnoreCase("Cylinder"))
									{
										// A Cylinder zone requires a center point
										// at x,y and a radius
										attrs = d.getAttributes();
										final int zoneRad = Integer.parseInt(attrs.getNamedItem("rad").getNodeValue());
										if (coords.length == 1 && zoneRad > 0)
										{
											temp.setZoneShape(new ZoneCylinder(coords[0][0], coords[0][1], minZ, maxZ, zoneRad));
										}
										else
										{
											LOGGER.warn("ZoneData: Bad data for zone: " + zoneId);
											continue;
										}
									}
									else
									{
										LOGGER.warn("ZoneData: Unknown shape: " + zoneShape);
										continue;
									}
									
								}
								
								zoneShape = null;
								
								// Check for aditional parameters
								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									if ("stat".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();
										String name = attrs.getNamedItem("name").getNodeValue();
										String val = attrs.getNamedItem("val").getNodeValue();
										
										temp.setParameter(name, val);
									}
									if ("spawn".equalsIgnoreCase(cd.getNodeName()))
									{
										temp.setSpawnLocs(cd);
									}
								}
								
								// Skip checks for fishing zones & add to fishing zone manager
								if (temp instanceof L2FishingZone)
								{
									FishingZoneManager.getInstance().addFishingZone((L2FishingZone) temp);
									continue;
								}
								
								if (temp instanceof L2WaterZone)
								{
									FishingZoneManager.getInstance().addWaterZone((L2WaterZone) temp);
								}
								
								// Register the zone into any world region it intersects with...
								// currently 11136 test for each zone :>
								int ax, ay, bx, by;
								
								for (int x = 0; x < worldRegions.length; x++)
								{
									for (int y = 0; y < worldRegions[x].length; y++)
									{
										ax = x - L2World.OFFSET_X << L2World.SHIFT_BY;
										bx = x + 1 - L2World.OFFSET_X << L2World.SHIFT_BY;
										ay = y - L2World.OFFSET_Y << L2World.SHIFT_BY;
										by = y + 1 - L2World.OFFSET_Y << L2World.SHIFT_BY;
										
										if (temp.getZoneShape().intersectsRectangle(ax, bx, ay, by))
										{
											if (Config.DEBUG)
											{
												LOGGER.info("Zone (" + zoneId + ") added to: " + x + " " + y);
											}
											worldRegions[x][y].addZone(temp);
										}
									}
								}
								
								// check if ID already exist in the map
								
								if (zones.containsKey(zoneId))
								{
									LOGGER.warn("Zone ID " + zoneId + " already exists and was replaced");
								}
								
								zones.put(zoneId, temp);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("ZoneData.load: Error while loading zones ", e);
		}
		
		LOGGER.info("ZoneData: loaded " + zones.size() + " zones.");
	}
	
	public L2ZoneType getZoneById(int zoneId)
	{
		return zones.get(zoneId);
	}
	
	public Map<Integer, L2ZoneType> getAllZones()
	{
		return zones;
	}
	
	public L2ZoneType getZoneByCoordinates(int x, int y, int z)
	{
		for (L2ZoneType zone : zones.values())
		{
			if (zone.isInsideZone(x, y, z))
			{
				return zone;
			}
		}
		
		return null;
	}
}
