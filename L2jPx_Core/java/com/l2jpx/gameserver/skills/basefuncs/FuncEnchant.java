package com.l2jpx.gameserver.skills.basefuncs;

import com.l2jpx.gameserver.enums.items.WeaponType;
import com.l2jpx.gameserver.enums.skills.Stats;
import com.l2jpx.gameserver.model.actor.Creature;
import com.l2jpx.gameserver.model.item.instance.ItemInstance;
import com.l2jpx.gameserver.skills.L2Skill;
import com.l2jpx.gameserver.skills.conditions.Condition;

/**
 * @see Func
 */
public class FuncEnchant extends Func
{
	public FuncEnchant(Object owner, Stats stat, double value, Condition cond)
	{
		super(owner, stat, 3, value, cond);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		// Condition does not exist or it fails, no change.
		if (getCond() != null && !getCond().test(effector, effected, skill))
			return value;
		
		final ItemInstance item = (ItemInstance) getFuncOwner();
		
		int enchant = item.getEnchantLevel();
		if (enchant <= 0)
			return value;
		
		int overenchant = 0;
		if (enchant > 3)
		{
			overenchant = enchant - 3;
			enchant = 3;
		}
		
		if (getStat() == Stats.MAGIC_DEFENCE || getStat() == Stats.POWER_DEFENCE)
			return value + enchant + (3 * overenchant);
		
		if (getStat() == Stats.MAGIC_ATTACK)
		{
			switch (item.getItem().getCrystalType())
			{
				case S:
					value += (4 * enchant + 8 * overenchant);
					break;
				
				case A:
				case B:
				case C:
					value += (3 * enchant + 6 * overenchant);
					break;
				
				case D:
					value += (2 * enchant + 4 * overenchant);
					break;
			}
			return value;
		}
		
		if (item.isWeapon())
		{
			final WeaponType type = (WeaponType) item.getItemType();
			
			switch (item.getItem().getCrystalType())
			{
				case S:
					switch (type)
					{
						case BOW:
							value += (10 * enchant + 20 * overenchant);
							break;
						
						case BIGBLUNT:
						case BIGSWORD:
						case DUALFIST:
						case DUAL:
							value += (6 * enchant + 12 * overenchant);
							break;
						
						default:
							value += (5 * enchant + 10 * overenchant);
							break;
					}
					break;
				
				case A:
					switch (type)
					{
						case BOW:
							value += (8 * enchant + 16 * overenchant);
							break;
						
						case BIGBLUNT:
						case BIGSWORD:
						case DUALFIST:
						case DUAL:
							value += (5 * enchant + 10 * overenchant);
							break;
						
						default:
							value += (4 * enchant + 8 * overenchant);
							break;
					}
					break;
				
				case B:
					switch (type)
					{
						case BOW:
							value += (6 * enchant + 12 * overenchant);
							break;
						
						case BIGBLUNT:
						case BIGSWORD:
						case DUALFIST:
						case DUAL:
							value += (4 * enchant + 8 * overenchant);
							break;
						
						default:
							value += (3 * enchant + 6 * overenchant);
							break;
					}
					break;
				
				case C:
					switch (type)
					{
						case BOW:
							value += (6 * enchant + 12 * overenchant);
							break;
						
						case BIGBLUNT:
						case BIGSWORD:
						case DUALFIST:
						case DUAL:
							value += (4 * enchant + 8 * overenchant);
							break;
						
						default:
							value += (3 * enchant + 6 * overenchant);
							break;
					}
					break;
				
				case D:
					switch (type)
					{
						case BOW:
							value += (4 * enchant + 8 * overenchant);
							break;
						
						default:
							value += (2 * enchant + 4 * overenchant);
							break;
					}
					break;
			}
		}
		return value;
	}
}