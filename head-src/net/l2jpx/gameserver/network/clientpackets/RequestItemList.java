package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.network.serverpackets.ItemList;

public final class RequestItemList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		if (getClient() != null && getClient().getActiveChar() != null && !getClient().getActiveChar().isInvetoryDisabled())
		{
			final ItemList il = new ItemList(getClient().getActiveChar(), true);
			sendPacket(il);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 0F RequestItemList";
	}
}
