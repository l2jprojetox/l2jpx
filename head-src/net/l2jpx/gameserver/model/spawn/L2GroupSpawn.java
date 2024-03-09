package net.l2jpx.gameserver.model.spawn;

import java.lang.reflect.Constructor;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.TerritoryTable;
import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.util.random.Rnd;

/**
 * @author littlecrow A special spawn implementation to spawn controllable mob
 */
public class L2GroupSpawn extends L2Spawn
{
	private final Constructor<?> constructor;
	private final L2NpcTemplate template;
	
	public L2GroupSpawn(final L2NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException
	{
		super(mobTemplate);
		constructor = Class.forName("net.l2jpx.gameserver.model.actor.instance.L2ControllableMobInstance").getConstructors()[0];
		template = mobTemplate;
		
		setAmount(1);
	}
	
	public L2NpcInstance doGroupSpawn()
	{
		L2NpcInstance mob = null;
		
		try
		{
			if (template.type.equalsIgnoreCase("L2Pet") || template.type.equalsIgnoreCase("L2Minion"))
			{
				return null;
			}
			
			Object[] parameters =
			{
				IdFactory.getInstance().getNextId(),
				template
			};
			Object tmp = constructor.newInstance(parameters);
			
			if (!(tmp instanceof L2NpcInstance))
			{
				return null;
			}
			
			mob = (L2NpcInstance) tmp;
			
			int newlocx, newlocy, newlocz;
			
			if (getLocx() == 0 && getLocy() == 0)
			{
				if (getLocation() == 0)
				{
					return null;
				}
				
				final int p[] = TerritoryTable.getInstance().getRandomPoint(getLocation());
				newlocx = p[0];
				newlocy = p[1];
				newlocz = p[2];
			}
			else
			{
				newlocx = getLocx();
				newlocy = getLocy();
				newlocz = getLocz();
			}
			
			mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
			
			if (getHeading() == -1)
			{
				mob.setHeading(Rnd.nextInt(61794));
			}
			else
			{
				mob.setHeading(getHeading());
			}
			
			mob.setSpawn(this);
			mob.spawnMe(newlocx, newlocy, newlocz);
			mob.onSpawn();
			
			if (Config.DEBUG)
			{
				LOGGER.debug("spawned Mob ID: " + template.npcId + " ,at: " + mob.getX() + " x, " + mob.getY() + " y, " + mob.getZ() + " z");
			}
			
			parameters = null;
			tmp = null;
			
			return mob;
			
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}
			
			LOGGER.warn("NPC class not found: " + e);
			return null;
		}
	}
}
