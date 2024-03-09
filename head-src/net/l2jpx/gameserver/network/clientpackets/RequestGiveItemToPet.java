package net.l2jpx.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import net.l2jpx.Config;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;
import net.l2jpx.gameserver.util.IllegalPlayerAction;
import net.l2jpx.gameserver.util.Util;

public final class RequestGiveItemToPet extends L2GameClientPacket
{
	private static Logger LOGGER = Logger.getLogger(RequestGetItemFromPet.class);
	
	private int objectId;
	private int amount;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		amount = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null || !(player.getPet() instanceof L2PetInstance))
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("giveitemtopet"))
		{
			player.sendMessage("You give items to pet too fast.");
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && player.getKarma() > 0)
		{
			return;
		}
		
		if (player.getPrivateStoreType() != 0)
		{
			player.sendMessage("Cannot exchange items while trading");
			return;
		}
		
		if (player.isCastingNow() || player.isCastingPotionNow())
		{
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " Tried To Use Enchant Exploit And Got Banned!", IllegalPlayerAction.PUNISH_KICKBAN);
			return;
		}
		
		final L2ItemInstance item = player.getInventory().getItemByObjectId(objectId);
		
		if (item == null)
		{
			return;
		}
		
		if (item.isAugmented())
		{
			return;
		}
		
		if (!item.isDropable() || !item.isDestroyable() || !item.isTradeable())
		{
			sendPacket(new SystemMessage(SystemMessageId.YOU_PET_CANNOT_CARRY_THIS_ITEM));
			return;
		}
		
		final L2PetInstance pet = (L2PetInstance) player.getPet();
		
		if (pet.isDead())
		{
			sendPacket(new SystemMessage(SystemMessageId.YOUR_PET_IS_MOTIONLESS_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED));
			return;
		}
		
		if (amount < 0)
		{
			return;
		}
		
		if (player.transferItem("Transfer", objectId, amount, pet.getInventory(), pet) == null)
		{
			LOGGER.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 8B RequestGiveItemToPet";
	}
}
