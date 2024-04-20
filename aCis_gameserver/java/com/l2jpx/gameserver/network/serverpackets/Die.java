package com.l2jpx.gameserver.network.serverpackets;

import com.l2jpx.gameserver.data.manager.CastleManager;
import com.l2jpx.gameserver.data.manager.ClanHallManager;
import com.l2jpx.gameserver.enums.SiegeSide;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.Monster;
import com.l2jpx.gameserver.model.entity.ClanHallSiege;
import com.l2jpx.gameserver.model.entity.Siege;
import com.l2jpx.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket
{
	private final Creature _creature;
	private final int _objectId;
	private final boolean _fake;
	
	private boolean _sweepable;
	private boolean _allowFixedRes;
	private Clan _clan;
	
	public Die(Creature creature)
	{
		_creature = creature;
		_objectId = creature.getObjectId();
		_fake = !creature.isDead();
		
		if (creature instanceof Player)
		{
			Player player = (Player) creature;
			_allowFixedRes = player.getAccessLevel().allowFixedRes();
			_clan = player.getClan();
			
		}
		else if (creature instanceof Monster)
			_sweepable = ((Monster) creature).getSpoilState().isSweepable();
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake)
			return;
		
		writeC(0x06);
		writeD(_objectId);
		writeD(0x01); // to nearest village
		
		if (_clan != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(_creature);
			final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(_creature);
			
			// Check first if an active Siege is under process.
			if (siege != null)
			{
				final SiegeSide side = siege.getSide(_clan);
				
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00); // to castle
				writeD((side == SiegeSide.ATTACKER && _clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
			}
			// If no Siege, check ClanHallSiege.
			else if (chs != null)
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle()) ? 0x01 : 0x00); // to castle
				writeD((chs.checkSide(_clan, SiegeSide.ATTACKER) && _clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
			}
			// We're in peace mode, activate generic teleports.
			else
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle()) ? 0x01 : 0x00); // to castle
				writeD(0x00); // to siege HQ
			}
		}
		else
		{
			writeD(0x00); // to clanhall
			writeD(0x00); // to castle
			writeD(0x00); // to siege HQ
		}
		
		writeD((_sweepable) ? 0x01 : 0x00); // sweepable (blue glow)
		writeD((_allowFixedRes) ? 0x01 : 0x00); // FIXED
	}
}