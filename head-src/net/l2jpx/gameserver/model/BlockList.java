package net.l2jpx.gameserver.model;

import java.util.HashSet;
import java.util.Set;

import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author luisantonioa
 */
public class BlockList
{
	private Set<String> blockSet;
	private boolean blockAll;
	private L2PcInstance owner;
	
	public BlockList(L2PcInstance owner)
	{
		this.owner = owner;
		blockSet = new HashSet<>();
		blockAll = false;
	}
	
	public void addToBlockList(String character)
	{
		if (character != null)
		{
			blockSet.add(character);
			
			SystemMessage sm = null;
			
			L2PcInstance target = L2World.getInstance().getPlayerByName(character);
			if (target != null)
			{
				sm = new SystemMessage(SystemMessageId.S1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
				sm.addString(owner.getName());
				target.sendPacket(sm);
			}
			
			sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST);
			sm.addString(character);
			owner.sendPacket(sm);
		}
	}
	
	public void removeFromBlockList(String character)
	{
		if (character != null)
		{
			blockSet.remove(character);
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST);
			sm.addString(character);
			owner.sendPacket(sm);
		}
	}
	
	public boolean isInBlockList(String character)
	{
		return blockSet.contains(character);
	}
	
	public boolean isBlockAll()
	{
		return blockAll;
	}
	
	public void setBlockAll(boolean state)
	{
		blockAll = state;
	}
	
	public Set<String> getBlockList()
	{
		return blockSet;
	}
	
}
