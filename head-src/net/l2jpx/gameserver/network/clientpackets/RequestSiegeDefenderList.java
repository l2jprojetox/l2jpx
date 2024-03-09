package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.managers.CastleManager;
import net.l2jpx.gameserver.managers.FortManager;
import net.l2jpx.gameserver.model.entity.siege.Castle;
import net.l2jpx.gameserver.model.entity.siege.Fort;
import net.l2jpx.gameserver.network.serverpackets.FortSiegeDefenderList;
import net.l2jpx.gameserver.network.serverpackets.SiegeDefenderList;

/**
 * @author programmos
 */
public final class RequestSiegeDefenderList extends L2GameClientPacket
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
			
			final SiegeDefenderList sdl = new SiegeDefenderList(castle);
			sendPacket(sdl);
		}
		else
		{
			final Fort fort = FortManager.getInstance().getFortById(castleId);
			
			if (fort == null)
			{
				return;
			}
			
			final FortSiegeDefenderList sdl = new FortSiegeDefenderList(fort);
			sendPacket(sdl);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] a3 RequestSiegeDefenderList";
	}
}
