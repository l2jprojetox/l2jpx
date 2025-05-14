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
package org.l2jpx.gameserver.scripting;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jpx.Config;
import org.l2jpx.commons.util.CommonUtil;
import org.l2jpx.commons.util.IXmlReader;
import org.l2jpx.gameserver.scripting.java.JavaExecutionContext;

/**
 * @author Mobius
 */
public class ScriptEngineManager implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ScriptEngineManager.class.getName());
	
	public static final Path SCRIPT_FOLDER = Config.SCRIPT_ROOT.toPath();
	public static final Path MASTER_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "handlers", "MasterHandler.java");
	public static final Path EFFECT_MASTER_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "handlers", "EffectMasterHandler.java");
	public static final Path SKILL_CONDITION_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "handlers", "SkillConditionMasterHandler.java");
	public static final Path CONDITION_HANDLER_FILE = Paths.get(SCRIPT_FOLDER.toString(), "handlers", "ConditionMasterHandler.java");
	public static final Path ONE_DAY_REWARD_MASTER_HANDLER = Paths.get(SCRIPT_FOLDER.toString(), "handlers", "DailyMissionMasterHandler.java");
	
	private static final JavaExecutionContext JAVA_EXECUTION_CONTEXT = new JavaExecutionContext();
	private static final List<String> EXCLUSIONS = new ArrayList<>();
	
	protected ScriptEngineManager()
	{
		// Load Scripts.xml
		load();
	}
	
	@Override
	public void load()
	{
		EXCLUSIONS.clear();
		parseDatapackFile("config/Scripts.xml");
		LOGGER.info("Loaded " + EXCLUSIONS.size() + " files to exclude.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		try
		{
			final Map<String, List<String>> excludePaths = new HashMap<>();
			forEach(doc, "list", listNode -> forEach(listNode, "exclude", excludeNode ->
			{
				final String excludeFile = parseString(excludeNode.getAttributes(), "file");
				excludePaths.putIfAbsent(excludeFile, new ArrayList<>());
				
				forEach(excludeNode, "include", includeNode -> excludePaths.get(excludeFile).add(parseString(includeNode.getAttributes(), "file")));
			}));
			
			final int nameCount = SCRIPT_FOLDER.getNameCount();
			Files.walkFileTree(SCRIPT_FOLDER, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					final String fileName = file.getFileName().toString();
					if (fileName.endsWith(".java"))
					{
						final Iterator<Path> relativePath = file.subpath(nameCount, file.getNameCount()).iterator();
						while (relativePath.hasNext())
						{
							final String nextPart = relativePath.next().toString();
							if (excludePaths.containsKey(nextPart))
							{
								boolean excludeScript = true;
								
								final List<String> includePath = excludePaths.get(nextPart);
								if (includePath != null)
								{
									while (relativePath.hasNext())
									{
										if (includePath.contains(relativePath.next().toString()))
										{
											excludeScript = false;
											break;
										}
									}
								}
								if (excludeScript)
								{
									EXCLUSIONS.add(file.toUri().getPath());
									break;
								}
							}
						}
					}
					return super.visitFile(file, attrs);
				}
			});
		}
		catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Couldn't load script exclusions.", e);
		}
	}
	
	private void processDirectory(File dir, List<Path> files)
	{
		for (File file : dir.listFiles())
		{
			if (file.isFile())
			{
				final String filePath = file.toURI().getPath();
				if (filePath.endsWith(".java") && !EXCLUSIONS.contains(filePath))
				{
					files.add(file.toPath().toAbsolutePath());
				}
			}
			else if (file.isDirectory())
			{
				processDirectory(file, files);
			}
		}
	}
	
	public void executeScript(Path sourceFiles) throws Exception
	{
		Path path = sourceFiles;
		if (!path.isAbsolute())
		{
			path = SCRIPT_FOLDER.resolve(path);
		}
		
		path = path.toAbsolutePath();
		// System.out.println("Executing script at path: " + path.toString());
		
		// Check if the path exists
		if (!Files.exists(path))
		{
			throw new Exception("Script file does not exist: " + path.toString());
		}
		
		// Execute the script and check for errors.
		final Entry<Path, Throwable> error = JAVA_EXECUTION_CONTEXT.executeScript(path);
		if (error != null)
		{
			final Throwable cause = error.getValue();
			if (cause != null)
			{
				LOGGER.warning(CommonUtil.getStackTrace(cause));
			}
			throw new Exception("ScriptEngine: " + error.getKey() + " failed execution!", cause);
		}
	}
	
	public void executeScriptList() throws Exception
	{
		if (Config.ALT_DEV_NO_QUESTS)
		{
			return;
		}
		
		final List<Path> files = new ArrayList<>();
		processDirectory(SCRIPT_FOLDER.toFile(), files);
		
		final Map<Path, Throwable> invokationErrors = JAVA_EXECUTION_CONTEXT.executeScripts(files);
		for (Entry<Path, Throwable> entry : invokationErrors.entrySet())
		{
			LOGGER.log(Level.WARNING, "ScriptEngine: " + entry.getKey() + " failed execution!", entry.getValue());
		}
	}
	
	public Path getCurrentLoadingScript()
	{
		return JAVA_EXECUTION_CONTEXT.getCurrentExecutingScript();
	}
	
	public static ScriptEngineManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
	}
}