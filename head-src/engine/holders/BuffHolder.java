package engine.holders;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Skill;

import engine.enums.BuffType;

/**
 * Clase usada en NpcBufferScheme
 * @author fissban
 */
public class BuffHolder
{
	private final int id;
	private final int level;
	private BuffType type = BuffType.NONE;
	
	public BuffHolder(int id, int level)
	{
		this.id = id;
		this.level = level;
	}
	
	public BuffHolder(BuffType type, int id, int level)
	{
		this.type = type;
		this.id = id;
		this.level = level;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public BuffType getType()
	{
		return type;
	}
	
	/**
	 * @return the Skill associated to the id/value.
	 */
	public final L2Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(id, level);
	}
}
