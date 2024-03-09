package net.l2jpx.gameserver.handler.itemhandlers.custom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.sql.NpcTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.model.spawn.L2Spawn;
import net.l2jpx.gameserver.network.serverpackets.CreatureSay;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.gameserver.thread.ThreadPoolManager;

public class MonsterSummon implements IItemHandler
{
	
	private static final int ITEM_ID = Config.ITEM_ID_MONSTER; // Substitua pelo ID real do item.
	private static final int MONSTER_ID = Config.MONSTER_ID; // Substitua pelo ID real do monstro a ser sumonado.
	private static final int COOLDOWN = Config.SPAWN_TIME; // 60 segundos de cooldown
	private static final Map<Integer, Long> lastUseTime = new ConcurrentHashMap<>();
	
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		L2PcInstance player = (L2PcInstance) playable;
		
		if (item.getItemId() != ITEM_ID)
			return;
		
		if (!checkCooldown(player))
			return;
		
		summonMonster(player);
		player.destroyItem("Consume", item.getObjectId(), 1, null, false);
	}
	
	private boolean checkCooldown(L2PcInstance player)
	{
		long currentTime = System.currentTimeMillis();
		Long lastUsed = lastUseTime.getOrDefault(player.getObjectId(), 0L);
		
		if (currentTime - lastUsed < COOLDOWN)
		{
			long timeLeft = (COOLDOWN - (currentTime - lastUsed)) / 1000;
			player.sendMessage("Você precisa esperar mais " + timeLeft + " segundos para usar este item novamente.");
			return false;
		}
		
		lastUseTime.put(player.getObjectId(), currentTime);
		return true;
	}
	
	private void summonMonster(L2PcInstance player)
	{
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(MONSTER_ID);
			
			if (template == null)
			{
				player.sendMessage("Erro ao sumonar o monstro.");
				return;
			}
			
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(player.getX() + 50);
			spawn.setLocy(player.getY() + 50);
			spawn.setLocz(player.getZ());
			spawn.setAmount(1);
			spawn.setHeading(player.getHeading());
			spawn.setRespawnDelay(1);
			
			L2MonsterInstance monster = (L2MonsterInstance) spawn.spawnOne();
			
			MagicSkillUser effect = new MagicSkillUser(player, monster, Config.EFFECT_SKILL_ID, Config.EFFECT_SKILL_LEVEL, 0, 0);
			player.sendPacket(effect);
			
			player.sendPacket(new CreatureSay(0, 15, "", Config.SUMMON_MESSAGE));
			
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				if (monster != null && !monster.isDead() && monster.isVisible())
				{
					monster.deleteMe();
					player.sendPacket(new CreatureSay(0, 15, "", Config.DESPAWN_MESSAGE));
				}
			}, Config.SPAWN_TIME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			ITEM_ID
		};
	}
}