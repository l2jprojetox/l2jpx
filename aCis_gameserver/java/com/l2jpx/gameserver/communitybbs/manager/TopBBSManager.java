package com.l2jpx.gameserver.communitybbs.manager;

import java.util.StringTokenizer;

import com.l2jpx.gameserver.model.actor.Player;

public class TopBBSManager extends BaseBBSManager
{
	protected TopBBSManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_bbshome"))
			loadStaticHtm("index.htm", player);
		else if (command.startsWith("_bbshome;"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			loadStaticHtm(st.nextToken(), player);
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "top/";
	}
	
	public static TopBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TopBBSManager INSTANCE = new TopBBSManager();
	}
}