package net.sf.l2j.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagic.CorePrivateWarriorCastDDMagicAggressive;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagic.WarriorCastDDMagicAggressive;
import net.sf.l2j.gameserver.skills.L2Skill;

public class CorePrivateWarriorCastDDMagicAggressive extends WarriorCastDDMagicAggressive
{
	public CorePrivateWarriorCastDDMagicAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastDDMagic/CorePrivateWarriorCastDDMagicAggressive");
	}
	
	public CorePrivateWarriorCastDDMagicAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29008, // b02_death_blader,
		29011 // b02_susceptor
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidMute = SkillTable.getInstance().getInfo(4215, 1);
			
			npc.getAI().addCastDesire(caster, raidMute, 1000000);
			
			return;
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	// EventHandler DESIRE_MANIPULATION(speller,desire)
	// {
	// }
}