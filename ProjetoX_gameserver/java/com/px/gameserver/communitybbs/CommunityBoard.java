package com.px.gameserver.communitybbs;

import com.px.Config;
import com.px.gameserver.communitybbs.Manager.BaseBBSManager;
import com.px.gameserver.communitybbs.Manager.ClanBBSManager;
import com.px.gameserver.communitybbs.Manager.FriendsBBSManager;
import com.px.gameserver.communitybbs.Manager.MailBBSManager;
import com.px.gameserver.communitybbs.Manager.PostBBSManager;
import com.px.gameserver.communitybbs.Manager.RegionBBSManager;
import com.px.gameserver.communitybbs.Manager.TopBBSManager;
import com.px.gameserver.communitybbs.Manager.TopicBBSManager;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.GameClient;
import com.px.gameserver.network.SystemMessageId;

public class CommunityBoard
{
	protected CommunityBoard()
	{
	}
	
	public void handleCommands(GameClient client, String command)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (!Config.ENABLE_COMMUNITY_BOARD)
		{
			player.sendPacket(SystemMessageId.CB_OFFLINE);
			return;
		}
		
		if (command.startsWith("_bbshome"))
			TopBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsloc"))
			RegionBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsclan"))
			ClanBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmemo"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmail") || command.equals("_maillist_0_1_0_"))
			MailBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_friend") || command.startsWith("_block"))
			FriendsBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbstopics"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsposts"))
			PostBBSManager.getInstance().parseCmd(command, player);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + command + " isn't implemented.</center></body></html>", player);
	}
	
	public void handleWriteCommands(GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (!Config.ENABLE_COMMUNITY_BOARD)
		{
			player.sendPacket(SystemMessageId.CB_OFFLINE);
			return;
		}
		
		if (url.equals("Topic"))
			TopicBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Post"))
			PostBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsloc"))
			RegionBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsclan"))
			ClanBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Mail"))
			MailBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_friend"))
			FriendsBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + url + " isn't implemented.</center></body></html>", player);
	}
	
	public static CommunityBoard getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityBoard INSTANCE = new CommunityBoard();
	}
}