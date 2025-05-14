/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.gameserver.model.actor.templates;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.l2jpx.gameserver.enums.Race;
import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.events.ListenersContainer;
import org.l2jpx.gameserver.model.item.type.WeaponType;
import org.l2jpx.gameserver.model.skill.Skill;
import org.l2jpx.gameserver.model.stats.Stat;

/**
 * Character template.
 * @author Zoey76
 */
public class CreatureTemplate extends ListenersContainer
{
	// BaseStats
	private WeaponType _baseAttackType;
	
	/** For client info use {@link #_fCollisionRadius} */
	private int _collisionRadius;
	/** For client info use {@link #_fCollisionHeight} */
	private int _collisionHeight;
	
	private float _fCollisionRadius;
	private float _fCollisionHeight;
	
	protected final Map<Stat, Double> _baseValues = new EnumMap<>(Stat.class);
	
	/** The creature's race. */
	private Race _race;
	
	public CreatureTemplate(StatSet set)
	{
		set(set);
	}
	
	public void set(StatSet set)
	{
		// Base stats
		_baseValues.put(Stat.STAT_STR, set.getDouble("baseSTR", 0));
		_baseValues.put(Stat.STAT_CON, set.getDouble("baseCON", 0));
		_baseValues.put(Stat.STAT_DEX, set.getDouble("baseDEX", 0));
		_baseValues.put(Stat.STAT_INT, set.getDouble("baseINT", 0));
		_baseValues.put(Stat.STAT_WIT, set.getDouble("baseWIT", 0));
		_baseValues.put(Stat.STAT_MEN, set.getDouble("baseMEN", 0));
		
		// Max HP/MP/CP
		_baseValues.put(Stat.MAX_HP, set.getDouble("baseHpMax", 0));
		_baseValues.put(Stat.MAX_MP, set.getDouble("baseMpMax", 0));
		_baseValues.put(Stat.MAX_CP, set.getDouble("baseCpMax", 0));
		
		// Regenerate HP/MP/CP
		_baseValues.put(Stat.REGENERATE_HP_RATE, set.getDouble("baseHpReg", 0));
		_baseValues.put(Stat.REGENERATE_MP_RATE, set.getDouble("baseMpReg", 0));
		_baseValues.put(Stat.REGENERATE_CP_RATE, set.getDouble("baseCpReg", 0));
		
		// Attack and Defense
		_baseValues.put(Stat.PHYSICAL_ATTACK, set.getDouble("basePAtk", 0));
		_baseValues.put(Stat.MAGIC_ATTACK, set.getDouble("baseMAtk", 0));
		_baseValues.put(Stat.PHYSICAL_DEFENCE, set.getDouble("basePDef", 0));
		_baseValues.put(Stat.MAGICAL_DEFENCE, set.getDouble("baseMDef", 0));
		
		// Attack speed
		_baseValues.put(Stat.PHYSICAL_ATTACK_SPEED, set.getDouble("basePAtkSpd", 300));
		_baseValues.put(Stat.MAGIC_ATTACK_SPEED, set.getDouble("baseMAtkSpd", 333));
		
		// Misc
		_baseValues.put(Stat.SHIELD_DEFENCE, set.getDouble("baseShldDef", 0));
		_baseValues.put(Stat.PHYSICAL_ATTACK_RANGE, set.getDouble("baseAtkRange", 40));
		_baseValues.put(Stat.RANDOM_DAMAGE, set.getDouble("baseRndDam", 0));
		
		// Shield and critical rates
		_baseValues.put(Stat.SHIELD_DEFENCE_RATE, set.getDouble("baseShldRate", 0));
		_baseValues.put(Stat.CRITICAL_RATE, set.getDouble("baseCritRate", 4));
		_baseValues.put(Stat.MAGIC_CRITICAL_RATE, set.getDouble("baseMCritRate", 5));
		
		// Breath under water
		_baseValues.put(Stat.BREATH, set.getDouble("baseBreath", 100));
		
		// Elemental Attributes
		// Attack
		_baseValues.put(Stat.FIRE_POWER, set.getDouble("baseFire", 0));
		_baseValues.put(Stat.WIND_POWER, set.getDouble("baseWind", 0));
		_baseValues.put(Stat.WATER_POWER, set.getDouble("baseWater", 0));
		_baseValues.put(Stat.EARTH_POWER, set.getDouble("baseEarth", 0));
		_baseValues.put(Stat.HOLY_POWER, set.getDouble("baseHoly", 0));
		_baseValues.put(Stat.DARK_POWER, set.getDouble("baseDark", 0));
		
		// Defense
		_baseValues.put(Stat.FIRE_RES, set.getDouble("baseFireRes", 0));
		_baseValues.put(Stat.WIND_RES, set.getDouble("baseWindRes", 0));
		_baseValues.put(Stat.WATER_RES, set.getDouble("baseWaterRes", 0));
		_baseValues.put(Stat.EARTH_RES, set.getDouble("baseEarthRes", 0));
		_baseValues.put(Stat.HOLY_RES, set.getDouble("baseHolyRes", 0));
		_baseValues.put(Stat.DARK_RES, set.getDouble("baseDarkRes", 0));
		_baseValues.put(Stat.BASE_ATTRIBUTE_RES, set.getDouble("baseElementRes", 0));
		
		// Geometry
		_fCollisionHeight = set.getFloat("collision_height", 0);
		_fCollisionRadius = set.getFloat("collision_radius", 0);
		_collisionRadius = (int) _fCollisionRadius;
		_collisionHeight = (int) _fCollisionHeight;
		
		// Speed
		_baseValues.put(Stat.RUN_SPEED, set.getDouble("baseRunSpd", 120));
		_baseValues.put(Stat.WALK_SPEED, set.getDouble("baseWalkSpd", 50));
		
		// Swimming
		_baseValues.put(Stat.SWIM_RUN_SPEED, set.getDouble("baseSwimRunSpd", 120));
		_baseValues.put(Stat.SWIM_WALK_SPEED, set.getDouble("baseSwimWalkSpd", 50));
		
		// Flying
		_baseValues.put(Stat.FLY_RUN_SPEED, set.getDouble("baseFlyRunSpd", 120));
		_baseValues.put(Stat.FLY_WALK_SPEED, set.getDouble("baseFlyWalkSpd", 50));
		
		// Attack type
		_baseAttackType = set.getEnum("baseAtkType", WeaponType.class, WeaponType.FIST);
		
		// Basic property
		_baseValues.put(Stat.ABNORMAL_RESIST_PHYSICAL, set.getDouble("physicalAbnormalResist", 10));
		_baseValues.put(Stat.ABNORMAL_RESIST_MAGICAL, set.getDouble("magicAbnormalResist", 10));
	}
	
	/**
	 * @return the baseSTR
	 */
	public int getBaseSTR()
	{
		final Double val = _baseValues.get(Stat.STAT_STR);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseCON
	 */
	public int getBaseCON()
	{
		final Double val = _baseValues.get(Stat.STAT_CON);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseDEX
	 */
	public int getBaseDEX()
	{
		final Double val = _baseValues.get(Stat.STAT_DEX);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseINT
	 */
	public int getBaseINT()
	{
		final Double val = _baseValues.get(Stat.STAT_INT);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseWIT
	 */
	public int getBaseWIT()
	{
		final Double val = _baseValues.get(Stat.STAT_WIT);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseMEN
	 */
	public int getBaseMEN()
	{
		final Double val = _baseValues.get(Stat.STAT_MEN);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseHpMax
	 */
	public float getBaseHpMax()
	{
		final Double val = _baseValues.get(Stat.MAX_HP);
		return val != null ? val.floatValue() : 0;
	}
	
	/**
	 * @return the baseCpMax
	 */
	public float getBaseCpMax()
	{
		final Double val = _baseValues.get(Stat.MAX_CP);
		return val != null ? val.floatValue() : 0;
	}
	
	/**
	 * @return the baseMpMax
	 */
	public float getBaseMpMax()
	{
		final Double val = _baseValues.get(Stat.MAX_MP);
		return val != null ? val.floatValue() : 0;
	}
	
	/**
	 * @return the baseHpReg
	 */
	public float getBaseHpReg()
	{
		final Double val = _baseValues.get(Stat.REGENERATE_HP_RATE);
		return val != null ? val.floatValue() : 0;
	}
	
	/**
	 * @return the baseMpReg
	 */
	public float getBaseMpReg()
	{
		final Double val = _baseValues.get(Stat.REGENERATE_MP_RATE);
		return val != null ? val.floatValue() : 0;
	}
	
	/**
	 * @return the _baseFire
	 */
	public int getBaseFire()
	{
		final Double val = _baseValues.get(Stat.FIRE_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseWind
	 */
	public int getBaseWind()
	{
		final Double val = _baseValues.get(Stat.WIND_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseWater
	 */
	public int getBaseWater()
	{
		final Double val = _baseValues.get(Stat.WATER_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseEarth
	 */
	public int getBaseEarth()
	{
		final Double val = _baseValues.get(Stat.EARTH_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseHoly
	 */
	public int getBaseHoly()
	{
		final Double val = _baseValues.get(Stat.HOLY_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseDark
	 */
	public int getBaseDark()
	{
		final Double val = _baseValues.get(Stat.DARK_POWER);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseFireRes
	 */
	public double getBaseFireRes()
	{
		final Double val = _baseValues.get(Stat.FIRE_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseWindRes
	 */
	public double getBaseWindRes()
	{
		final Double val = _baseValues.get(Stat.WIND_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseWaterRes
	 */
	public double getBaseWaterRes()
	{
		final Double val = _baseValues.get(Stat.WATER_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseEarthRes
	 */
	public double getBaseEarthRes()
	{
		final Double val = _baseValues.get(Stat.EARTH_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseHolyRes
	 */
	public double getBaseHolyRes()
	{
		final Double val = _baseValues.get(Stat.HOLY_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseDarkRes
	 */
	public double getBaseDarkRes()
	{
		final Double val = _baseValues.get(Stat.DARK_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the _baseElementRes
	 */
	public double getBaseElementRes()
	{
		final Double val = _baseValues.get(Stat.BASE_ATTRIBUTE_RES);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the basePAtk
	 */
	public int getBasePAtk()
	{
		final Double val = _baseValues.get(Stat.PHYSICAL_ATTACK);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseMAtk
	 */
	public int getBaseMAtk()
	{
		final Double val = _baseValues.get(Stat.MAGIC_ATTACK);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the basePDef
	 */
	public int getBasePDef()
	{
		final Double val = _baseValues.get(Stat.PHYSICAL_DEFENCE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseMDef
	 */
	public int getBaseMDef()
	{
		final Double val = _baseValues.get(Stat.MAGICAL_DEFENCE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the basePAtkSpd
	 */
	public int getBasePAtkSpd()
	{
		final Double val = _baseValues.get(Stat.PHYSICAL_ATTACK_SPEED);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseMAtkSpd
	 */
	public int getBaseMAtkSpd()
	{
		final Double val = _baseValues.get(Stat.MAGIC_ATTACK_SPEED);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the random damage
	 */
	public int getRandomDamage()
	{
		final Double val = _baseValues.get(Stat.RANDOM_DAMAGE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseShldDef
	 */
	public int getBaseShldDef()
	{
		final Double val = _baseValues.get(Stat.SHIELD_DEFENCE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseShldRate
	 */
	public int getBaseShldRate()
	{
		final Double val = _baseValues.get(Stat.SHIELD_DEFENCE_RATE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseCritRate
	 */
	public int getBaseCritRate()
	{
		final Double val = _baseValues.get(Stat.CRITICAL_RATE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseMCritRate
	 */
	public int getBaseMCritRate()
	{
		final Double val = _baseValues.get(Stat.MAGIC_CRITICAL_RATE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the baseBreath
	 */
	public int getBaseBreath()
	{
		final Double val = _baseValues.get(Stat.BREATH);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return base abnormal resist by basic property type.
	 */
	public int getBaseAbnormalResistPhysical()
	{
		final Double val = _baseValues.get(Stat.ABNORMAL_RESIST_PHYSICAL);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return base abnormal resist by basic property type.
	 */
	public int getBaseAbnormalResistMagical()
	{
		final Double val = _baseValues.get(Stat.ABNORMAL_RESIST_MAGICAL);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * @return the collisionRadius
	 */
	public int getCollisionRadius()
	{
		return _collisionRadius;
	}
	
	/**
	 * @return the collisionHeight
	 */
	public int getCollisionHeight()
	{
		return _collisionHeight;
	}
	
	/**
	 * @return the fCollisionRadius
	 */
	public float getFCollisionRadius()
	{
		return _fCollisionRadius;
	}
	
	/**
	 * @return the fCollisionHeight
	 */
	public float getFCollisionHeight()
	{
		return _fCollisionHeight;
	}
	
	/**
	 * @return the base attack type (Sword, Fist, Blunt, etc..)
	 */
	public WeaponType getBaseAttackType()
	{
		return _baseAttackType;
	}
	
	/**
	 * Sets base attack type.
	 * @param type
	 */
	public void setBaseAttackType(WeaponType type)
	{
		_baseAttackType = type;
	}
	
	/**
	 * @return the baseAtkRange
	 */
	public int getBaseAttackRange()
	{
		final Double val = _baseValues.get(Stat.PHYSICAL_ATTACK_RANGE);
		return val != null ? val.intValue() : 0;
	}
	
	/**
	 * Overridden in NpcTemplate
	 * @return the characters skills
	 */
	public Map<Integer, Skill> getSkills()
	{
		return Collections.emptyMap();
	}
	
	/**
	 * Gets the craeture's race.
	 * @return the race
	 */
	public Race getRace()
	{
		return _race;
	}
	
	/**
	 * Sets the creature's race.
	 * @param race the race
	 */
	public void setRace(Race race)
	{
		_race = race;
	}
	
	/**
	 * @param stat
	 * @param defaultValue
	 * @return
	 */
	public double getBaseValue(Stat stat, double defaultValue)
	{
		final Double val = _baseValues.get(stat);
		return val != null ? val.doubleValue() : defaultValue;
	}
}
