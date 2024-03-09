package net.l2jpx.gameserver.skills.l2skills;

import net.l2jpx.gameserver.idfactory.IdFactory;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.templates.StatsSet;
import net.l2jpx.util.random.Rnd;

/**
 * @author Nemesiss
 */
public class L2SkillCreateItem extends L2Skill
{
	private final int[] createItemId;
	private final int createItemCount;
	private final int randomCount;
	
	public L2SkillCreateItem(final StatsSet set)
	{
		super(set);
		createItemId = set.getIntegerArray("create_item_id");
		createItemCount = set.getInteger("create_item_count", 0);
		randomCount = set.getInteger("random_count", 1);
	}
	
	/**
	 * @see net.l2jpx.gameserver.model.L2Skill#useSkill(net.l2jpx.gameserver.model.L2Character, net.l2jpx.gameserver.model.L2Object[])
	 */
	@Override
	public void useSkill(final L2Character activeChar, final L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		if (createItemId == null || createItemCount == 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE));
			return;
		}
		final L2PcInstance player = (L2PcInstance) activeChar;
		if (activeChar instanceof L2PcInstance)
		{
			final int count = createItemCount * (Rnd.nextInt(randomCount) + 1);
			final int rndid = Rnd.nextInt(createItemId.length);
			giveItems(player, createItemId[rndid], count);
		}
	}
	
	/**
	 * @param activeChar
	 * @param itemId
	 * @param count
	 */
	public void giveItems(final L2PcInstance activeChar, final int itemId, final int count)
	{
		final L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		// if(item == null)
		// return;
		
		item.setCount(count);
		activeChar.getInventory().addItem("Skill", item, activeChar, activeChar);
		
		if (count > 1)
		{
			final SystemMessage smsg = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1S);
			smsg.addItemName(item.getItemId());
			smsg.addNumber(count);
			activeChar.sendPacket(smsg);
		}
		else
		{
			final SystemMessage smsg = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
			smsg.addItemName(item.getItemId());
			activeChar.sendPacket(smsg);
		}
		final ItemList il = new ItemList(activeChar, false);
		activeChar.sendPacket(il);
	}
}
