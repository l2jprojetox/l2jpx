package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Summon;
import net.l2jpx.gameserver.model.actor.instance.L2BoatInstance;
import net.l2jpx.gameserver.model.actor.instance.L2DoorInstance;
import net.l2jpx.gameserver.model.actor.instance.L2ItemInstance;
import net.l2jpx.gameserver.model.actor.instance.L2NpcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PetInstance;
import net.l2jpx.gameserver.model.actor.instance.L2StaticObjectInstance;
import net.l2jpx.gameserver.network.serverpackets.CharInfo;
import net.l2jpx.gameserver.network.serverpackets.DoorInfo;
import net.l2jpx.gameserver.network.serverpackets.DoorStatusUpdate;
import net.l2jpx.gameserver.network.serverpackets.GetOnVehicle;
import net.l2jpx.gameserver.network.serverpackets.NpcInfo;
import net.l2jpx.gameserver.network.serverpackets.PetInfo;
import net.l2jpx.gameserver.network.serverpackets.PetItemList;
import net.l2jpx.gameserver.network.serverpackets.RelationChanged;
import net.l2jpx.gameserver.network.serverpackets.SpawnItem;
import net.l2jpx.gameserver.network.serverpackets.SpawnItemPoly;
import net.l2jpx.gameserver.network.serverpackets.StaticObject;
import net.l2jpx.gameserver.network.serverpackets.UserInfo;
import net.l2jpx.gameserver.network.serverpackets.VehicleInfo;
import net.l2jpx.gameserver.thread.TaskPriority;

public class RequestRecordInfo extends L2GameClientPacket
{
	/**
	 * urgent messages, execute immediately
	 * @return
	 */
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_NORMAL;
	}
	
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.getKnownList().updateKnownObjects();
		activeChar.sendPacket(new UserInfo(activeChar));
		
		for (final L2Object object : activeChar.getKnownList().getKnownObjects().values())
		{
			if (object == null)
			{
				continue;
			}
			
			if (object.getPoly().isMorphed() && object.getPoly().getPolyType().equals("item"))
			{
				activeChar.sendPacket(new SpawnItemPoly(object));
			}
			else
			{
				if (object instanceof L2ItemInstance)
				{
					activeChar.sendPacket(new SpawnItem((L2ItemInstance) object));
				}
				else if (object instanceof L2DoorInstance)
				{
					L2DoorInstance door = (L2DoorInstance) object;
					
					activeChar.sendPacket(new DoorInfo(door));
					activeChar.sendPacket(new DoorStatusUpdate(door));
				}
				else if (object instanceof L2BoatInstance)
				{
					if (!activeChar.isInBoat() && object != activeChar.getBoat())
					{
						activeChar.sendPacket(new VehicleInfo((L2BoatInstance) object));
						((L2BoatInstance) object).sendVehicleDeparture(activeChar);
					}
				}
				else if (object instanceof L2StaticObjectInstance)
				{
					activeChar.sendPacket(new StaticObject((L2StaticObjectInstance) object));
				}
				else if (object instanceof L2NpcInstance)
				{
					activeChar.sendPacket(new NpcInfo((L2NpcInstance) object, activeChar));
				}
				else if (object instanceof L2Summon)
				{
					final L2Summon summon = (L2Summon) object;
					
					// Check if the L2PcInstance is the owner of the Pet
					if (activeChar.equals(summon.getOwner()))
					{
						activeChar.sendPacket(new PetInfo(summon));
						
						if (summon instanceof L2PetInstance)
						{
							activeChar.sendPacket(new PetItemList((L2PetInstance) summon));
						}
					}
					else
					{
						activeChar.sendPacket(new NpcInfo(summon, activeChar));
					}
					
					// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
					summon.updateEffectIcons(true);
				}
				else if (object instanceof L2PcInstance)
				{
					final L2PcInstance otherPlayer = (L2PcInstance) object;
					
					if (otherPlayer.isInBoat())
					{
						otherPlayer.getPosition().setWorldPosition(otherPlayer.getBoat().getPosition().getWorldPosition());
						activeChar.sendPacket(new CharInfo(otherPlayer));
						final int relation = otherPlayer.getRelation(activeChar);
						
						if (otherPlayer.getKnownList().getKnownRelations().get(activeChar.getObjectId()) != null && otherPlayer.getKnownList().getKnownRelations().get(activeChar.getObjectId()) != relation)
						{
							activeChar.sendPacket(new RelationChanged(otherPlayer, relation, activeChar.isAutoAttackable(otherPlayer)));
						}
						
						activeChar.sendPacket(new GetOnVehicle(otherPlayer, otherPlayer.getBoat(), otherPlayer.getInBoatPosition().getX(), otherPlayer.getInBoatPosition().getY(), otherPlayer.getInBoatPosition().getZ()));
					}
					else
					{
						activeChar.sendPacket(new CharInfo(otherPlayer));
						final int relation = otherPlayer.getRelation(activeChar);
						
						if (otherPlayer.getKnownList().getKnownRelations().get(activeChar.getObjectId()) != null && otherPlayer.getKnownList().getKnownRelations().get(activeChar.getObjectId()) != relation)
						{
							activeChar.sendPacket(new RelationChanged(otherPlayer, relation, activeChar.isAutoAttackable(otherPlayer)));
						}
					}
				}
				
				if (object instanceof L2Character)
				{
					// Update the state of the L2Character object client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance
					final L2Character obj = (L2Character) object;
					obj.getAI().describeStateToPlayer(activeChar);
				}
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "[0] CF RequestRecordInfo";
	}
}
