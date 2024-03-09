package net.l2jpx.gameserver.model.actor.instance;

import net.l2jpx.gameserver.templates.L2Henna;

/**
 * This class represents a Non-Player-Character in the world. it can be a monster or a friendly character. it also uses a template to fetch some static values. the templates are hardcoded in the client, so we can rely on them.
 * @author ReynalDev
 */

public class L2HennaInstance
{
	// private static Logger LOGGER = Logger.getLogger(L2HennaInstance.class);
	
	private final L2Henna hennaTemplate;
	private int symbolId;
	private int itemIdDye;
	private int price;
	private int cancelFee;
	private int statINT;
	private int statSTR;
	private int statCON;
	private int statMEN;
	private int statDEX;
	private int statWIT;
	private int amountDyeRequire;
	
	public L2HennaInstance(final L2Henna template)
	{
		hennaTemplate = template;
		symbolId = hennaTemplate.getSymbolId();
		itemIdDye = hennaTemplate.getDyeId();
		amountDyeRequire = hennaTemplate.getAmountDyeRequire();
		price = hennaTemplate.getPrice();
		cancelFee = hennaTemplate.getCancelFee();
		statINT = hennaTemplate.getStatINT();
		statSTR = hennaTemplate.getStatSTR();
		statCON = hennaTemplate.getStatCON();
		statMEN = hennaTemplate.getStatMEN();
		statDEX = hennaTemplate.getStatDEX();
		statWIT = hennaTemplate.getStatWIT();
	}
	
	public String getName()
	{
		String res = "";
		if (statINT > 0)
		{
			res = res + "INT +" + statINT;
		}
		else if (statSTR > 0)
		{
			res = res + "STR +" + statSTR;
		}
		else if (statCON > 0)
		{
			res = res + "CON +" + statCON;
		}
		else if (statMEN > 0)
		{
			res = res + "MEN +" + statMEN;
		}
		else if (statDEX > 0)
		{
			res = res + "DEX +" + statDEX;
		}
		else if (statWIT > 0)
		{
			res = res + "WIT +" + statWIT;
		}
		
		if (statINT < 0)
		{
			res = res + ", INT " + statINT;
		}
		else if (statSTR < 0)
		{
			res = res + ", STR " + statSTR;
		}
		else if (statCON < 0)
		{
			res = res + ", CON " + statCON;
		}
		else if (statMEN < 0)
		{
			res = res + ", MEN " + statMEN;
		}
		else if (statDEX < 0)
		{
			res = res + ", DEX " + statDEX;
		}
		else if (statWIT < 0)
		{
			res = res + ", WIT " + statWIT;
		}
		
		return res;
	}
	
	public L2Henna getTemplate()
	{
		return hennaTemplate;
	}
	
	public int getSymbolId()
	{
		return symbolId;
	}
	
	public void setSymbolId(final int SymbolId)
	{
		symbolId = SymbolId;
	}
	
	public int getItemIdDye()
	{
		return itemIdDye;
	}
	
	public void setItemIdDye(final int ItemIdDye)
	{
		itemIdDye = ItemIdDye;
	}
	
	public int getAmountDyeRequire()
	{
		return amountDyeRequire;
	}
	
	public void setAmountDyeRequire(final int AmountDyeRequire)
	{
		amountDyeRequire = AmountDyeRequire;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public void setPrice(final int Price)
	{
		price = Price;
	}
	
	public int getCancelFee()
	{
		return cancelFee;
	}
	
	public void setCancelFee(int price)
	{
		cancelFee = price;
	}
	
	public int getStatINT()
	{
		return statINT;
	}
	
	public void setStatINT(final int StatINT)
	{
		statINT = StatINT;
	}
	
	public int getStatSTR()
	{
		return statSTR;
	}
	
	public void setStatSTR(final int StatSTR)
	{
		statSTR = StatSTR;
	}
	
	public int getStatCON()
	{
		return statCON;
	}
	
	public void setStatCON(final int StatCON)
	{
		statCON = StatCON;
	}
	
	public int getStatMEN()
	{
		return statMEN;
	}
	
	public void setStatMEN(final int StatMEM)
	{
		statMEN = StatMEM;
	}
	
	public int getStatDEX()
	{
		return statDEX;
	}
	
	public void setStatDEX(final int StatDEX)
	{
		statDEX = StatDEX;
	}
	
	public int getStatWIT()
	{
		return statWIT;
	}
	
	public void setStatWIT(final int StatWIT)
	{
		statWIT = StatWIT;
	}
}
