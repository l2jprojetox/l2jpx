package com.px.gameserver.model.memo;

import java.util.Map;

import com.px.commons.data.MemoSet;

import com.px.gameserver.model.World;
import com.px.gameserver.model.WorldObject;
import com.px.gameserver.model.actor.Creature;
import com.px.gameserver.model.actor.Npc;

/**
 * An implementation of {@link MemoSet} used for Npc.
 */
public class NpcMemo extends MemoSet
{
	public static final NpcMemo DUMMY_SET = new NpcMemo();
	
	private static final long serialVersionUID = 1L;
	
	public NpcMemo()
	{
		super();
	}
	
	public NpcMemo(final int size)
	{
		super(size);
	}
	
	public NpcMemo(final Map<String, String> m)
	{
		super(m);
	}
	
	@Override
	protected void onSet(String key, String value)
	{
	}
	
	@Override
	protected void onUnset(String key)
	{
	}
	
	/**
	 * @param str : The {@link String} used as parameter.
	 * @return The {@link Creature} linked to the objectId passed as a {@link String} parameter, or null if not found.
	 */
	public final Creature getCreature(String str)
	{
		final Integer id = getInteger(str, 0);
		if (id == 0)
			return null;
		
		final WorldObject wo = World.getInstance().getObject(id);
		if (wo == null)
			return null;
		
		if (wo instanceof Npc)
			if (((Npc) wo).isDecayed())
				return null;
			
		return (Creature) wo;
	}
}