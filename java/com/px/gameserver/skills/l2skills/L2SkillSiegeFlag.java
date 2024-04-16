package com.px.gameserver.skills.l2skills;

import com.px.commons.data.StatSet;

import com.px.gameserver.data.manager.CastleManager;
import com.px.gameserver.data.manager.ClanHallManager;
import com.px.gameserver.enums.SiegeSide;
import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.idfactory.IdFactory;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.instance.SiegeFlag;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.model.pledge.Clan;
import com.px.gameserver.model.residence.castle.Siege;
import com.px.gameserver.model.residence.clanhall.ClanHallSiege;
import com.px.gameserver.network.SystemMessageId;
import com.px.gameserver.network.serverpackets.SystemMessage;
import com.px.gameserver.skills.L2Skill;

public class L2SkillSiegeFlag extends L2Skill
{
	private final boolean _isAdvanced;
	
	public L2SkillSiegeFlag(StatSet set)
	{
		super(set);
		
		_isAdvanced = set.getBool("isAdvanced", false);
	}
	
	@Override
	public void useSkill(Creature activeChar, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = activeChar.getActingPlayer();
		
		if (!check(player, true))
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		// Template initialization
		final StatSet npcDat = new StatSet();
		
		npcDat.set("id", 35062);
		npcDat.set("type", "SiegeFlag");
		
		npcDat.set("name", clan.getName());
		npcDat.set("usingServerSideName", true);
		
		npcDat.set("hp", (_isAdvanced) ? 100000 : 50000);
		npcDat.set("mp", 0);
		
		npcDat.set("pAtk", 0);
		npcDat.set("mAtk", 0);
		npcDat.set("pDef", 500);
		npcDat.set("mDef", 500);
		
		npcDat.set("runSpd", 0); // Have to keep this, static object MUST BE 0 (critical error otherwise).
		
		npcDat.set("radius", 10);
		npcDat.set("height", 80);
		
		npcDat.set("baseDamageRange", "0;0;80;120");
		
		// Spawn a new flag.
		final SiegeFlag flag = new SiegeFlag(clan, IdFactory.getInstance().getNextId(), new NpcTemplate(npcDat));
		flag.getStatus().setMaxHp();
		flag.spawnMe(player.getPosition());
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param isCheckOnly : If false, send a notification to the {@link Player} telling him why the operation failed.
	 * @return True if the {@link Player} can place a {@link SiegeFlag}.
	 */
	public static boolean check(Player player, boolean isCheckOnly)
	{
		boolean isAttackerUnderActiveSiege = false;
		
		// Check first if an active Siege is under process.
		final Siege siege = CastleManager.getInstance().getActiveSiege(player);
		if (siege != null)
			isAttackerUnderActiveSiege = siege.checkSide(player.getClan(), SiegeSide.ATTACKER);
		else
		{
			// If no Siege, check ClanHallSiege.
			final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(player);
			if (chs != null)
				isAttackerUnderActiveSiege = chs.checkSide(player.getClan(), SiegeSide.ATTACKER);
		}
		
		SystemMessage sm = null;
		if (!isAttackerUnderActiveSiege)
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(247);
		else if (!player.isClanLeader())
			sm = SystemMessage.getSystemMessage(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
		else if (player.getClan().getFlag() != null)
			sm = SystemMessage.getSystemMessage(SystemMessageId.NOT_ANOTHER_HEADQUARTERS);
		else if (!player.isInsideZone(ZoneId.HQ))
			sm = SystemMessage.getSystemMessage(SystemMessageId.NOT_SET_UP_BASE_HERE);
		else if (!player.getKnownTypeInRadius(SiegeFlag.class, 400).isEmpty())
			sm = SystemMessage.getSystemMessage(SystemMessageId.HEADQUARTERS_TOO_CLOSE);
		
		if (sm != null && !isCheckOnly)
			player.sendPacket(sm);
		
		return sm == null;
	}
}