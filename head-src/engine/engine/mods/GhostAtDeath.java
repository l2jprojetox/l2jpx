package engine.engine.mods;

import net.l2jpx.gameserver.network.serverpackets.NpcInfo;

import engine.data.properties.ConfigData;
import engine.engine.AbstractMod;
import engine.enums.TeamType;
import engine.holders.objects.CharacterHolder;
import engine.holders.objects.NpcHolder;
import engine.holders.objects.PlayerHolder;
import engine.util.UtilSpawn;

/**
 * @author fissban
 */
public class GhostAtDeath extends AbstractMod
{
	// fantasma
	private static final int NPC = 70438;
	// tiempo en desaparecer el fantasma (segundos)
	private static final int DISAPPEAR = 60;
	
	public GhostAtDeath()
	{
		registerMod(ConfigData.GHOST_AT_DEATH_ENABLED);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!(victim instanceof PlayerHolder) || killer == null)
		{
			return;
		}
		
		int x = victim.getInstance().getX();
		int y = victim.getInstance().getY();
		int z = victim.getInstance().getZ();
		
		NpcHolder nh = UtilSpawn.npc(NPC, x, y, z, 0, 0, DISAPPEAR * 1000, TeamType.NONE, victim.getWorldId());
		nh.getInstance().setName(victim.getInstance().getName());
		nh.getInstance().setTitle(victim.getInstance().getName());
		nh.getInstance().broadcastPacket(new NpcInfo(nh.getInstance(), killer.getInstance()));
	}
	
	@Override
	public void onSpawn(NpcHolder npc)
	{
		// TODO Auto-generated method stub
		super.onSpawn(npc);
	}
}
