package net.l2jpx.gameserver.datatables.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.l2jpx.gameserver.controllers.RecipeController;
import net.l2jpx.gameserver.templates.Recipe;
import net.l2jpx.gameserver.templates.RecipeMaterial;

/**
 * @author programmos
 * @author ReynalDev
 */
public class RecipeTable extends RecipeController
{
	private static final Logger LOGGER = Logger.getLogger(RecipeTable.class);
	private static RecipeTable instance;
	private Map<Integer, Recipe> recipes = new HashMap<>();
	
	public static RecipeTable getInstance()
	{
		if (instance == null)
		{
			instance = new RecipeTable();
		}
		
		return instance;
	}
	
	private RecipeTable()
	{
		String filePath = "data/xml/recipes.xml";
		try
		{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			
			NodeList hennaList = doc.getElementsByTagName("recipe");
			
			for (int i = 0; i < hennaList.getLength(); i++)
			{	
				Node recipeNode = hennaList.item(i);
				NamedNodeMap recipeNodeAttributes = recipeNode.getAttributes();
				
				int recipeId = Integer.parseInt(recipeNodeAttributes.getNamedItem("id").getNodeValue());
				String recipeName = recipeNodeAttributes.getNamedItem("name").getNodeValue();
				int recipeLevel = Integer.parseInt(recipeNodeAttributes.getNamedItem("level").getNodeValue());
				int recipeMpConsume = Integer.parseInt(recipeNodeAttributes.getNamedItem("mp_consume").getNodeValue());
				int recipeSuccessRate = Integer.parseInt(recipeNodeAttributes.getNamedItem("success_rate").getNodeValue());
				int recipeItemId = Integer.parseInt(recipeNodeAttributes.getNamedItem("item_id").getNodeValue());
				boolean isCommonRecipe = Boolean.parseBoolean(recipeNodeAttributes.getNamedItem("is_common").getNodeValue());
				boolean isDwarvenRecipe = !isCommonRecipe;
				
				Recipe recipe = new Recipe(recipeId, recipeLevel, recipeItemId, recipeName, recipeSuccessRate, recipeMpConsume, 0, 0, isDwarvenRecipe);
				
				for (Node recipeInfo = recipeNode.getFirstChild(); recipeInfo != null; recipeInfo = recipeInfo.getNextSibling())
				{
					switch (recipeInfo.getNodeName())
					{
						case "materials":
							for (Node recipeMaterials = recipeInfo.getFirstChild(); recipeMaterials != null; recipeMaterials = recipeMaterials.getNextSibling())
							{
								NamedNodeMap attrs = recipeMaterials.getAttributes();
								
								switch (recipeMaterials.getNodeName())
								{
									case "item":
										int materialItemId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
										int materialItemCount = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
										
										recipe.addRecipe(new RecipeMaterial(materialItemId, materialItemCount));
										break;
								}
								
							}
							break;
							
						case "products":
							Node recipeProduct = recipeInfo.getFirstChild().getNextSibling();
							NamedNodeMap attrs = recipeProduct.getAttributes();
							
							int productItemId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int productItemCount = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());
							
							recipe.setProdcutItemId(productItemId);
							recipe.setProductItemCount(productItemCount);
							
							break;
					}
				}
				
				recipes.put(recipe.getId(), recipe);
			}
			
			LOGGER.info("Loaded: " + recipes.size() + " recipes");
		}
		catch (Exception e)
		{
			LOGGER.error("RecipeTable.RecipeTable : Error while creating table. ", e);
		}
	}
	
	public int getRecipesCount()
	{
		return recipes.size();
	}
	
	public Recipe getRecipeById(int recipeId)
	{
		return recipes.get(recipeId);
	}
	
	public Recipe getRecipeByItemId(int recipeItemId)
	{
		for(Recipe recipe : recipes.values())
		{
			if (recipe.getRecipeId() == recipeItemId)
			{
				return recipe;
			}
		}
		
		return null;
	}
}
