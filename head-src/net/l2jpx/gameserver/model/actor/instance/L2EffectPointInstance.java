package net.l2jpx.gameserver.model.actor.instance;

import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.templates.L2NpcTemplate;

public class L2EffectPointInstance extends L2NpcInstance
{
	private final L2Character owner;
	
	public L2EffectPointInstance(final int objectId, final L2NpcTemplate template, final L2Character owner)
	{
		super(objectId, template);
		this.owner = owner;
	}
	
	public L2Character getOwner()
	{
		return owner;
	}
	
	/**
	 * this is called when a player interacts with this NPC
	 * @param player
	 */
	@Override
	public void onAction(final L2PcInstance player)
	{
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onActionShift(final L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
