package net.l2jpx.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import net.l2jpx.gameserver.model.L2Skill;

/**
 * sample 0000: 6d 0c 00 00 00 00 00 00 00 03 00 00 00 f3 03 00 m............... 0010: 00 00 00 00 00 01 00 00 00 f4 03 00 00 00 00 00 ................ 0020: 00 01 00 00 00 10 04 00 00 00 00 00 00 01 00 00 ................ 0030: 00 2c 04 00 00 00 00 00 00 03 00 00 00 99 04 00 .,.............. 0040:
 * 00 00 00 00 00 02 00 00 00 a0 04 00 00 00 00 00 ................ 0050: 00 01 00 00 00 c0 04 00 00 01 00 00 00 01 00 00 ................ 0060: 00 76 00 00 00 01 00 00 00 01 00 00 00 a3 00 00 .v.............. 0070: 00 01 00 00 00 01 00 00 00 c2 00 00 00 01 00 00 ................ 0080: 00 01 00 00
 * 00 d6 00 00 00 01 00 00 00 01 00 00 ................ 0090: 00 f4 00 00 00 format d (ddd)
 * @author ReynalDev
 */
public class SkillList extends L2GameServerPacket
{
	private List<L2Skill> skills = new ArrayList<>();
	
	public void addSkill(L2Skill skill)
	{
		skills.add(skill);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x58);
		writeD(skills.size());
		
		for (L2Skill skill : skills)
		{
			writeD( (skill.isActive() || skill.isToggle()) ? 0 : 1);
			writeD(skill.getLevel());
			writeD(skill.getId());
			writeC(0); // disabled
		}
	}
	
	@Override
	public String getType()
	{
		return "[S] 58 SkillList";
	}
}
