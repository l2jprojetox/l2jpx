package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2ClanMember;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public class RequestGiveNickName extends L2GameClientPacket
{
	static Logger LOGGER = Logger.getLogger(RequestGiveNickName.class);
	
	private String target;
	private String title;
	
	@Override
	protected void readImpl()
	{
		target = readS();
		title = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Noblesse can bestow a title to themselves
		if (activeChar.isNoble() && target.matches(activeChar.getName()))
		{
			activeChar.setTitle(title);
			final SystemMessage sm = new SystemMessage(SystemMessageId.TITLE_CHANGED);
			activeChar.sendPacket(sm);
			activeChar.broadcastTitleInfo();
		}
		// Can the player change/give a title?
		else if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_GIVE_TITLE) == L2Clan.CP_CL_GIVE_TITLE)
		{
			if (activeChar.getClan().getLevel() < 3)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE);
				activeChar.sendPacket(sm);
				sm = null;
				return;
			}
			
			final L2ClanMember member1 = activeChar.getClan().getClanMember(target);
			if (member1 != null)
			{
				final L2PcInstance member = member1.getPlayerInstance();
				if (member != null)
				{
					// is target from the same clan?
					member.setTitle(title);
					SystemMessage sm = new SystemMessage(SystemMessageId.TITLE_CHANGED);
					member.sendPacket(sm);
					member.broadcastTitleInfo();
					sm = null;
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
					sm.addString("Target needs to be online to get a title");
					activeChar.sendPacket(sm);
					sm = null;
				}
			}
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Target does not belong to your clan");
				activeChar.sendPacket(sm);
				sm = null;
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 55 RequestGiveNickName";
	}
}
