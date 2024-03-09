package net.l2jpx.gameserver.handler.itemhandlers;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.NpcTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2GourdInstance;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

/**
 * @author l2jfrozen dev.
 */
public class JackpotSeed implements IItemHandler
{
	private L2GourdInstance gourd = null;
	
	private static int[] itemIds =
	{
		6389, // small seed
		6390
		// large seed
	};
	
	private static int[] npcIds =
	{
		12774, // Young Pumpkin
		12777
		// Large Young Pumpkin
	};
	
	@Override
	public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2NpcTemplate template1 = null;
		final int itemId = item.getItemId();
		for (int i = 0; i < itemIds.length; i++)
		{
			if (itemIds[i] == itemId)
			{
				template1 = NpcTable.getInstance().getTemplate(npcIds[i]);
				break;
			}
		}
		
		if (template1 == null)
		{
			return;
		}
		
		try
		{
			final L2Spawn spawn = new L2Spawn(template1);
			spawn.setId(IdFactory.getInstance().getNextId());
			spawn.setLocx(activeChar.getX());
			spawn.setLocy(activeChar.getY());
			spawn.setLocz(activeChar.getZ());
			gourd = (L2GourdInstance) spawn.spawnOne();
			L2World.getInstance().storeObject(gourd);
			gourd.setOwner(activeChar.getName());
			activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("Created " + template1.name + " at x: " + spawn.getLocx() + " y: " + spawn.getLocy() + " z: " + spawn.getLocz());
			activeChar.sendPacket(sm);
			sm = null;
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}
			
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("Target is not ingame.");
			activeChar.sendPacket(sm);
			sm = null;
		}
		activeChar = null;
		template1 = null;
	}
	
	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}
