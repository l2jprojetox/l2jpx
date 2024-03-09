package net.l2jpx.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import net.l2jpx.Config;
import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.model.L2Summon;
import net.l2jpx.gameserver.network.serverpackets.CreatureSay;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.gameserver.thread.ThreadPoolManager;

/**
 * @author Ederik
 */
public class L2ProtectorInstance extends L2NpcInstance
{
	private ScheduledFuture<?> aiTask;
	
	private class ProtectorAI implements Runnable
	{
		private L2ProtectorInstance caster;
		
		protected ProtectorAI(L2ProtectorInstance caster)
		{
			this.caster = caster;
		}
		
		@Override
		public void run()
		{
			/**
			 * For each known player in range, cast sleep if pvpFlag != 0 or Karma >0 Skill use is just for buff animation
			 */
			for (L2PcInstance player : getKnownList().getKnownPlayers().values())
			{
				if (player.getKarma() > 0 && Config.PROTECTOR_PLAYER_PK || player.getPvpFlag() != 0 && Config.PROTECTOR_PLAYER_PVP)
				{
					handleCast(player, Config.PROTECTOR_SKILLID, Config.PROTECTOR_SKILLLEVEL);
				}
				 
				L2Summon activePet = player.getPet();
				
				if (activePet == null)
				{
					continue;
				}
				
				if (activePet.getKarma() > 0 && Config.PROTECTOR_PLAYER_PK || activePet.getPvpFlag() != 0 && Config.PROTECTOR_PLAYER_PVP)
				{
					handleCastonPet(activePet, Config.PROTECTOR_SKILLID, Config.PROTECTOR_SKILLLEVEL);
				}
			}
		}
		
		// Cast for Player
		private boolean handleCast(L2PcInstance player, int skillId, int skillLevel)
		{
			if (player.isGM() || player.isDead() || !player.isVisible() || !isInsideRadius(player, Config.PROTECTOR_RADIUS_ACTION, false, false))
			{
				return false;
			}
			
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			
			if (player.getFirstEffect(skill) == null)
			{
				int objId = caster.getObjectId();
				skill.getEffects(caster, player, false, false, false);
				broadcastPacket(new MagicSkillUser(caster, player, skillId, skillLevel, Config.PROTECTOR_SKILLTIME, 0));
				broadcastPacket(new CreatureSay(objId, 0, String.valueOf(getName()), Config.PROTECTOR_MESSAGE));
				
				skill = null;
				return true;
			}
			
			return false;
		}
		
		// Cast for pet
		private boolean handleCastonPet(L2Summon summon, int skillId, int skillLevel)
		{
			if (summon.isDead() || !summon.isVisible() || !isInsideRadius(summon, Config.PROTECTOR_RADIUS_ACTION, false, false))
			{
				return false;
			}
			
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			if (summon.getFirstEffect(skill) == null)
			{
				int objId = caster.getObjectId();
				skill.getEffects(caster, summon, false, false, false);
				broadcastPacket(new MagicSkillUser(caster, summon, skillId, skillLevel, Config.PROTECTOR_SKILLTIME, 0));
				broadcastPacket(new CreatureSay(objId, 0, String.valueOf(getName()), Config.PROTECTOR_MESSAGE));
				return true;
			}
			return false;
		}
	}
	
	public L2ProtectorInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		
		if (aiTask != null)
		{
			aiTask.cancel(true);
		}
		
		aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new ProtectorAI(this), 500, 500);
	}
	
	@Override
	public void deleteMe()
	{
		if (aiTask != null)
		{
			aiTask.cancel(true);
			aiTask = null;
		}
		
		super.deleteMe();
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
}
