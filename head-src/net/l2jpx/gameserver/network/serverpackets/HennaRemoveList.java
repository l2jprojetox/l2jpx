package net.l2jpx.gameserver.network.serverpackets;

import net.l2jpx.gameserver.model.actor.instance.L2HennaInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;

public class HennaRemoveList extends L2GameServerPacket
{
	private final L2PcInstance player;
	
	public HennaRemoveList(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe5);
		writeD(player.getAdena());
		writeD(player.getHennaEmptySlots());
		writeD(Math.abs(player.getHennaEmptySlots() - 3));
		
		for (int i = 1; i <= 3; i++)
		{
			L2HennaInstance henna = player.getHennas(i);
			
			if (henna != null)
			{
				writeD(henna.getSymbolId());
				writeD(henna.getItemIdDye());
				writeD(henna.getAmountDyeRequire() / 2);
				writeD(henna.getCancelFee());
				writeD(0x01);
			}
		}
	}

	@Override
	public String getType()
	{
		return null;
	}
}