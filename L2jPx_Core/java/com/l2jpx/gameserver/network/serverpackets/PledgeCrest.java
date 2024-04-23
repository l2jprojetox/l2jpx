package com.l2jpx.gameserver.network.serverpackets;

import com.l2jpx.gameserver.data.cache.CrestCache;
import com.l2jpx.gameserver.data.cache.CrestCache.CrestType;

public class PledgeCrest extends L2GameServerPacket
{
	private final int _crestId;
	private final byte[] _data;
	
	public PledgeCrest(int crestId)
	{
		_crestId = crestId;
		_data = CrestCache.getInstance().getCrest(CrestType.PLEDGE, _crestId);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x6c);
		writeD(_crestId);
		if (_data != null)
		{
			writeD(_data.length);
			writeB(_data);
		}
		else
			writeD(0);
	}
}