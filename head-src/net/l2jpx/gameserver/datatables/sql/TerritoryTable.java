package net.l2jpx.gameserver.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2Territory;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author Balancer
 * @author ReynalDev
 */
public class TerritoryTable
{
	private final static Logger LOGGER = Logger.getLogger(TerritoryTable.class);
	private static final String SELECT_TERRITORIES = "SELECT loc_id, loc_x, loc_y, loc_zmin, loc_zmax, proc FROM territories";
	private static Map<Integer, L2Territory> territory = new HashMap<>();
	
	public TerritoryTable()
	{
		territory.clear();
		// load all data at server start
		loadData();
	}
	
	public int[] getRandomPoint(final Integer terr)
	{
		return territory.get(terr).getRandomPoint();
	}
	
	public int getProcMax(final Integer terr)
	{
		return territory.get(terr).getProcMax();
	}
	
	public void loadData()
	{
		territory.clear();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_TERRITORIES);
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				int terr = rset.getInt("loc_id");
				
				if (territory.get(terr) == null)
				{
					L2Territory t = new L2Territory();
					territory.put(terr, t);
				}
				territory.get(terr).add(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_zmin"), rset.getInt("loc_zmax"), rset.getInt("proc"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("TerritoryTable.reload_data : Could not be initialized ", e);
		}
		
		LOGGER.info("TerritoryTable: Loaded " + territory.size() + " territories");
	}
	
	private static class SingletonHolder
	{
		protected static final TerritoryTable instance = new TerritoryTable();
	}
	
	public static TerritoryTable getInstance()
	{
		return SingletonHolder.instance;
	}
	
}
