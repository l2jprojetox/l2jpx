package net.l2jpx.gameserver.ai.special;

import java.util.concurrent.ThreadLocalRandom;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Effect;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.quest.Quest;

/**
 * @author ReynalDev
 */
public class HotSprings extends Quest
{
	private static final int DISEASE_CHANCE = 1;
	private static final int[] MONSTERS = { 21314, 21316, 21317, 21319, 21321, 21322 };
	private static final int[] DESEASES = { 4551, 4552, 4553, 4554 };
	
	public HotSprings()
	{
		super(-1, "Hot Springs", "Hot Springs deseases");
		
		for (int mobId : MONSTERS)
		{
			addAttackId(mobId);
		}
	}
	
	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (ThreadLocalRandom.current().nextInt(100) < DISEASE_CHANCE && !npc.isCastingNow())
		{
			int level = 1;
			int skillId = DESEASES[ThreadLocalRandom.current().nextInt(DESEASES.length)];
			L2Effect effect = attacker.getFirstEffect(skillId);
			
			if(effect != null)
			{
				level = effect.getLevel() + 1;
			}
			
			if(level > 10)
				level = 10;
				
			SkillTable.getInstance().getInfo(skillId, level).getEffects(npc, attacker);
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
}
