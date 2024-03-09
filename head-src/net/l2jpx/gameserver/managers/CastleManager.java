package net.l2jpx.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.entity.sevensigns.SevenSigns;
import net.l2jpx.gameserver.model.entity.siege.Castle;
import net.l2jpx.util.database.L2DatabaseFactory;

public class CastleManager
{
	protected static final Logger LOGGER = Logger.getLogger(CastleManager.class);
	
	private static final String SELECT_CASTLES_ID = "SELECT id FROM castle ORDER by id";
	int castleId = 1; // from this castle
	
	public static final CastleManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private List<Castle> castles;
	
	private static final int castleCirclets[] =
	{
		0,
		6838,
		6835,
		6839,
		6837,
		6840,
		6834,
		6836,
		8182,
		8183
	};
	
	public CastleManager()
	{
		load();
	}
	
	public int findNearestCastlesIndex(L2Object obj)
	{
		int index = getCastleIndex(obj);
		if (index < 0)
		{
			double closestDistance = 99999999;
			double distance;
			Castle castle;
			for (int i = 0; i < getCastles().size(); i++)
			{
				castle = getCastles().get(i);
				
				if (castle == null)
				{
					continue;
				}
				
				distance = castle.getDistance(obj);
				
				if (closestDistance > distance)
				{
					closestDistance = distance;
					index = i;
				}
			}
		}
		return index;
	}
	
	private final void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_CASTLES_ID);
			ResultSet rs = statement.executeQuery())
		{
			while (rs.next())
			{
				getCastles().add(new Castle(rs.getInt("id")));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("CastleManager.load : Could not select castle ids from castle table", e);
		}
	}
	
	public Castle getCastleById(int castleId)
	{
		for (Castle temp : getCastles())
		{
			if (temp.getCastleId() == castleId)
			{
				return temp;
			}
		}
		
		return null;
	}
	
	public Castle getCastleByOwner(L2Clan clan)
	{
		if (clan == null)
		{
			return null;
		}
		
		for (Castle temp : getCastles())
		{
			if (temp != null && temp.getOwnerId() == clan.getClanId())
			{
				return temp;
			}
		}
		
		return null;
	}
	
	public Castle getCastle(String name)
	{
		if (name == null || name.isEmpty())
		{
			return null;
		}
		
		for (Castle temp : getCastles())
		{
			if (temp.getName().equalsIgnoreCase(name.trim()))
			{
				return temp;
			}
		}
		
		return null;
	}
	
	public Castle getCastle(int x, int y, int z)
	{
		for (Castle temp : getCastles())
		{
			if (temp.checkIfInZone(x, y, z))
			{
				return temp;
			}
		}
		
		return null;
	}
	
	public Castle getCastle(L2Object activeObject)
	{
		if (activeObject == null)
		{
			return null;
		}
		
		return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public int getCastleIndex(int castleId)
	{
		Castle castle;
		for (int i = 0; i < getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if (castle != null && castle.getCastleId() == castleId)
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getCastleIndex(L2Object activeObject)
	{
		return getCastleIndex(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public int getCastleIndex(int x, int y, int z)
	{
		Castle castle;
		for (int i = 0; i < getCastles().size(); i++)
		{
			castle = getCastles().get(i);
			if (castle != null && castle.checkIfInZone(x, y, z))
			{
				return i;
			}
		}
		return -1;
	}
	
	public List<Castle> getCastles()
	{
		if (castles == null)
		{
			castles = new ArrayList<>();
		}
		return castles;
	}
	
	public void validateTaxes(int sealStrifeOwner)
	{
		int maxTax;
		
		switch (sealStrifeOwner)
		{
			case SevenSigns.CABAL_DUSK:
				maxTax = 5;
				break;
			case SevenSigns.CABAL_DAWN:
				maxTax = 25;
				break;
			default: // no owner
				maxTax = 15;
				break;
		}
		
		for (Castle castle : castles)
		{
			if (castle.getTaxPercent() > maxTax)
			{
				castle.setTaxPercent(maxTax);
			}
		}
	}
	
	public int getCirclet()
	{
		return getCircletByCastleId(castleId);
	}
	
	public int getCircletByCastleId(final int castleId)
	{
		if (castleId > 0 && castleId < 10)
		{
			return castleCirclets[castleId];
		}
		
		return 0;
	}
	
	private static class SingletonHolder
	{
		protected static final CastleManager instance = new CastleManager();
	}
}
