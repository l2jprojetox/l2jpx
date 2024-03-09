package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.model.Inventory;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public final class ConditionSlotItemType extends ConditionInventory
{
	
	private final int mask;
	
	public ConditionSlotItemType(final int slot, final int mask)
	{
		super(slot);
		this.mask = mask;
	}
	
	@Override
	public boolean testImpl(final Env env)
	{
		if (!(env.player instanceof L2PcInstance))
		{
			return false;
		}
		final Inventory inv = ((L2PcInstance) env.player).getInventory();
		final L2ItemInstance item = inv.getPaperdollItem(slot);
		if (item == null)
		{
			return false;
		}
		return (item.getItem().getItemMask() & mask) != 0;
	}
}
