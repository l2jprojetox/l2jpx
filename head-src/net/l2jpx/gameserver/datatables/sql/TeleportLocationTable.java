package net.l2jpx.gameserver.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2TeleportLocation;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author ReynalDev
 */
public class TeleportLocationTable
{
	private final static Logger LOGGER = Logger.getLogger(TeleportLocationTable.class);
	private static final String SELECT_TELEPORTS = "SELECT id, description, loc_x, loc_y, loc_z, item_id, price, fornoble FROM teleport";
	private static final String SELECT_CUSTOM_TELEPORTS = "SELECT id, description, loc_x, loc_y, loc_z, item_id, price, fornoble FROM custom_teleport";
	
	private static TeleportLocationTable instance;
	
	private Map<Integer, L2TeleportLocation> teleports = new HashMap<>();
	
	public static TeleportLocationTable getInstance()
	{
		if (instance == null)
		{
			instance = new TeleportLocationTable();
		}
		
		return instance;
	}
	
	private TeleportLocationTable()
	{
		load();
	}
	
	public void load()
	{
		teleports.clear();
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_TELEPORTS);
			ResultSet rset = statement.executeQuery())
		{
			L2TeleportLocation teleport;
			
			while (rset.next())
			{
				teleport = new L2TeleportLocation();
				
				teleport.setTeleId(rset.getInt("id"));
				teleport.setLocX(rset.getInt("loc_x"));
				teleport.setLocY(rset.getInt("loc_y"));
				teleport.setLocZ(rset.getInt("loc_z"));
				teleport.setItemid(rset.getInt("item_id"));
				teleport.setPrice(rset.getInt("price"));
				teleport.setIsForNoble(rset.getInt("fornoble") == 1);
				
				teleports.put(teleport.getTeleId(), teleport);
			}
			
			LOGGER.info("TeleportLocationTable: Loaded " + teleports.size() + " Teleport Location Templates");
		}
		catch (Exception e)
		{
			LOGGER.error("TeleportLocationTable.load : Error while creating teleport table ", e);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_CUSTOM_TELEPORTS);
			ResultSet rset = statement.executeQuery())
		{
			L2TeleportLocation teleport;
			
			int cTeleCount = teleports.size();
			
			while (rset.next())
			{
				teleport = new L2TeleportLocation();
				teleport.setTeleId(rset.getInt("id"));
				teleport.setLocX(rset.getInt("loc_x"));
				teleport.setLocY(rset.getInt("loc_y"));
				teleport.setLocZ(rset.getInt("loc_z"));
				teleport.setItemid(rset.getInt("item_id"));
				teleport.setPrice(rset.getInt("price"));
				teleport.setIsForNoble(rset.getInt("fornoble") == 1);
				teleports.put(teleport.getTeleId(), teleport);
			}
			
			cTeleCount = teleports.size() - cTeleCount;
			
			if (cTeleCount > 0)
			{
				LOGGER.info("CustomTeleportLocationTable: Loaded " + cTeleCount + " Custom Teleport Location Templates.");
			}
			
		}
		catch (Exception e)
		{
			LOGGER.error("TeleportLocationTable.load : Error while creating custom teleport table ", e);
		}
	}
	
	public L2TeleportLocation getTemplate(int id)
	{
		return teleports.get(id);
	}
}
