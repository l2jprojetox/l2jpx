package engine.engine.mods;

import net.l2jpx.gameserver.datatables.sql.ArmorSetsTable;
import net.l2jpx.gameserver.model.Inventory;
import net.l2jpx.gameserver.model.L2ArmorSet;
import net.l2jpx.gameserver.model.PcInventory;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;

import engine.data.properties.ConfigData;
import engine.engine.AbstractMod;
import engine.holders.objects.CharacterHolder;
import engine.holders.objects.NpcHolder;
import engine.holders.objects.PlayerHolder;
import engine.util.Util;

/**
 * Class responsible for giving the character a "custom" effect by having all their set enchanted to xxx
 * @author fissban
 */
public class EnchantAbnormalEffectArmor extends AbstractMod
{
	/**
	 * Constructor
	 */
	public EnchantAbnormalEffectArmor()
	{
		registerMod(ConfigData.ENABLE_EnchantAbnormalEffectArmor);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onEnchant(PlayerHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public void onEquip(CharacterHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public void onUnequip(CharacterHolder ph)
	{
		checkSetEffect(ph);
	}
	
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		cancelTimer("customEffectSkill", null, ph);
		
		return super.onExitWorld(ph);
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		switch (timerName)
		{
			case "customEffectSkill":
			{
				if (ph != null)
				{
					ph.getInstance().broadcastPacket(new MagicSkillUser(ph.getInstance(), ph.getInstance(), 4326, 1, 1000, 1000));
				}
				break;
			}
		}
	}
	
	/** MISC --------------------------------------------------------------------------------------------- */
	
	private void checkSetEffect(CharacterHolder character)
	{
		if (!Util.areObjectType(L2PcInstance.class, character))
		{
			return;
		}
		
		PlayerHolder ph = (PlayerHolder) character;
		
		// We review the positions of the set of the character.
		if (checkItems(ph))
		{
			startTimer("customEffectSkill", 4000, null, ph, true);
		}
		else
		{
			// if the character has the effect would Cancelled
			cancelTimer("customEffectSkill", null, ph);
		}
	}
	
	/**
	 * It checks the character:<br>
	 * <li>Keep all equipment + ENCHANT_EFFECT_LVL except the coat and jewelry</li>
	 * <li>You have equipped a complete set according to "ArmorSetsTable"</li> <br>
	 * @param  ph
	 * @param  paperdoll
	 * @return
	 */
	private boolean checkItems(PlayerHolder ph)
	{
		PcInventory inv = ph.getInstance().getInventory();
		
		// Checks if player is wearing a chest item
		L2ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if (chestItem == null)
		{
			return false;
		}
		
		// checks if there is armorset for chest item that player worns
		L2ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getItemId());
		if (armorSet == null)
		{
			return false;
		}
		
		if (!armorSet.containAll(ph.getInstance()))
		{
			return false;
		}
		
		// check enchant lvl
		if (!checkEnchant(ph, Inventory.PAPERDOLL_CHEST, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, Inventory.PAPERDOLL_LEGS, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, Inventory.PAPERDOLL_HEAD, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, Inventory.PAPERDOLL_GLOVES, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, Inventory.PAPERDOLL_FEET, armorSet))
		{
			return false;
		}
		if (!checkEnchant(ph, Inventory.PAPERDOLL_HEAD, armorSet))
		{
			return false;
		}
		
		return true;
	}
	
	private static boolean checkEnchant(PlayerHolder ph, int type, L2ArmorSet armorSet)
	{
		L2ItemInstance item = ph.getInstance().getInventory().getPaperdollItem(type);
		
		if (item == null)
		{
			return true;
		}
		if (armorSet.containItem(type, item.getItemId()) && item.getEnchantLevel() >= ConfigData.ENCHANT_EFFECT_LVL)
		{
			return true;
		}
		
		return false;
	}
}
