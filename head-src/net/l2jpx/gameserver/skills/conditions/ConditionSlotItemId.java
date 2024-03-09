package net.l2jpx.gameserver.skills.conditions;

import net.l2jpx.gameserver.model.Inventory;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.skills.Env;

/**
 * @author mkizub
 */
public final class ConditionSlotItemId extends ConditionInventory
{
	
	private final int itemId;
	private final int enchantLevel;
	
	public ConditionSlotItemId(final int slot, final int itemId, final int enchantLevel)
	{
		super(slot);
		this.itemId = itemId;
		this.enchantLevel = enchantLevel;
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
			return itemId == 0;
		}
		return item.getItemId() == itemId && item.getEnchantLevel() >= enchantLevel;
	}
}
