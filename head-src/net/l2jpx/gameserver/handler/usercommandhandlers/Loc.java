package net.l2jpx.gameserver.handler.usercommandhandlers;

import net.l2jpx.gameserver.datatables.csv.MapRegionTable;
import net.l2jpx.gameserver.handler.IUserCommandHandler;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 *
 *
 */
public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar)
	{
		final int nearestTown = MapRegionTable.getInstance().getClosestTownNumber(activeChar);
		SystemMessageId msg;
		
		switch (nearestTown)
		{
			case 0:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_TALKING_ISLAND_VILLAGE;
				break;
			case 1:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ELVEN_VILLAGE;
				break;
			case 2:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DARK_ELF_VILLAGE;
				break;
			case 3:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ORC_VILLAGE;
				break;
			case 4:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DWARVEN_VILLAGE;
				break;
			case 5:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GLUDIO;
				break;
			case 6:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_GLUDIN_VILLAGE;
				break;
			case 7:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_DION;
				break;
			case 8:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GIRAN;
				break;
			case 9:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_OREN;
				break;
			case 10:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_ADEN_CASTLE_TOWN;
				break;
			case 11:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_HUNTERS_VILLAGE;
				break;
			case 12:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_GIRAN_HARBOR;
				break;
			case 13:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_HEINE;
				break;
			case 14:
				msg = SystemMessageId.LOC_RUNE_S1_S2_S3;
				break;
			case 15:
				msg = SystemMessageId.LOC_GODDARD_S1_S2_S3;
				break;
			case 16:
				msg = SystemMessageId.LOC_SCHUTTGART_S1_S2_S3;
				break;
			case 17:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_THE_FLORAN_VILLAGE;
				break;
			case 18:
				msg = SystemMessageId.LOC_PRIMEVAL_ISLE_S1_S2_S3;
				break;
			default:
				msg = SystemMessageId.CURRENT_LOCATION_S1_S2_S3_NEAR_ADEN_CASTLE_TOWN;
		}
		
		SystemMessage sm = new SystemMessage(msg);
		msg = null;
		sm.addNumber(activeChar.getX());
		sm.addNumber(activeChar.getY());
		sm.addNumber(activeChar.getZ());
		activeChar.sendPacket(sm);
		sm = null;
		
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
