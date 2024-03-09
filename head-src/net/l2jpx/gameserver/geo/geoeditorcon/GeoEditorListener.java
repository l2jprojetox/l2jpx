package net.l2jpx.gameserver.geo.geoeditorcon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import net.l2jpx.Config;

public class GeoEditorListener extends Thread
{
	protected static final Logger LOGGER = Logger.getLogger(GeoEditorListener.class);
	
	private static final class SingletonHolder
	{
		protected static final GeoEditorListener INSTANCE = new GeoEditorListener();
	}
	
	public static GeoEditorListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private ServerSocket serverSocket;
	private GeoEditorThread geoEditor;
	
	protected GeoEditorListener()
	{
		try
		{
			serverSocket = new ServerSocket(Config.GEOEDITOR_PORT);
		}
		catch (IOException e)
		{
			LOGGER.error("Error creating geoeditor listener! ", e);
			System.exit(1);
		}
		start();
		LOGGER.info("GeoEditorListener Initialized.");
	}
	
	public GeoEditorThread getThread()
	{
		return geoEditor;
	}
	
	public String getStatus()
	{
		if (geoEditor != null && geoEditor.isWorking())
		{
			return "Geoeditor connected.";
		}
		return "Geoeditor not connected.";
	}
	
	@Override
	public void run()
	{
		Socket connection = null;
		try
		{
			while (true)
			{
				connection = serverSocket.accept();
				if (geoEditor != null && geoEditor.isWorking())
				{
					LOGGER.warn("Geoeditor already connected!");
					connection.close();
					continue;
				}
				LOGGER.info("Received geoeditor connection from: " + connection.getInetAddress().getHostAddress());
				geoEditor = new GeoEditorThread(connection);
				geoEditor.start();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("GeoEditorListener error ", e);
			try
			{
				if (connection != null)
				{
					connection.close();
				}
			}
			catch (final Exception e2)
			{
				LOGGER.error("GeoEditorListener error while closing connection", e2);
			}
		}
		finally
		{
			try
			{
				serverSocket.close();
			}
			catch (IOException io)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					LOGGER.error("GeoEditorListener error while closing server socket", io);
				}
			}
		}
	}
}