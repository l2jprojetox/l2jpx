package com.px.gameserver.scripting.script.ai.individual.AgitWarrior;

import com.px.commons.random.Rnd;

import com.px.gameserver.data.SkillTable;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.scripting.script.ai.individual.DefaultNpc;
import com.px.gameserver.skills.L2Skill;

public class AgitWarrior extends DefaultNpc
{
	private static final L2Skill NPC_STRIKE = SkillTable.getInstance().getInfo(4032, 6);
	
	private static final int DEST_X = 83311;
	private static final int DEST_Y = -16331;
	private static final int DEST_Z = -1840;
	
	public AgitWarrior()
	{
		super("ai/individual/AgitWarrior");
	}
	
	public AgitWarrior(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35428,
		35618
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			npc.forceRunStance();
			npc.getAI().addMoveToDesire(new Location(DEST_X + Rnd.get(400), DEST_Y + Rnd.get(400), DEST_Z), 5);
		}
		return null;
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("2001", npc, null, (Rnd.get(6) + 5) * 1000);
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		npc.lookNeighbor();
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		if (player != null && (player.getClanId() != npc.getClanId() || player.getClanId() == 0))
		{
			npc.getAI().addAttackDesire(attacker, ((((double) damage) / npc.getStatus().getMaxHp()) / 0.05) * (attacker instanceof Player ? 100 : 10));
			
			if (Rnd.get(100) < 10)
				npc.getAI().addCastDesire(attacker, NPC_STRIKE, 1000000);
		}
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		final Player player = creature.getActingPlayer();
		if (player != null && (player.getClanId() != npc.getClanId() || player.getClanId() == 0))
			npc.getAI().addAttackDesire(player, 200);
	}
}