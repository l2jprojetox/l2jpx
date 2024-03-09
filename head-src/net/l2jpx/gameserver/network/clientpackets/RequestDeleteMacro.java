package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestDeleteMacro extends L2GameClientPacket
{
	private int id;
	
	@Override
	protected void readImpl()
	{
		id = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		// Macro exploit fix
		if (!getClient().getFloodProtectors().getMacro().tryPerformAction("delete macro"))
		{
			return;
		}
		
		getClient().getActiveChar().deleteMacro(id);
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString("Delete macro id=" + id);
		sendPacket(sm);
		sm = null;
	}
	
	@Override
	public String getType()
	{
		return "[C] C2 RequestDeleteMacro";
	}
	
}
