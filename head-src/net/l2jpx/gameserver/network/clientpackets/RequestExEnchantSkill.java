package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.datatables.sql.SkillTreeTable;
import net.l2jpx.gameserver.datatables.xml.ExperienceData;
import net.l2jpx.gameserver.model.L2EnchantSkillLearn;
import net.l2jpx.gameserver.model.L2ShortCut;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2FolkInstance;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ShortCutRegister;
import net.l2jpx.gameserver.network.serverpackets.StatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.network.serverpackets.UserInfo;
import net.l2jpx.gameserver.util.IllegalPlayerAction;
import net.l2jpx.gameserver.util.Util;
import net.l2jpx.util.L2Log;
import net.l2jpx.util.random.Rnd;

/**
 * Format chdd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 * @author -Wooden-
 */
public final class RequestExEnchantSkill extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestAquireSkill.class);
	private int skillId;
	private int skillLvl;
	
	@Override
	protected void readImpl()
	{
		skillId = readD();
		skillLvl = readD();
	}
	
	@Override
	protected void runImpl()
	{
		
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2FolkInstance trainer = player.getLastFolkNPC();
		if (trainer == null)
		{
			return;
		}
		
		final int npcid = trainer.getNpcId();
		
		if (!player.isInsideRadius(trainer, L2NpcInstance.INTERACTION_DISTANCE, false, false) && !player.isGM())
		{
			return;
		}
		
		if (player.getSkillLevel(skillId) >= skillLvl)
		{
			return;
		}
		
		if (player.getClassId().getId() < 88)
		{
			return;
		}
		
		if (player.getLevel() < 76)
		{
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
		
		int counts = 0;
		int requiredSp = 10000000;
		int requiredExp = 100000;
		byte rate = 0;
		int baseLvl = 1;
		
		final L2EnchantSkillLearn[] skills = SkillTreeTable.getInstance().getAvailableEnchantSkills(player);
		
		for (final L2EnchantSkillLearn s : skills)
		{
			final L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
			
			if (sk == null || sk != skill || !sk.getCanLearn(player.getClassId()) || !sk.canTeachBy(npcid))
			{
				continue;
			}
			
			counts++;
			requiredSp = s.getSpCost();
			requiredExp = s.getExp();
			rate = s.getRate(player);
			baseLvl = s.getBaseLevel();
		}
		
		if (counts == 0 && !Config.ALT_GAME_SKILL_LEARN)
		{
			player.sendMessage("You are trying to learn skill that u can't..");
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to learn skill that he can't!!!", IllegalPlayerAction.PUNISH_KICK);
			return;
		}
		
		if (player.getSp() >= requiredSp)
		{
			// Like L2OFF you can't delevel during skill enchant
			final long expAfter = player.getExp() - requiredExp;
			if (player.getExp() >= requiredExp && expAfter >= ExperienceData.getInstance().getExpForLevel(player.getLevel()))
			{
				if (Config.ES_SP_BOOK_NEEDED && (skillLvl == 101 || skillLvl == 141)) // only first lvl requires book
				{
					final int spbId = 6622;
					
					final L2ItemInstance spb = player.getInventory().getItemByItemId(spbId);
					
					if (spb == null)// Haven't spellbook
					{
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL));
						return;
					}
					// ok
					player.destroyItem("Consume", spb, trainer, true);
				}
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
				player.sendPacket(sm);
				return;
			}
		}
		else
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			player.sendPacket(sm);
			return;
		}
		if (Rnd.get(100) <= rate)
		{
			player.addSkill(skill, true);
			
			if (Config.DEBUG)
			{
				LOGGER.debug("Learned skill " + skillId + " for " + requiredSp + " SP.");
			}
			
			player.getStat().removeExpAndSp(requiredExp, requiredSp);
			
			final StatusUpdate su = new StatusUpdate(player.getObjectId());
			su.addAttribute(StatusUpdate.SP, player.getSp());
			player.sendPacket(su);
			
			final SystemMessage ep = new SystemMessage(SystemMessageId.YOU_EXPERIENCE_HAS_DECREASED_BY_S1);
			ep.addNumber(requiredExp);
			sendPacket(ep);
			
			final SystemMessage sp = new SystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
			sp.addNumber(requiredSp);
			sendPacket(sp);
			
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1);
			sm.addSkillName(skillId);
			player.sendPacket(sm);
			
			L2Log.add("Enchant skill successful for player " + player.getName() + ", skill " + skill.getName() + "(" + skill.getId() + ") to level " + player.getSkillLevel(skill.getId()), "enchant_skill");
		}
		else
		{
			if (skill.getLevel() > 100)
			{
				skillLvl = baseLvl;
				player.addSkill(SkillTable.getInstance().getInfo(skillId, skillLvl), true);
				player.sendSkillList();
			}
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1);
			sm.addSkillName(skillId);
			player.sendPacket(sm);
			
			L2Log.add("Enchant skill failed for player " + player.getName() + ", skill " + skill.getName() + "(" + skill.getId() + ") to level " + skill.getLevel(), "enchant_skill");
		}
		trainer.showEnchantSkillList(player, player.getClassId());
		
		player.sendPacket(new UserInfo(player));
		player.sendSkillList();
		
		// update all the shortcuts to this skill
		
		for (L2ShortCut sc : player.getAllShortCuts())
		{
			if (sc.getId() == skillId && sc.getType() == L2ShortCut.TYPE_SKILL)
			{
				L2ShortCut newsc = new L2ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLvl, 1);
				player.sendPacket(new ShortCutRegister(newsc));
				player.registerShortCut(newsc);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:07 RequestExEnchantSkill";
	}
	
}
