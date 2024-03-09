package net.l2jpx.gameserver.datatables;

import java.util.Arrays;
import java.util.List;

import net.l2jpx.gameserver.model.entity.siege.Castle;

/**
 * This class has just one simple function to return the item id of a crown regarding to castleid
 * @author evill33t
 * @author ReynalDev
 */
public class CastleCircletTable
{
	public static final int THE_LORDS_CROWN = 6841;
	public static final int CIRCLET_OF_GLUDIO = 6838;
	public static final int CIRCLET_OF_DION = 6835;
	public static final int CIRCLET_OF_GIRAN = 6839;
	public static final int CIRCLET_OF_OREN = 6837;
	public static final int CIRCLET_OF_ADEN = 6840;
	public static final int CIRCLET_OF_INNADRIL = 6834;
	public static final int CIRCLET_OF_GODDARD = 6836;
	public static final int CIRCLET_OF_RUNE = 8182;
	public static final int CIRCLET_SCHUTTGART = 8183;
	
	private static final List<Integer> CROWNLIST = Arrays.asList(
		THE_LORDS_CROWN,
		CIRCLET_OF_GLUDIO,
		CIRCLET_OF_DION,
		CIRCLET_OF_GIRAN,
		CIRCLET_OF_OREN,
		CIRCLET_OF_ADEN,
		CIRCLET_OF_INNADRIL,
		CIRCLET_OF_GODDARD,
		CIRCLET_OF_RUNE,
		CIRCLET_SCHUTTGART
	);
	
	public static List<Integer> getCrownList()
	{
		return CROWNLIST;
	}
	
	public static int getCircletByCastleId(int castleId)
	{
		int circletId = 0;
		switch (castleId)
		{
			case Castle.GLUDIO:
				circletId = 6838;
				break;
			case Castle.DION:
				circletId = 6835;
				break;
			case Castle.GIRAN:
				circletId = 6839;
				break;
			case Castle.OREN:
				circletId = 6837;
				break;
			case Castle.ADEN:
				circletId = 6840;
				break;
			case Castle.INNADRIL:
				circletId = 6834;
				break;
			case Castle.GODDARD:
				circletId = 6836;
				break;
			case Castle.RUNE:
				circletId = 8182;
				break;
			case Castle.SCHUTTGART:
				circletId = 8183;
				break;
			default:
				circletId = 0;
		}
		return circletId;
	}
}
