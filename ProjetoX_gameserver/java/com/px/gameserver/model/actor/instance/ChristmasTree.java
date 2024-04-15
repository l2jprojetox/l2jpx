package com.px.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import com.px.commons.concurrent.ThreadPool;

import com.px.gameserver.data.SkillTable.FrequentSkill;
import com.px.gameserver.enums.ZoneId;
import com.px.gameserver.model.L2Skill;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.actor.template.NpcTemplate;
import com.px.gameserver.network.serverpackets.ActionFailed;

/**
 * Christmas trees used on events.<br>
 * The special tree (npcId 13007) emits a regen aura, but only when set outside a peace zone.
 */
public class ChristmasTree extends Folk
{
	public static final int SPECIAL_TREE_ID = 13007;
	
	private ScheduledFuture<?> _aiTask;
	
	public ChristmasTree(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		if (template.getNpcId() == SPECIAL_TREE_ID && !isInsideZone(ZoneId.TOWN))
		{
			final L2Skill recoveryAura = FrequentSkill.SPECIAL_TREE_RECOVERY_BONUS.getSkill();
			if (recoveryAura == null)
				return;
			
			_aiTask = ThreadPool.scheduleAtFixedRate(() ->
			{
				for (Player player : getKnownTypeInRadius(Player.class, 200))
				{
					if (player.getFirstEffect(recoveryAura) == null)
						recoveryAura.getEffects(player, player);
				}
			}, 3000, 3000);
		}
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		super.deleteMe();
	}
	
	@Override
	public void onAction(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}