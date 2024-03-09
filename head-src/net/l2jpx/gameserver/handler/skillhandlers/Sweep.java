package net.l2jpx.gameserver.handler.skillhandlers;

import net.l2jpx.Config;
import net.l2jpx.gameserver.handler.ISkillHandler;
import net.l2jpx.gameserver.model.L2Attackable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Skill.SkillType;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.holder.RewardHolder;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author drunk
 */
public class Sweep implements ISkillHandler
{
	// private static Logger LOGGER = Logger.getLogger(Sweep.class);
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SWEEP
	};
	
	@Override
	public void useSkill(final L2Character activeChar, final L2Skill skill, final L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		final L2PcInstance player = (L2PcInstance) activeChar;
		final InventoryUpdate iu = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		boolean send = false;
		
		for (final L2Object target1 : targets)
		{
			if (!(target1 instanceof L2Attackable))
			{
				continue;
			}
			
			final L2Attackable target = (L2Attackable) target1;
			RewardHolder[] items = null;
			boolean isSweeping = false;
			synchronized (target)
			{
				if (target.isSweepActive())
				{
					items = target.takeSweep();
					isSweeping = true;
				}
			}
			
			if (isSweeping)
			{
				if (items == null || items.length == 0)
				{
					continue;
				}
				for (final RewardHolder ritem : items)
				{
					if (player.isInParty())
					{
						player.getParty().distributeItem(player, ritem, true, target);
					}
					else
					{
						L2ItemInstance item = player.getInventory().addItem("Sweep", ritem.getRewardId(), ritem.getRewardCount(), player, target);
						if (iu != null)
						{
							iu.addItem(item);
						}
						send = true;
						item = null;
						
						SystemMessage smsg;
						
						if (ritem.getRewardCount() > 1)
						{
							smsg = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1S); // earned $s2$s1
							smsg.addItemName(ritem.getRewardId());
							smsg.addNumber(ritem.getRewardCount());
						}
						else
						{
							smsg = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1); // earned $s1
							smsg.addItemName(ritem.getRewardId());
						}
						player.sendPacket(smsg);
						smsg = null;
					}
				}
			}
			target.endDecayTask();
			
			if (send)
			{
				if (iu != null)
				{
					player.sendPacket(iu);
				}
				else
				{
					player.sendPacket(new ItemList(player, false));
				}
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
