package com.l2jpx.gameserver.network.clientpackets;

import com.l2jpx.Config;
import com.l2jpx.gameserver.data.SkillTable;
import com.l2jpx.gameserver.data.xml.SkillTreeData;
import com.l2jpx.gameserver.model.actor.Player;
import com.l2jpx.gameserver.model.actor.instance.Folk;
import com.l2jpx.gameserver.model.holder.skillnode.EnchantSkillNode;
import com.l2jpx.gameserver.network.serverpackets.ExEnchantSkillInfo;
import com.l2jpx.gameserver.skills.L2Skill;

public final class RequestExEnchantSkillInfo extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLevel;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLevel = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_skillId <= 0 || _skillLevel <= 0)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.getClassId().getLevel() < 3 || player.getStatus().getLevel() < 76)
			return;
		
		final Folk folk = player.getCurrentFolk();
		if (folk == null || !player.getAI().canDoInteract(folk))
			return;
		
		if (player.getSkillLevel(_skillId) >= _skillLevel)
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
		if (skill == null)
			return;
		
		if (!folk.getTemplate().canTeach(player.getClassId()))
			return;
		
		final EnchantSkillNode esn = SkillTreeData.getInstance().getEnchantSkillFor(player, _skillId, _skillLevel);
		if (esn == null)
			return;
		
		final ExEnchantSkillInfo esi = new ExEnchantSkillInfo(_skillId, _skillLevel, esn.getSp(), esn.getExp(), esn.getEnchantRate(player.getStatus().getLevel()));
		if (Config.ES_SP_BOOK_NEEDED && esn.getItem() != null)
			esi.addRequirement(4, esn.getItem().getId(), esn.getItem().getValue(), 0);
		
		sendPacket(esi);
	}
}