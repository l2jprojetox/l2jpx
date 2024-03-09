package net.l2jpx.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.managers.CursedWeaponsManager;
import net.l2jpx.gameserver.model.CursedWeapon;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.network.serverpackets.ExCursedWeaponLocation;
import net.l2jpx.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import net.l2jpx.util.Point3D;

/**
 * Format: (ch)
 * @author ProGramMoS
 */
public final class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// ignore read packet
	}
	
	@Override
	protected void runImpl()
	{
		final L2Character activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final List<CursedWeaponInfo> list = new ArrayList<>();
		for (final CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			if (!cw.isActive())
			{
				continue;
			}
			
			final Point3D pos = cw.getWorldPosition();
			
			if (pos != null)
			{
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
			}
		}
		
		if (!list.isEmpty())
		{
			activeChar.sendPacket(new ExCursedWeaponLocation(list));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:23 RequestCursedWeaponLocation";
	}
}
