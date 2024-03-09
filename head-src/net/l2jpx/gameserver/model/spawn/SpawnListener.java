package net.l2jpx.gameserver.model.spawn;

import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;

/**
 * This class ...
 * @author  luisantonioa
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public interface SpawnListener
{
	public void npcSpawned(L2NpcInstance npc);
}
