package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.SkillCoolTime;

public class RequestSkillCoolTime extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		// this is just a trigger packet. it has no content
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		
		if (player != null)
		{
			player.sendPacket(new SkillCoolTime(player));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 0xa6 RequestSkillCoolTime";
	}
}