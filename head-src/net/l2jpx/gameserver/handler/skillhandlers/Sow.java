package net.l2jpx.gameserver.handler.skillhandlers;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.handler.ISkillHandler;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Manor;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Skill.SkillType;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.PlaySound;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.util.random.Rnd;

/**
 * @author l3x
 */
public class Sow implements ISkillHandler
{
	protected static final Logger LOGGER = Logger.getLogger(Sow.class);
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SOW
	};
	
	private L2PcInstance activeCharSow;
	private L2MonsterInstance targetSow;
	private int seedId;
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		activeCharSow = (L2PcInstance) activeChar;
		
		final L2Object[] targetList = skill.getTargetList(activeChar);
		if (targetList == null)
		{
			return;
		}
		
		if (Config.DEBUG)
		{
			LOGGER.info("Casting sow");
		}
		
		for (int index = 0; index < targetList.length; index++)
		{
			if (!(targetList[0] instanceof L2MonsterInstance))
			{
				continue;
			}
			
			targetSow = (L2MonsterInstance) targetList[0];
			if (targetSow.isSeeded())
			{
				activeCharSow.sendPacket(ActionFailed.STATIC_PACKET);
				continue;
			}
			
			if (targetSow.isDead())
			{
				activeCharSow.sendPacket(ActionFailed.STATIC_PACKET);
				continue;
			}
			
			if (targetSow.getSeeder() != activeCharSow)
			{
				activeCharSow.sendPacket(ActionFailed.STATIC_PACKET);
				continue;
			}
			
			seedId = targetSow.getSeedType();
			if (seedId == 0)
			{
				activeCharSow.sendPacket(ActionFailed.STATIC_PACKET);
				continue;
			}
			
			L2ItemInstance item = activeCharSow.getInventory().getItemByItemId(seedId);
			if (item == null)
			{
				activeCharSow.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			// Consuming used seed
			activeCharSow.destroyItem("Consume", item.getObjectId(), 1, null, false);
			item = null;
			
			SystemMessage sm = null;
			if (calcSuccess())
			{
				activeCharSow.sendPacket(new PlaySound("Itemsound.quest_itemget"));
				targetSow.setSeeded();
				sm = new SystemMessage(SystemMessageId.THE_SEED_WAS_SUCCESSFULLY_SOWN);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.THE_SEED_WAS_NOT_SOWN);
			}
			
			if (activeCharSow.getParty() == null)
			{
				activeCharSow.sendPacket(sm);
			}
			else
			{
				activeCharSow.getParty().broadcastToPartyMembers(sm);
			}
			sm = null;
			// TODO: Mob should not agro on player, this way doesn't work really nice
			targetSow.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			
		}
		
	}
	
	private boolean calcSuccess()
	{
		if (activeCharSow == null || targetSow == null)
		{
			return false;
		}
		
		// TODO: check all the chances
		int basicSuccess = (L2Manor.getInstance().isAlternative(seedId) ? 20 : 90);
		int minlevelSeed = 0;
		int maxlevelSeed = 0;
		minlevelSeed = L2Manor.getInstance().getSeedMinLevel(seedId);
		maxlevelSeed = L2Manor.getInstance().getSeedMaxLevel(seedId);
		
		final int levelPlayer = activeCharSow.getLevel(); // Attacker Level
		final int levelTarget = targetSow.getLevel(); // taret Level
		
		// 5% decrease in chance if player level
		// is more then +/- 5 levels to seed's_ level
		if (levelTarget < minlevelSeed)
		{
			basicSuccess -= 5;
		}
		if (levelTarget > maxlevelSeed)
		{
			basicSuccess -= 5;
		}
		
		// 5% decrease in chance if player level
		// is more than +/- 5 levels to target's_ level
		int diff = (levelPlayer - levelTarget);
		if (diff < 0)
		{
			diff = -diff;
		}
		
		if (diff > 5)
		{
			basicSuccess -= 5 * (diff - 5);
		}
		
		// chance can't be less than 1%
		if (basicSuccess < 1)
		{
			basicSuccess = 1;
		}
		
		final int rate = Rnd.nextInt(99);
		
		return (rate < basicSuccess);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
