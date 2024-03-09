package net.l2jpx.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Character;
import net.l2jpx.gameserver.model.L2Object;
import net.l2jpx.gameserver.model.L2Skill;
import net.l2jpx.gameserver.network.serverpackets.MagicSkillUser;
import net.l2jpx.gameserver.templates.L2NpcTemplate;
import net.l2jpx.util.random.Rnd;

/**
 * @author Drunkard Zabb0x Lets drink2code!
 */
public class L2XmasTreeInstance extends L2NpcInstance
{
	private final ScheduledFuture<?> aiTask;
	
	class XmassAI implements Runnable
	{
		private final L2XmasTreeInstance caster;
		
		protected XmassAI(final L2XmasTreeInstance caster)
		{
			this.caster = caster;
		}
		
		@Override
		public void run()
		{
			for (final L2PcInstance player : getKnownList().getKnownPlayers().values())
			{
				final int i = Rnd.nextInt(3);
				handleCast(player, (4262 + i));
			}
		}
		
		private boolean handleCast(final L2PcInstance player, final int skillId)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
			
			if (player.getFirstEffect(skill) == null)
			{
				setTarget(player);
				doCast(skill);
				
				MagicSkillUser msu = new MagicSkillUser(caster, player, skill.getId(), 1, skill.getHitTime(), 0);
				broadcastPacket(msu);
				return true;
			}
			return false;
		}
	}
	
	public L2XmasTreeInstance(final int objectId, final L2NpcTemplate template)
	{
		super(objectId, template);
		// aiTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new XmassAI(this), 3000, 3000); // This AI is for given buff
		aiTask = null;
	}
	
	@Override
	public void deleteMe()
	{
		if (aiTask != null)
		{
			aiTask.cancel(true);
		}
		
		super.deleteMe();
	}
	
	@Override
	public int getDistanceToWatchObject(final L2Object object)
	{
		return 900;
	}
	
	@Override
	public boolean isAutoAttackable(final L2Character attacker)
	{
		return false;
	}
	
}
