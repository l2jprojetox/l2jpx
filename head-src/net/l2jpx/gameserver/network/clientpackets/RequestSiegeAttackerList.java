package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.managers.FortManager;
import net.l2jpx.gameserver.model.entity.siege.Castle;
import net.l2jpx.gameserver.model.entity.siege.Fort;
import net.l2jpx.gameserver.network.serverpackets.FortSiegeAttackerList;
import net.l2jpx.gameserver.network.serverpackets.SiegeAttackerList;

/**
 * @author programmos
 */
public final class RequestSiegeAttackerList extends L2GameClientPacket
{
	private int castleId;
	
	@Override
	protected void readImpl()
	{
		castleId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (castleId < 100)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(castleId);
			
			if (castle == null)
			{
				return;
			}
			
			final SiegeAttackerList sal = new SiegeAttackerList(castle);
			sendPacket(sal);
		}
		else
		{
			final Fort fort = FortManager.getInstance().getFortById(castleId);
			
			if (fort == null)
			{
				return;
			}
			
			final FortSiegeAttackerList sal = new FortSiegeAttackerList(fort);
			sendPacket(sal);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] a2 RequestSiegeAttackerList";
	}
}
