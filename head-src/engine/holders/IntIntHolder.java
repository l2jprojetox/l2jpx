package engine.holders;

import net.l2jpx.gameserver.datatables.SkillTable;
import net.l2jpx.gameserver.model.L2Skill;

/**
 * A generic int/int container.
 */
public class IntIntHolder
{
	private int id;
	private int value;
	
	public IntIntHolder(int id, int value)
	{
		this.id = id;
		this.value = value;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	/**
	 * @return the Skill associated to the id/value.
	 */
	public final L2Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(id, value);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": Id: " + id + ", Value: " + value;
	}
}
