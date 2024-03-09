package net.l2jpx.loginserver.datatables.xml;

import java.io.File;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.loginserver.GameServerThread;
import net.l2jpx.loginserver.network.gameserverpackets.ServerStatus;
import net.l2jpx.util.database.L2DatabaseFactory;
import net.l2jpx.util.random.Rnd;

/**
 * @author KenM
 * @author ReynalDev
 */
public class GameServerTable
{
	private static final Logger LOGGER = Logger.getLogger(GameServerTable.class);
	private static final String SELECT_GAMESERVERS = "SELECT server_id, hexid FROM gameservers";
	private static final String INSERT_GAMESERVER = "INSERT INTO gameservers (hexid,server_id) VALUES (?,?)";
	private static GameServerTable instance;
	
	// Server Names Config
	private static Map<Integer, String> serverNames = new HashMap<>();
	
	// Game Server Table
	private final Map<Integer, GameServerInfo> gameServerTable = new ConcurrentHashMap<>();
	
	// RSA Config
	private static final int KEYS_SIZE = 10;
	private KeyPair[] keyPairs;
	
	public static void load() throws GeneralSecurityException
	{
		if (instance == null)
		{
			instance = new GameServerTable();
		}
		else
		{
			throw new IllegalStateException("Load can only be invoked a single time.");
		}
	}
	
	public static GameServerTable getInstance()
	{
		return instance;
	}
	
	public GameServerTable() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		loadServerNames();
		LOGGER.info("Loaded " + serverNames.size() + " server names");
		
		loadRegisteredGameServers();
		LOGGER.info("Loaded " + gameServerTable.size() + " registered Game Servers");
		
		loadRSAKeys();
		LOGGER.info("Cached " + keyPairs.length + " RSA keys for Game Server communication.");
	}
	
	private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
		keyGen.initialize(spec);
		
		keyPairs = new KeyPair[KEYS_SIZE];
		
		for (int i = 0; i < KEYS_SIZE; i++)
		{
			keyPairs[i] = keyGen.genKeyPair();
		}
	}
	
	private void loadServerNames()
	{
		String path = "config/others/servername.xml";
		try
		{
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("server");
			
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) nNode;
					
					int id = Integer.parseInt(element.getAttribute("id"));
					String name = element.getAttribute("name");
					serverNames.put(id, name);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Could not load servername.xml file", e);
		}
	}
	
	private void loadRegisteredGameServers()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_GAMESERVERS);
			ResultSet rset = statement.executeQuery())
		{
			int id;
			GameServerInfo gsi;
			
			while (rset.next())
			{
				id = rset.getInt("server_id");
				gsi = new GameServerInfo(id, stringToHex(rset.getString("hexid")));
				gameServerTable.put(id, gsi);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("GameServerTable.loadRegisteredGameServers : Could not select from gameservers table", e);
		}
		
	}
	
	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return gameServerTable;
	}
	
	public GameServerInfo getRegisteredGameServerById(final int id)
	{
		return gameServerTable.get(id);
	}
	
	public boolean hasRegisteredGameServerOnId(final int id)
	{
		return gameServerTable.containsKey(id);
	}
	
	
	public boolean register(final int id, final GameServerInfo gsi)
	{
		// avoid two servers registering with the same id
		if (!gameServerTable.containsKey(id))
		{
			gameServerTable.put(id, gsi);
			gsi.setId(id);
			return true;
		}
		return false;
	}
	
	public void registerServerOnDB(final GameServerInfo gsi)
	{
		this.registerServerOnDB(gsi.getHexId(), gsi.getId());
	}
	
	public void registerServerOnDB(final byte[] hexId, final int id)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_GAMESERVER))
		{
			statement.setString(1, hexToString(hexId));
			statement.setInt(2, id);
			statement.executeUpdate();
		}
		catch (final SQLException e)
		{
			LOGGER.error("GameServerTable.registerServerOnDB : Could not insert into gamservers table", e);
		}
	}
	
	public String getServerNameById(final int id)
	{
		return getServerNames().get(id);
	}
	
	public Map<Integer, String> getServerNames()
	{
		return serverNames;
	}
	
	public KeyPair getKeyPair()
	{
		return keyPairs[Rnd.nextInt(10)];
	}
	
	private byte[] stringToHex(final String string)
	{
		return new BigInteger(string, 16).toByteArray();
	}
	
	private String hexToString(final byte[] hex)
	{
		if (hex == null)
		{
			return "null";
		}
		
		return new BigInteger(hex).toString(16);
	}
	
	public static class GameServerInfo
	{
		// auth
		private int id;
		private final byte[] hexId;
		private boolean isAuthed;
		
		// status
		private GameServerThread gst;
		private int status;
		
		// network
		private String internalIp;
		private String externalIp;
		private String externalHost;
		private int port;
		
		// config
		private final boolean isPvp = true;
		private boolean isTestServer;
		private boolean isShowingClock;
		private boolean isShowingBrackets;
		private int maxPlayers;
		
		public GameServerInfo(final int id, final byte[] hexId, final GameServerThread gst)
		{
			this.id = id;
			this.hexId = hexId;
			this.gst = gst;
			status = ServerStatus.STATUS_DOWN;
		}
		
		public GameServerInfo(final int id, final byte[] hexId)
		{
			this(id, hexId, null);
		}
		
		public void setId(final int id)
		{
			this.id = id;
		}
		
		public int getId()
		{
			return id;
		}
		
		public byte[] getHexId()
		{
			return hexId;
		}
		
		public void setAuthed(final boolean isAuthed)
		{
			this.isAuthed = isAuthed;
		}
		
		public boolean isAuthed()
		{
			return isAuthed;
		}
		
		public void setGameServerThread(final GameServerThread gst)
		{
			this.gst = gst;
		}
		
		public GameServerThread getGameServerThread()
		{
			return gst;
		}
		
		public void setStatus(final int status)
		{
			this.status = status;
		}
		
		public int getStatus()
		{
			return status;
		}
		
		public int getCurrentPlayerCount()
		{
			if (gst == null)
			{
				return 0;
			}
			
			return gst.getPlayerCount();
		}
		
		public void setInternalIp(final String internalIp)
		{
			this.internalIp = internalIp;
		}
		
		public String getInternalHost()
		{
			return internalIp;
		}
		
		public void setExternalIp(final String externalIp)
		{
			this.externalIp = externalIp;
		}
		
		public String getExternalIp()
		{
			return externalIp;
		}
		
		public void setExternalHost(final String externalHost)
		{
			this.externalHost = externalHost;
		}
		
		public String getExternalHost()
		{
			return externalHost;
		}
		
		public int getPort()
		{
			return port;
		}
		
		public void setPort(final int port)
		{
			this.port = port;
		}
		
		public void setMaxPlayers(final int maxPlayers)
		{
			this.maxPlayers = maxPlayers;
		}
		
		public int getMaxPlayers()
		{
			return maxPlayers;
		}
		
		public boolean isPvp()
		{
			return isPvp;
		}
		
		public void setTestServer(final boolean val)
		{
			isTestServer = val;
		}
		
		public boolean isTestServer()
		{
			return isTestServer;
		}
		
		public void setShowingClock(final boolean clock)
		{
			isShowingClock = clock;
		}
		
		public boolean isShowingClock()
		{
			return isShowingClock;
		}
		
		public void setShowingBrackets(final boolean val)
		{
			isShowingBrackets = val;
		}
		
		public boolean isShowingBrackets()
		{
			return isShowingBrackets;
		}
		
		public void setDown()
		{
			setAuthed(false);
			setPort(0);
			setGameServerThread(null);
			setStatus(ServerStatus.STATUS_DOWN);
		}
	}
}
