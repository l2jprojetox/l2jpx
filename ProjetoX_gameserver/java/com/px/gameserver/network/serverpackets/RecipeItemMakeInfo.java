package com.px.gameserver.network.serverpackets;

import com.px.gameserver.data.xml.RecipeData;
import com.px.gameserver.model.actor.Player;
import com.px.gameserver.model.item.Recipe;

public class RecipeItemMakeInfo extends L2GameServerPacket
{
	private final int _id;
	private final Player _activeChar;
	private final int _status;
	
	public RecipeItemMakeInfo(int id, Player player, int status)
	{
		_id = id;
		_activeChar = player;
		_status = status;
	}
	
	public RecipeItemMakeInfo(int id, Player player)
	{
		_id = id;
		_activeChar = player;
		_status = -1;
	}
	
	@Override
	protected final void writeImpl()
	{
		Recipe recipe = RecipeData.getInstance().getRecipeList(_id);
		if (recipe != null)
		{
			writeC(0xD7);
			
			writeD(_id);
			writeD((recipe.isDwarven()) ? 0 : 1);
			writeD((int) _activeChar.getCurrentMp());
			writeD(_activeChar.getMaxMp());
			writeD(_status);
		}
	}
}