package net.l2jpx.gameserver.handler.itemhandlers.custom;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.handler.IItemHandler;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PlayableInstance;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;

public class FullBuffScroll implements IItemHandler
{
	
	private static final int ITEM_ID = 9701; // ID do item
	
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		L2PcInstance player = (L2PcInstance) playable;
		
		if (item.getItemId() == ITEM_ID)
		{
			applyBuffs(player);
			player.destroyItem("Consume", item.getObjectId(), 1, null, false);
		}
	}
	
	private void applyBuffs(L2PcInstance player)
	{
		int[][] buffs =
		{
			{
				1204,
				2
			}, // Wind Walk
			{
				1045,
				3
			}, // Bless the Body
			{
				1068,
				3
			}, // Might
			{
				1035,
				4
			}, // Mental Shield
			{
				1040,
				3
			}, // Shield
			{
				1077,
				3
			}, // Focus
			{
				1086,
				2
			}, // Haste
			{
				1240,
				3
			}, // Guidance
			{
				1036,
				2
			}, // Magic Barrier
			{
				1388,
				3
			}, // Greater Might
			{
				1389,
				3
			}, // Greater Shield
			// Adicione mais buffs conforme necessário
		};
		
		for (int[] buff : buffs)
		{
			int skillId = buff[0];
			int skillLevel = buff[1];
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			if (skill != null)
			{
				skill.getEffects(player, player);
				player.broadcastPacket(new MagicSkillUser(player, player, skill.getId(), skill.getLevel(), 0, 0));
			}
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			ITEM_ID
		};
	}
	
}
