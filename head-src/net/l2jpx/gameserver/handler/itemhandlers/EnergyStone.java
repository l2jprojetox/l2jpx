package net.l2jpx.gameserver.handler.itemhandlers;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.EtcStatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.skills.effects.EffectCharge;
import net.l2jpx.gameserver.skills.l2skills.L2SkillCharge;

public class EnergyStone implements IItemHandler
{
	
	public EnergyStone()
	{
	}
	
	@Override
	public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
	{
		L2PcInstance activeChar;
		if (playable instanceof L2PcInstance)
		{
			activeChar = (L2PcInstance) playable;
		}
		else if (playable instanceof L2PetInstance)
		{
			activeChar = ((L2PetInstance) playable).getOwner();
		}
		else
		{
			return;
		}
		if (item.getItemId() != 5589)
		{
			return;
		}
		if (activeChar.isAllSkillsDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SITTING));
			return;
		}
		skill = getChargeSkill(activeChar);
		if (skill == null)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_TO_UNSUITABLE_TERMS);
			sm.addItemName(5589);
			activeChar.sendPacket(sm);
			return;
		}
		
		final SystemMessage sm1 = new SystemMessage(SystemMessageId.USE_S1);
		sm1.addItemName(5589);
		activeChar.sendPacket(sm1);
		
		effect = activeChar.getChargeEffect();
		if (effect == null)
		{
			final L2Skill dummy = SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel());
			if (dummy != null)
			{
				dummy.getEffects(activeChar, activeChar);
				final MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, skill.getId(), 1, 1, 0);
				activeChar.sendPacket(MSU);
				activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
			}
			return;
		}
		
		if (effect.numCharges < 2)
		{
			effect.addNumCharges(1);
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FORCE_HAS_INCREASED_TO_S1_LEVEL);
			sm.addNumber(effect.getLevel());
			activeChar.sendPacket(sm);
		}
		else
		{
			if (effect.numCharges == 2)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_FORCE_HAS_REACHED_MAXIMUM_CAPACITY));
			}
		}
		
		final MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, skill.getId(), 1, 1, 0);
		activeChar.sendPacket(MSU);
		activeChar.broadcastPacket(MSU);
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
	}
	
	private L2SkillCharge getChargeSkill(final L2PcInstance activeChar)
	{
		final L2Skill skills[] = activeChar.getAllSkills();
		final L2Skill arr$[] = skills;
		for (final L2Skill s : arr$)
		{
			if (s.getId() == 50 || s.getId() == 8)
			{
				return (L2SkillCharge) s;
			}
		}
		
		return null;
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
	private static final int ITEM_IDS[] =
	{
		5589
	};
	private EffectCharge effect;
	private L2SkillCharge skill;
	
}