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
package org.l2jpx.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.l2jpx.gameserver.model.StatSet;
import org.l2jpx.gameserver.model.effects.AbstractEffect;
import org.l2jpx.gameserver.scripting.ScriptEngineManager;

/**
 * @author BiggBoss, UnAfraid
 */
public class EffectHandler
{
	private final Map<String, Function<StatSet, AbstractEffect>> _effectHandlerFactories = new HashMap<>();
	
	public void registerHandler(String name, Function<StatSet, AbstractEffect> handlerFactory)
	{
		_effectHandlerFactories.put(name, handlerFactory);
	}
	
	public Function<StatSet, AbstractEffect> getHandlerFactory(String name)
	{
		return _effectHandlerFactories.get(name);
	}
	
	public int size()
	{
		return _effectHandlerFactories.size();
	}
	
	public void executeScript()
	{
		try
		{
			ScriptEngineManager.getInstance().executeScript(ScriptEngineManager.EFFECT_MASTER_HANDLER_FILE);
		}
		catch (Exception e)
		{
			throw new Error("Problems while running EffectMasterHandler", e);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final EffectHandler INSTANCE = new EffectHandler();
	}
	
	public static EffectHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}
