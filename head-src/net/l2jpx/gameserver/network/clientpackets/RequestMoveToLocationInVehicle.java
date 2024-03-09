package net.l2jpx.gameserver.network.clientpackets;

import net.l2jpx.gameserver.ai.CtrlIntention;
import net.l2jpx.gameserver.managers.BoatManager;
import net.l2jpx.gameserver.model.actor.instance.L2BoatInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.position.L2CharPosition;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.templates.L2WeaponType;
import net.l2jpx.gameserver.thread.TaskPriority;
import net.l2jpx.util.Point3D;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private final Point3D pos = new Point3D(0, 0, 0);
	private final Point3D origin_pos = new Point3D(0, 0, 0);
	private int boatId;
	
	public TaskPriority getPriority()
	{
		return TaskPriority.PR_HIGH;
	}
	
	@Override
	protected void readImpl()
	{
		int x, y, z;
		boatId = readD(); // objectId of boat
		x = readD();
		y = readD();
		z = readD();
		pos.setXYZ(x, y, z);
		x = readD();
		y = readD();
		z = readD();
		origin_pos.setXYZ(x, y, z);
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		else if (activeChar.isAttackingNow() && activeChar.getActiveWeaponItem() != null && activeChar.getActiveWeaponItem().getItemType() == L2WeaponType.BOW)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			final L2BoatInstance boat = BoatManager.getInstance().GetBoat(boatId);
			if (boat == null)
			{
				return;
			}
			activeChar.setBoat(boat);
			activeChar.setInBoat(true);
			activeChar.setInBoatPosition(pos);
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO_IN_A_BOAT, new L2CharPosition(pos.getX(), pos.getY(), pos.getZ(), 0), new L2CharPosition(origin_pos.getX(), origin_pos.getY(), origin_pos.getZ(), 0));
		}
		
	}
	
	@Override
	public String getType()
	{
		return "[] RequestMoveToLocationInVehicle";
	}
}
