package net.l2jpx.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.model.actor.instance.L2GrandBossInstance;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.model.actor.instance.L2RaidBossInstance;
import net.l2jpx.gameserver.network.SystemMessageId;
import net.l2jpx.gameserver.network.serverpackets.CreatureSay;
import net.l2jpx.gameserver.network.serverpackets.ExCloseMPCC;
import net.l2jpx.gameserver.network.serverpackets.ExOpenMPCC;
import net.l2jpx.gameserver.network.serverpackets.L2GameServerPacket;
import net.l2jpx.gameserver.network.serverpackets.SystemMessage;

/**
 * @author chris_00
 * @author ReynalDev
 */
public class L2CommandChannel
{
	private List<L2Party> partys;
	private L2PcInstance commandLeader = null;
	private int channelLvl;
	
	public L2CommandChannel(L2PcInstance leader)
	{
		commandLeader = leader;
		partys = new ArrayList<>();
		partys.add(leader.getParty());
		channelLvl = leader.getParty().getLevel();
		leader.getParty().setCommandChannel(this);
		leader.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessageId.COMMAND_CHANNEL_FORMED));
		leader.getParty().broadcastToPartyMembers(new ExOpenMPCC());
	}
	
	public void addParty(L2Party party)
	{
		if (party == null)
		{
			return;
		}
		
		partys.add(party);
		
		if (party.getLevel() > channelLvl)
		{
			channelLvl = party.getLevel();
		}
		
		party.setCommandChannel(this);
		party.broadcastToPartyMembers(new SystemMessage(SystemMessageId.JOINED_COMMAND_CHANNEL));
		broadcastToChannelMembers(new ExOpenMPCC());
	}
	
	public void removeParty(L2Party party)
	{
		if (party == null)
		{
			return;
		}
		
		partys.remove(party);
		channelLvl = 0;
		
		for (L2Party pty : partys)
		{
			if (pty.getLevel() > channelLvl)
			{
				channelLvl = pty.getLevel();
			}
		}
		
		party.setCommandChannel(null);
		party.broadcastToPartyMembers(new ExCloseMPCC());
		
		if (partys.size() < 2)
		{
			broadcastToChannelMembers(new SystemMessage(SystemMessageId.COMMAND_CHANNEL_DISBANDED));
			disbandChannel();
		}
	}
	
	public void disbandChannel()
	{
		if (partys != null)
		{
			for (L2Party party : partys)
			{
				if (party != null)
				{
					removeParty(party);
				}
			}
			partys.clear();
		}
	}
	
	public int getMemberCount()
	{
		int count = 0;
		
		for (L2Party party : partys)
		{
			if (party != null)
			{
				count += party.getMemberCount();
			}
		}
		return count;
	}
	
	public void broadcastToChannelMembers(L2GameServerPacket gsp)
	{
		if (partys != null && !partys.isEmpty())
		{
			for (L2Party party : partys)
			{
				if (party != null)
				{
					party.broadcastToPartyMembers(gsp);
				}
			}
		}
	}
	
	public void broadcastCSToChannelMembers(CreatureSay gsp, L2PcInstance broadcaster)
	{
		if (partys != null && !partys.isEmpty())
		{
			for (L2Party party : partys)
			{
				if (party != null)
				{
					party.broadcastCSToPartyMembers(gsp, broadcaster);
				}
			}
		}
	}
	
	public List<L2Party> getPartys()
	{
		return partys;
	}
	
	public List<L2PcInstance> getMembers()
	{
		List<L2PcInstance> members = new ArrayList<>();
		
		for (L2Party party : getPartys())
		{
			members.addAll(party.getPartyMembers());
		}
		
		return members;
	}
	
	public int getLevel()
	{
		return channelLvl;
	}
	
	public void setChannelLeader(L2PcInstance leader)
	{
		commandLeader = leader;
	}
	
	public L2PcInstance getChannelLeader()
	{
		return commandLeader;
	}
	
	/**
	 * Queen Ant, Core, Orfen, Zaken: MemberCount > 36<br>
	 * Baium: MemberCount > 56<br>
	 * Antharas: MemberCount > 225<br>
	 * Valakas: MemberCount > 99<br>
	 * normal RaidBoss: MemberCount > 18
	 * @param  obj
	 * @return     true if proper condition for RaidWar
	 */
	public boolean meetRaidWarCondition(L2Object obj)
	{
		if (!(obj instanceof L2RaidBossInstance) || !(obj instanceof L2GrandBossInstance))
		{
			return false;
		}
		
		int npcId = ((L2Attackable) obj).getNpcId();
		
		switch (npcId)
		{
			case 29001: // Queen Ant
			case 29006: // Core
			case 29014: // Orfen
			case 29022: // Zaken
				return getMemberCount() > 36;
			case 29020: // Baium
				return getMemberCount() > 56;
			case 29019: // Antharas
				return getMemberCount() > 225;
			case 29028: // Valakas
				return getMemberCount() > 99;
			default: // normal Raidboss
				return getMemberCount() > 18;
		}
	}
}
