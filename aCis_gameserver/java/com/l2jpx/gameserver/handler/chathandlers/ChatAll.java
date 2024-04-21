package com.l2jpx.gameserver.handler.chathandlers;

import com.l2jpx.gameserver.enums.FloodProtector;
import com.l2jpx.gameserver.enums.SayType;
import com.l2jpx.gameserver.handler.IChatHandler;
import com.l2jpx.gameserver.handler.IVoicedCommandHandler;
import com.l2jpx.gameserver.handler.VoicedCommandHandler;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.network.serverpackets.CreatureSay;

import java.util.StringTokenizer;

public class ChatAll implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.ALL
	};

	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;

		boolean vcd_used = false;
		StringTokenizer st = null;
		String params = null;
		if (text.startsWith("."))
			st = new StringTokenizer(text);
		IVoicedCommandHandler vch;
		String command = "";
		if (st != null && st.countTokens() > 1)
		{
			command = st.nextToken().substring(1);
			params = text.substring(command.length() + 2);
			vch = VoicedCommandHandler.getInstance().getHandler(command);
		}
		else
		{
			command = text.substring(1);
			vch = VoicedCommandHandler.getInstance().getHandler(command);
		}

		if (vch != null)
		{
			vch.useVoicedCommand(command, player, params);
			vcd_used = true;
		}
		if (!vcd_used)
		{
			CreatureSay cs = new CreatureSay(player.getObjectId(), type, player.getName(), text);

			for (Player knownPlayer : player.getKnownTypeInRadius(Player.class, 1250))
				knownPlayer.sendPacket(cs);
			player.sendPacket(cs);
		}
	}
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}