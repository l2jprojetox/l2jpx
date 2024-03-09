package net.l2jpx.gameserver.handler.skillhandlers;

import net.l2jpx.gameserver.ai.CtrlEvent;
import net.l2jpx.gameserver.handler.ISkillHandler;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Skill.SkillType;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.Formulas;

/**
 * @author drunk
 */
public class Spoil implements ISkillHandler
{
	// private static Logger LOGGER = Logger.getLogger(Spoil.class);
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SPOIL
	};
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		if (targets == null)
		{
			return;
		}
		
		for (final L2Object target1 : targets)
		{
			if (!(target1 instanceof L2MonsterInstance))
			{
				continue;
			}
			
			L2MonsterInstance target = (L2MonsterInstance) target1;
			
			if (target.isSpoil())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.IT_HAS_ALREDAY_BEEN_SPOILED));
				continue;
			}
			
			// SPOIL SYSTEM by Lbaldi
			boolean spoil = false;
			if (!target.isDead())
			{
				spoil = Formulas.calcMagicSuccess(activeChar, (L2Character) target1, skill);
				
				if (spoil)
				{
					target.setSpoil(true);
					target.setIsSpoiledBy(activeChar.getObjectId());
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED));
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_RESISTED_YOUR_S2);
					sm.addString(target.getName());
					sm.addSkillName(skill.getDisplayId());
					activeChar.sendPacket(sm);
					sm = null;
				}
				target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar);
			}
			
			target = null;
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
