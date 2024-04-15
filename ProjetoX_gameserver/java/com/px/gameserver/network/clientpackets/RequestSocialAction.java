package com.px.gameserver.network.clientpackets;

import com.px.gameserver.enums.IntentionType;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.network.FloodProtectors;
import com.px.gameserver.network.FloodProtectors.Action;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SocialAction;

public class RequestSocialAction extends L2GameClientPacket
{
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_actionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.SOCIAL))
			return;
		
		final Player activeChar = getClient().getPlayer();
		if (activeChar == null)
			return;
		
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		if (_actionId < 2 || _actionId > 13)
			return;
		
		if (activeChar.isInStoreMode() || activeChar.getActiveRequester() != null || activeChar.isAlikeDead() || activeChar.getAI().getDesire().getIntention() != IntentionType.IDLE)
			return;
		
		activeChar.broadcastPacket(new SocialAction(activeChar, _actionId));
	}
}