package net.l2jpx.gameserver.handler.itemhandlers;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.managers.CastleManorManager;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2MonsterInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author l3x
 */
public class Harvester implements IItemHandler
{
	
	private static final int[] ITEM_IDS =
	{
		5125
		/* Harvester */
	};
	L2PcInstance activeChar;
	L2MonsterInstance target;
	
	@Override
	public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		if (CastleManorManager.getInstance().isDisabled())
		{
			return;
		}
		
		activeChar = (L2PcInstance) playable;
		if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof L2MonsterInstance))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THAT_IS_THE_INCORRECT_TARGET));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		target = (L2MonsterInstance) activeChar.getTarget();
		if (target == null || !target.isDead())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(2098, 1); // harvesting skill
		activeChar.useMagic(skill, false, false);
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
