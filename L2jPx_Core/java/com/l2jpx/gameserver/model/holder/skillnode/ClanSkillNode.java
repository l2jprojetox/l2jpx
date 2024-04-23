package com.l2jpx.gameserver.model.holder.skillnode;

import com.l2jpx.commons.data.StatSet;

/**
 * A datatype used by clan skill types. It extends {@link GeneralSkillNode}.
 */
public final class ClanSkillNode extends GeneralSkillNode
{
	private final int _itemId;
	
	public ClanSkillNode(StatSet set)
	{
		super(set);
		
		_itemId = set.getInteger("itemId");
	}
	
	public int getItemId()
	{
		return _itemId;
	}
}