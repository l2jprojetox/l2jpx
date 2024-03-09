package net.l2jpx.gameserver.model.zone.type;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.base.Race;
import net.l2jpx.gameserver.model.zone.L2ZoneType;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author durgus
 */
public class L2MotherTreeZone extends L2ZoneType
{
	public L2MotherTreeZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			if (player.getRace() == Race.elf)
			{
				player.setInsideZone(L2Character.ZONE_MOTHERTREE, true);
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ENTERED_THE_SHADOW_OF_THE_MOTHER_TREE));
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			if (player.getRace() == Race.elf)
			{
				player.setInsideZone(L2Character.ZONE_MOTHERTREE, false);
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_LEFT_THE_SHADOW_OF_THE_MOTHER_TREE));
			}
		}
	}
	
	@Override
	protected void onDieInside(L2Character character)
	{
	}
	
	@Override
	protected void onReviveInside(L2Character character)
	{
	}
}
