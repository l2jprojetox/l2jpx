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
package org.l2jpx.gameserver.scripting.java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.l2jpx.gameserver.scripting.annotations.Disabled;

/**
 * @author HorridoJoho
 */
public class JavaExecutionContext
{
	private static final Logger LOGGER = Logger.getLogger(JavaExecutionContext.class.getName());
	
	private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
	private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
	private static final List<String> OPTIONS = new ArrayList<>();
	
	private static Path _currentExecutingScript;
	
	public JavaExecutionContext()
	{
		// The Java version is hardcoded to "1.8" for both the source and target options in the compiler settings.
		// This decision is primarily driven by compatibility considerations with the scripting environment.
		addOptionIfNotNull(OPTIONS, "1.8", "-source");
		addOptionIfNotNull(OPTIONS, "data/scripts", "-sourcepath");
		addOptionIfNotNull(OPTIONS, "source,lines,vars", "-g:");
		OPTIONS.add("-target");
		OPTIONS.add("1.8");
	}
	
	private boolean addOptionIfNotNull(List<String> list, String nullChecked, String before)
	{
		if (nullChecked == null)
		{
			return false;
		}
		
		if (before.endsWith(":"))
		{
			list.add(before + nullChecked);
		}
		else
		{
			list.add(before);
			list.add(nullChecked);
		}
		
		return true;
	}
	
	public Map<Path, Throwable> executeScripts(Iterable<Path> sourcePaths) throws Exception
	{
		final DiagnosticCollector<JavaFileObject> fileManagerDiagnostics = new DiagnosticCollector<>();
		final DiagnosticCollector<JavaFileObject> compilationDiagnostics = new DiagnosticCollector<>();
		
		try (ScriptingFileManager fileManager = new ScriptingFileManager(COMPILER.getStandardFileManager(fileManagerDiagnostics, null, StandardCharsets.UTF_8)))
		{
			// We really need an iterable of files or strings.
			final List<String> sourcePathStrings = new ArrayList<>();
			for (Path sourcePath : sourcePaths)
			{
				sourcePathStrings.add(sourcePath.toAbsolutePath().toString());
			}
			
			final StringWriter strOut = new StringWriter();
			final PrintWriter out = new PrintWriter(strOut);
			final boolean compilationSuccess = COMPILER.getTask(out, fileManager, compilationDiagnostics, OPTIONS, null, fileManager.getJavaFileObjectsFromStrings(sourcePathStrings)).call();
			if (!compilationSuccess)
			{
				logDiagnostics(out, fileManagerDiagnostics, compilationDiagnostics);
				throw new RuntimeException(strOut.toString());
			}
			
			final Map<Path, Throwable> executionFailures = new HashMap<>();
			final Iterable<ScriptingOutputFileObject> compiledClasses = fileManager.getCompiledClasses();
			for (Path sourcePath : sourcePaths)
			{
				boolean found = false;
				for (ScriptingOutputFileObject compiledClass : compiledClasses)
				{
					final Path compiledSourcePath = compiledClass.getSourcePath();
					// sourcePath can be relative, so we have to use endsWith
					if ((compiledSourcePath != null) && (compiledSourcePath.equals(sourcePath) || compiledSourcePath.endsWith(sourcePath)))
					{
						final String javaName = compiledClass.getJavaName();
						if (javaName.indexOf('$') != -1)
						{
							continue;
						}
						
						found = true;
						_currentExecutingScript = compiledSourcePath;
						try
						{
							final ScriptingClassLoader loader = new ScriptingClassLoader(CLASS_LOADER, compiledClasses);
							final Class<?> javaClass = loader.loadClass(javaName);
							executeMainMethod(javaClass, compiledSourcePath);
						}
						catch (Exception e)
						{
							executionFailures.put(compiledSourcePath, e);
						}
						finally
						{
							_currentExecutingScript = null;
						}
						break;
					}
				}
				
				if (!found)
				{
					LOGGER.severe("Compilation successful, but class corresponding to " + sourcePath.toString() + " not found!");
				}
			}
			
			return executionFailures;
		}
	}
	
	private void logDiagnostics(PrintWriter out, DiagnosticCollector<JavaFileObject> fileManagerDiagnostics, DiagnosticCollector<JavaFileObject> compilationDiagnostics)
	{
		out.println();
		out.println("----------------");
		out.println("File diagnostics");
		out.println("----------------");
		for (Diagnostic<? extends JavaFileObject> diagnostic : fileManagerDiagnostics.getDiagnostics())
		{
			logDiagnostic(out, diagnostic);
		}
		
		out.println();
		out.println("-----------------------");
		out.println("Compilation diagnostics");
		out.println("-----------------------");
		for (Diagnostic<? extends JavaFileObject> diagnostic : compilationDiagnostics.getDiagnostics())
		{
			logDiagnostic(out, diagnostic);
		}
	}
	
	private void logDiagnostic(PrintWriter out, Diagnostic<? extends JavaFileObject> diagnostic)
	{
		String sourceName = (diagnostic.getSource() != null) ? diagnostic.getSource().getName() : "Unknown Source";
		out.println("\t" + diagnostic.getKind() + ": " + sourceName + ", Line " + diagnostic.getLineNumber() + ", Column " + diagnostic.getColumnNumber());
		out.println("\t\tcode: " + diagnostic.getCode());
		out.println("\t\tmessage: " + diagnostic.getMessage(null));
	}
	
	private void executeMainMethod(Class<?> javaClass, Path compiledSourcePath) throws Exception
	{
		if (javaClass.isAnnotationPresent(Disabled.class))
		{
			return;
		}
		
		for (Method method : javaClass.getMethods())
		{
			if (method.getName().equals("main") && Modifier.isStatic(method.getModifiers()) && (method.getParameterCount() == 1) && (method.getParameterTypes()[0] == String[].class))
			{
				method.invoke(null, (Object) new String[]
				{
					compiledSourcePath.toString()
				});
				break;
			}
		}
	}
	
	public Entry<Path, Throwable> executeScript(Path sourcePath) throws Exception
	{
		final Map<Path, Throwable> executionFailures = executeScripts(Arrays.asList(sourcePath));
		if (!executionFailures.isEmpty())
		{
			return executionFailures.entrySet().iterator().next();
		}
		return null;
	}
	
	public Path getCurrentExecutingScript()
	{
		return _currentExecutingScript;
	}
}
