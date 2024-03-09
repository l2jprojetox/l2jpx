package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SocialAction;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.util.Util;

public class RequestSocialAction extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestSocialAction.class);
	private int actionId;
	
	@Override
	protected void readImpl()
	{
		actionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		// You cannot do anything else while fishing
		if (activeChar.isFishing())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			activeChar.sendPacket(sm);
			return;
		}
		
		// check if its the actionId is allowed
		if (actionId < 2 || actionId > 13)
		{
			Util.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " requested an internal Social Action.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (activeChar.getPrivateStoreType() == 0 && activeChar.getActiveRequester() == null && !activeChar.isAlikeDead() && (!activeChar.isAllSkillsDisabled() || activeChar.isInDuel()) && activeChar.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
		{
			if (Config.DEBUG)
			{
				LOGGER.debug("Social Action:" + actionId);
			}
			
			SocialAction atk = new SocialAction(activeChar.getObjectId(), actionId);
			activeChar.broadcastPacket(atk);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 1B RequestSocialAction";
	}
}
