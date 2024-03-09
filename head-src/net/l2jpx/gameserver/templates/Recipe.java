package net.l2jpx.gameserver.templates;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ReynalDev
 */
public class Recipe
{
	private List<RecipeMaterial> materials;
	private int id;
	private int level;
	private int recipeItemId;
	private String recipeName;
	private int successRate;
	private int mpCost;
	private int producItemId;
	private int producItemCount;
	private boolean isDwarvenRecipe;
	
	public Recipe(int id, int level, int recipeId, String recipeName, int successRate, int mpCost, int productItemId, int productItemCount, boolean isDwarvenRecipe)
	{
		this.id = id;
		materials = new ArrayList<>();
		this.level = level;
		this.recipeItemId = recipeId;
		this.recipeName = recipeName;
		this.successRate = successRate;
		this.mpCost = mpCost;
		this.producItemId = productItemId;
		this.producItemCount = productItemCount;
		this.isDwarvenRecipe = isDwarvenRecipe;
	}
	
	public void addRecipe(RecipeMaterial recipe)
	{
		materials.add(recipe);
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getRecipeId()
	{
		return recipeItemId;
	}
	
	public String getRecipeName()
	{
		return recipeName;
	}
	
	public int getSuccessRate()
	{
		return successRate;
	}
	
	public int getMpCost()
	{
		return mpCost;
	}
	
	public boolean isConsumable()
	{
		return producItemId >= 1463 && producItemId <= 1467 || producItemId >= 2509 && producItemId <= 2514 || producItemId >= 3947 && producItemId <= 3952 || producItemId >= 1341 && producItemId <= 1345;
	}
	
	public int getProductItemId()
	{
		return producItemId;
	}
	
	public int getProductItemCount()
	{
		return producItemCount;
	}
	
	public boolean isDwarvenRecipe()
	{
		return isDwarvenRecipe;
	}
	
	public List<RecipeMaterial> getMaterials()
	{
		return materials;
	}
	
	public void setProdcutItemId(int itemId)
	{
		producItemId = itemId;
	}
	
	public void setProductItemCount(int count)
	{
		producItemCount = count;
	}
}
