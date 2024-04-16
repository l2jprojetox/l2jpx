package com.px.gameserver.network.clientpackets;

import com.px.Config;
import com.px.gameserver.communitybbs.CommunityBoard;

public final class RequestShowBoard extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		readD(); // Not used for security reason.
	}
	
	@Override
	protected void runImpl()
	{
		CommunityBoard.getInstance().handleCommands(getClient(), Config.BBS_DEFAULT);
	}
}