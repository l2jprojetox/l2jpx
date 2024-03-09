package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.GMViewCharacterInfo;
import net.l2jpx.gameserver.network.serverpackets.GMViewItemList;
import net.l2jpx.gameserver.network.serverpackets.GMViewPledgeInfo;
import net.l2jpx.gameserver.network.serverpackets.GMViewQuestList;
import net.l2jpx.gameserver.network.serverpackets.GMViewSkillInfo;
import net.l2jpx.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;

public final class RequestGMCommand extends L2GameClientPacket
{
	static Logger LOGGER = Logger.getLogger(RequestGMCommand.class);
	
	private String targetName;
	private int command;
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
		command = readD();
		// unknown = readD();
	}
	
	@Override
	protected void runImpl()
	{
		
		final L2PcInstance player = L2World.getInstance().getPlayerByName(targetName);
		
		// prevent non gm or low level GMs from vieweing player stuff
		if (player == null || !getClient().getActiveChar().getAccessLevel().allowAltG())
		{
			return;
		}
		
		switch (command)
		{
			case 1: // player status
			{
				sendPacket(new GMViewCharacterInfo(player));
				break;
			}
			case 2: // player clan
			{
				if (player.getClan() != null)
				{
					sendPacket(new GMViewPledgeInfo(player.getClan(), player));
				}
				break;
			}
			case 3: // player skills
			{
				sendPacket(new GMViewSkillInfo(player));
				break;
			}
			case 4: // player quests
			{
				sendPacket(new GMViewQuestList(player));
				break;
			}
			case 5: // player inventory
			{
				sendPacket(new GMViewItemList(player));
				break;
			}
			case 6: // player warehouse
			{
				// gm warehouse view to be implemented
				sendPacket(new GMViewWarehouseWithdrawList(player));
				break;
			}
			
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 6e RequestGMCommand";
	}
}
