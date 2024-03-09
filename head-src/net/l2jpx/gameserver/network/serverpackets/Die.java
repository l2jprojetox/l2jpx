package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.autofarm.AutofarmPlayerRoutine;
import net.l2jpx.gameserver.datatables.xml.AccessLevels;
import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.managers.FortManager;
import net.l2jpx.gameserver.model.L2Attackable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Clan;
import net.l2jpx.gameserver.model.L2SiegeClan;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.entity.siege.Castle;
import net.l2jpx.gameserver.model.entity.siege.Fort;
import net.l2jpx.gameserver.templates.AccessLevel;

/**
 * sample 0b 952a1048 objectId 00000000 00000000 00000000 00000000 00000000 00000000 format dddddd rev 377 format ddddddd rev 417
 * @version $Revision: 1.3.3 $ $Date: 2009/04/29 00:46:18 $
 */
public class Die extends L2GameServerPacket
{
	private final int charObjId;
	private final boolean fake;
	private boolean sweepable;
	private boolean canTeleport;
	private AccessLevel access = AccessLevels.getInstance().getUserAccessLevel();
	private L2Clan clan;
	private L2Character activeChar;
	
	public Die(final L2Character cha)
	{
		activeChar = cha;
		if (cha instanceof L2PcInstance)
		{			
			final L2PcInstance player = (L2PcInstance) cha;
			final AutofarmPlayerRoutine bot = player.getBot();
			access = player.getAccessLevel();
			clan = player.getClan();
			canTeleport = !player.isPendingRevive();
			
			if (player.isAutoFarm())
	           {
	               bot.stop();
	               player.setAutoFarm(false);
	           }
			
		}
		charObjId = cha.getObjectId();
		fake = !cha.isDead();
		if (cha instanceof L2Attackable)
		{
			sweepable = ((L2Attackable) cha).isSweepActive();
		}
		
	}
	
	@Override
	protected final void writeImpl()
	{
		if (fake)
		{
			return;
		}
		
		writeC(0x06);
		
		writeD(charObjId);
		// NOTE:
		// 6d 00 00 00 00 - to nearest village
		// 6d 01 00 00 00 - to hide away
		// 6d 02 00 00 00 - to castle
		// 6d 03 00 00 00 - to siege HQ
		// sweepable
		// 6d 04 00 00 00 - FIXED
		
		writeD(canTeleport ? 0x01 : 0); // 6d 00 00 00 00 - to nearest village
		
		if (canTeleport && clan != null)
		{
			L2SiegeClan siegeClan = null;
			Boolean isInDefense = false;
			final Castle castle = CastleManager.getInstance().getCastle(activeChar);
			final Fort fort = FortManager.getInstance().getFort(activeChar);
			
			if (castle != null && castle.getSiege().getIsInProgress())
			{
				// siege in progress
				siegeClan = castle.getSiege().getAttackerClan(clan);
				if (siegeClan == null && castle.getSiege().checkIsDefender(clan))
				{
					isInDefense = true;
				}
			}
			else if (fort != null && fort.getSiege().getIsInProgress())
			{
				// siege in progress
				siegeClan = fort.getSiege().getAttackerClan(clan);
				if (siegeClan == null && fort.getSiege().checkIsDefender(clan))
				{
					isInDefense = true;
				}
			}
			
			writeD(clan.getHasHideout() > 0 ? 0x01 : 0x00); // 6d 01 00 00 00 - to hide away
			writeD(clan.getCastleId() > 0 || clan.getHasFort() > 0 || isInDefense ? 0x01 : 0x00); // 6d 02 00 00 00 - to castle
			writeD(siegeClan != null && !isInDefense && siegeClan.getFlag().size() > 0 ? 0x01 : 0x00); // 6d 03 00 00 00 - to siege HQ
		}
		else
		{
			writeD(0x00); // 6d 01 00 00 00 - to hide away
			writeD(0x00); // 6d 02 00 00 00 - to castle
			writeD(0x00); // 6d 03 00 00 00 - to siege HQ
		}
		
		writeD(sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		writeD(access.allowFixedRes() ? 0x01 : 0x00); // 6d 04 00 00 00 - to FIXED
	}
	
	@Override
	public String getType()
	{
		return "[S] 06 Die";
	}
}
