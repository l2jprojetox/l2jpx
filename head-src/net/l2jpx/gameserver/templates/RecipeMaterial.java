package net.l2jpx.gameserver.templates;

/**
 * @author ReynalDev
 */
public class RecipeMaterial
{
	private int materialItemId;
	private int materialItemCount;
	
	/**
	 * Constructor of L2RecipeInstance (create a new line in a RecipeList).<BR>
	 * <BR>
	 * @param itemId   the item id
	 * @param quantity the quantity
	 */
	public RecipeMaterial(int itemId, int quantity)
	{
		this.materialItemId = itemId;
		this.materialItemCount = quantity;
	}
	
	/**
	 * Return the Identifier of the L2RecipeInstance Item needed.<BR>
	 * <BR>
	 * @return the item id
	 */
	public int getItemId()
	{
		return materialItemId;
	}
	
	/**
	 * Return the Item quantity needed of the L2RecipeInstance.<BR>
	 * <BR>
	 * @return the quantity
	 */
	public int getQuantity()
	{
		return materialItemCount;
	}
}
