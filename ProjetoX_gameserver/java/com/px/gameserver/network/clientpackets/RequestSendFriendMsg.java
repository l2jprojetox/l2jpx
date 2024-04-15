package com.px.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.px.Config;
import com.px.gameserver.model.World;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.L2FriendSay;

/**
 * Recieve Private (Friend) Message - 0xCC Format: c SS S: Message S: Receiving Player
 * @author Tempy
 */
public final class RequestSendFriendMsg extends L2GameClientPacket
{
	private static final Logger CHAT_LOG = Logger.getLogger("chat");
	
	private String _message;
	private String _reciever;
	
	@Override
	protected void readImpl()
	{
		_message = readS();
		_reciever = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_message == null || _message.isEmpty() || _message.length() > 300)
			return;
		
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		final Player targetPlayer = World.getInstance().getPlayer(_reciever);
		if (targetPlayer == null || !targetPlayer.getFriendList().contains(activeChar.getObjectId()))
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return;
		}
		
		if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, _message);
			record.setLoggerName("chat");
			record.setParameters(new Object[]
			{
				"PRIV_MSG",
				"[" + activeChar.getName() + " to " + _reciever + "]"
			});
			
			CHAT_LOG.log(record);
		}
		
		targetPlayer.sendPacket(new L2FriendSay(activeChar.getName(), _reciever, _message));
	}
}