package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.datatables.sql.PetNameTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Summon;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.InventoryUpdate;
import net.l2jpx.gameserver.network.serverpackets.NpcInfo;
import net.l2jpx.gameserver.network.serverpackets.PetInfo;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

public final class RequestChangePetName extends L2GameClientPacket
{
	private String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2Character activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2Summon pet = activeChar.getPet();
		if (pet == null)
		{
			return;
		}
		
		if (pet.getName() != null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_SET_THE_NAME_OF_THE_PET));
			return;
		}
		else if (PetNameTable.getInstance().doesPetNameExist(name, pet.getTemplate().npcId))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET));
			return;
		}
		else if (name.length() < 3 || name.length() > 16)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			sm.addString("Your pet's name can be up to 16 characters.");
			// SystemMessage sm = new SystemMessage(SystemMessage.NAMING_PETNAME_UP_TO_8CHARS);
			activeChar.sendPacket(sm);
			sm = null;
			
			return;
		}
		else if (!PetNameTable.getInstance().isValidPetName(name))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PETS_NAME));
			return;
		}
		
		pet.setName(name);
		pet.broadcastPacket(new NpcInfo(pet, activeChar));
		activeChar.sendPacket(new PetInfo(pet));
		// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
		pet.updateEffectIcons(true);
		
		// set the flag on the control item to say that the pet has a name
		if (pet instanceof L2PetInstance)
		{
			final L2ItemInstance controlItem = pet.getOwner().getInventory().getItemByObjectId(pet.getControlItemId());
			
			if (controlItem != null)
			{
				controlItem.setCustomType2(1);
				controlItem.updateDatabase();
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(controlItem);
				activeChar.sendPacket(iu);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] 89 RequestChangePetName";
	}
}
