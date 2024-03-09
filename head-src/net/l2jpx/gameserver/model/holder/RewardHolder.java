package net.l2jpx.gameserver.model.holder;

/**
 * @author fissban
 */
public class RewardHolder
{
	private int id;
	private int count;
	private int chance;
	private int enchantLevel;
	
	/**
	 * @param rewardId
	 * @param rewardCount
	 * @param enchantLevel
	 */
	public RewardHolder(int rewardId, int rewardCount)
	{
		id = rewardId;
		count = rewardCount;
		chance = 100;
	}
	
	/**
	 * @param rewardId
	 * @param rewardCount
	 * @param rewardChance
	 * @param enchantLevel
	 */
	// Construtor completo incluindo o nível de encantamento
	public RewardHolder(int rewardId, int rewardCount, int rewardChance, int enchantLevel)
	{
		this.id = rewardId;
		this.count = rewardCount;
		this.chance = rewardChance;
		this.enchantLevel = enchantLevel;
	}
	
	public int getRewardId()
	{
		return id;
	}
	
	public int getRewardCount()
	{
		return count;
	}
	
	public int getRewardChance()
	{
		return chance;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public void setChance(int chance)
	{
		this.chance = chance;
	}
	
	public int setEnchantLevel(int enchantLevel)
	{
		return this.enchantLevel = enchantLevel;
	}
	
	public int getEnchantLevel()
	{ // Getter para o nível de encantamento
		return enchantLevel;
	}
}