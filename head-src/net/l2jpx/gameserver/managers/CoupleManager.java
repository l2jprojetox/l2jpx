package net.l2jpx.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.Wedding;
import net.l2jpx.util.database.L2DatabaseFactory;

/**
 * @author evill33t
 */
public class CoupleManager
{
	protected static final Logger LOGGER = Logger.getLogger(CoupleManager.class);
	private static final String SELECT_COUPLES_ID = "SELECT id FROM character_wedding ORDER BY id";
	
	private List<Wedding> couples = new ArrayList<>();
	
	public static final CoupleManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	public CoupleManager()
	{
		LOGGER.info("Initializing CoupleManager");
		couples.clear();
		load();
	}
	
	public void reload()
	{
		couples.clear();
		load();
	}
	
	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_COUPLES_ID);
			ResultSet rs = statement.executeQuery())
		{
			while (rs.next())
			{
				getCouples().add(new Wedding(rs.getInt("id")));
			}
			
			if (!getCouples().isEmpty())
			{
				LOGGER.info("Loaded: " + getCouples().size() + " couples(s)");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("CoupleManager.load : Could not select from character_wedding table", e);
		}
	}
	
	public Wedding getCouple(int coupleId)
	{
		int index = getCoupleIndex(coupleId);
		
		if (index >= 0)
		{
			return getCouples().get(index);
		}
		
		return null;
	}
	
	public void createCouple(L2PcInstance player1, L2PcInstance player2)
	{
		if (player1 != null && player2 != null)
		{
			if (player1.getPartnerId() == 0 && player2.getPartnerId() == 0)
			{
				int player1id = player1.getObjectId();
				int player2id = player2.getObjectId();
				
				Wedding newWedding = new Wedding(player1, player2);
				getCouples().add(newWedding);
				player1.setPartnerId(player2id);
				player2.setPartnerId(player1id);
				player1.setCoupleId(newWedding.getId());
				player2.setCoupleId(newWedding.getId());
			}
		}
	}
	
	public void deleteCouple(int coupleId)
	{
		int index = getCoupleIndex(coupleId);
		Wedding wedding = getCouples().get(index);
		
		if (wedding != null)
		{
			L2PcInstance player1 = (L2PcInstance) L2World.getInstance().findObject(wedding.getPlayer1Id());
			L2PcInstance player2 = (L2PcInstance) L2World.getInstance().findObject(wedding.getPlayer2Id());
			
			if (player1 != null)
			{
				player1.setPartnerId(0);
				player1.setMarried(false);
				player1.setCoupleId(0);
				
			}
			
			if (player2 != null)
			{
				player2.setPartnerId(0);
				player2.setMarried(false);
				player2.setCoupleId(0);
				
			}
			
			wedding.divorce();
			getCouples().remove(index);
		}
	}
	
	public int getCoupleIndex(int coupleId)
	{
		int i = 0;
		
		for (Wedding temp : getCouples())
		{
			if (temp != null && temp.getId() == coupleId)
			{
				temp = null;
				return i;
			}
			i++;
		}
		
		return -1;
	}
	
	public List<Wedding> getCouples()
	{
		return couples;
	}
	
	private static class SingletonHolder
	{
		protected static final CoupleManager instance = new CoupleManager();
	}
}
