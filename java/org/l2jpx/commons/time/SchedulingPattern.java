/*
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jpx.commons.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.l2jpx.commons.util.Rnd;
import org.l2jpx.commons.util.TimeUtil;

/**
 * <p>
 * A UNIX crontab-like pattern is a string split in five space separated parts. Each part is intented as:
 * </p>
 * <ol>
 * <li><strong>Minutes sub-pattern</strong>. During which minutes of the hour should the task been launched? The values range is from 0 to 59.</li>
 * <li><strong>Hours sub-pattern</strong>. During which hours of the day should the task been launched? The values range is from 0 to 23.</li>
 * <li><strong>Days of month sub-pattern</strong>. During which days of the month should the task been launched? The values range is from 1 to 31. The special value L can be used to recognize the last day of month.</li>
 * <li><strong>Months sub-pattern</strong>. During which months of the year should the task been launched? The values range is from 1 (January) to 12 (December), otherwise this sub-pattern allows the aliases &quot;jan&quot;, &quot;feb&quot;, &quot;mar&quot;, &quot;apr&quot;, &quot;may&quot;,
 * &quot;jun&quot;, &quot;jul&quot;, &quot;aug&quot;, &quot;sep&quot;, &quot;oct&quot;, &quot;nov&quot; and &quot;dec&quot;.</li>
 * <li><strong>Days of week sub-pattern</strong>. During which days of the week should the task been launched? The values range is from 0 (Sunday) to 6 (Saturday), otherwise this sub-pattern allows the aliases &quot;sun&quot;, &quot;mon&quot;, &quot;tue&quot;, &quot;wed&quot;, &quot;thu&quot;,
 * &quot;fri&quot; and &quot;sat&quot;.</li>
 * </ol>
 * <p>
 * The star wildcard character is also admitted, indicating &quot;every minute of the hour&quot;, &quot;every hour of the day&quot;, &quot;every day of the month&quot;, &quot;every month of the year&quot; and &quot;every day of the week&quot;, according to the sub-pattern in which it is used.
 * </p>
 * <p>
 * Once the scheduler is started, a task will be launched when the five parts in its scheduling pattern will be true at the same time.
 * </p>
 * <p>
 * Some examples:
 * </p>
 * <p>
 * <strong>5 * * * *</strong><br />
 * This pattern causes a task to be launched once every hour, at the begin of the fifth minute (00:05, 01:05, 02:05 etc.).
 * </p>
 * <p>
 * <strong>* * * * *</strong><br />
 * This pattern causes a task to be launched every minute.
 * </p>
 * <p>
 * <strong>* 12 * * Mon</strong><br />
 * This pattern causes a task to be launched every minute during the 12th hour of Monday.
 * </p>
 * <p>
 * <strong>* 12 16 * Mon</strong><br />
 * This pattern causes a task to be launched every minute during the 12th hour of Monday, 16th, but only if the day is the 16th of the month.
 * </p>
 * <p>
 * Every sub-pattern can contain two or more comma separated values.
 * </p>
 * <p>
 * <strong>59 11 * * 1,2,3,4,5</strong><br />
 * This pattern causes a task to be launched at 11:59AM on Monday, Tuesday, Wednesday, Thursday and Friday.
 * </p>
 * <p>
 * Values intervals are admitted and defined using the minus character.
 * </p>
 * <p>
 * <strong>59 11 * * 1-5</strong><br />
 * This pattern is equivalent to the previous one.
 * </p>
 * <p>
 * The slash character can be used to identify step values within a range. It can be used both in the form <em>*&#47;c</em> and <em>a-b/c</em>. The subpattern is matched every <em>c</em> values of the range <em>0,maxvalue</em> or <em>a-b</em>.
 * </p>
 * <p>
 * <strong>*&#47;5 * * * *</strong><br />
 * This pattern causes a task to be launched every 5 minutes (0:00, 0:05, 0:10, 0:15 and so on).
 * </p>
 * <p>
 * <strong>3-18&#47;5 * * * *</strong><br />
 * This pattern causes a task to be launched every 5 minutes starting from the third minute of the hour, up to the 18th (0:03, 0:08, 0:13, 0:18, 1:03, 1:08 and so on).
 * </p>
 * <p>
 * <strong>*&#47;15 9-17 * * *</strong><br />
 * This pattern causes a task to be launched every 15 minutes between the 9th and 17th hour of the day (9:00, 9:15, 9:30, 9:45 and so on... note that the last execution will be at 17:45).
 * </p>
 * <p>
 * All the fresh described syntax rules can be used together.
 * </p>
 * <p>
 * <strong>* 12 10-16&#47;2 * *</strong><br />
 * This pattern causes a task to be launched every minute during the 12th hour of the day, but only if the day is the 10th, the 12th, the 14th or the 16th of the month.
 * </p>
 * <p>
 * <strong>* 12 1-15,17,20-25 * *</strong><br />
 * This pattern causes a task to be launched every minute during the 12th hour of the day, but the day of the month must be between the 1st and the 15th, the 20th and the 25, or at least it must be the 17th.
 * </p>
 * <p>
 * Finally lets you combine more scheduling patterns into one, with the pipe character:
 * </p>
 * <p>
 * <strong>0 5 * * *|8 10 * * *|22 17 * * *</strong><br />
 * This pattern causes a task to be launched every day at 05:00, 10:08 and 17:22.
 * </p>
 * @author Carlo Pelliccia
 */
public class SchedulingPattern
{
	private static final int MINUTE_MIN_VALUE = 0;
	private static final int MINUTE_MAX_VALUE = 59;
	private static final int HOUR_MIN_VALUE = 0;
	private static final int HOUR_MAX_VALUE = 23;
	private static final int DAY_OF_MONTH_MIN_VALUE = 1;
	private static final int DAY_OF_MONTH_MAX_VALUE = 31;
	private static final int MONTH_MIN_VALUE = 1;
	private static final int MONTH_MAX_VALUE = 12;
	private static final int DAY_OF_WEEK_MIN_VALUE = 0;
	private static final int DAY_OF_WEEK_MAX_VALUE = 7;
	
	/**
	 * The parser for the minute values.
	 */
	private static final ValueParser MINUTE_VALUE_PARSER = new MinuteValueParser();
	
	/**
	 * The parser for the hour values.
	 */
	private static final ValueParser HOUR_VALUE_PARSER = new HourValueParser();
	
	/**
	 * The parser for the day of month values.
	 */
	private static final ValueParser DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();
	
	/**
	 * The parser for the month values.
	 */
	private static final ValueParser MONTH_VALUE_PARSER = new MonthValueParser();
	
	/**
	 * The parser for the day of week values.
	 */
	private static final ValueParser DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();
	
	/**
	 * The pattern as a string.
	 */
	private final String _asString;
	
	/**
	 * The ValueMatcher list for the "minute" field.
	 */
	protected List<ValueMatcher> _minuteMatchers = new ArrayList<>();
	
	/**
	 * The ValueMatcher list for the "hour" field.
	 */
	protected List<ValueMatcher> _hourMatchers = new ArrayList<>();
	
	/**
	 * The ValueMatcher list for the "day of month" field.
	 */
	protected List<ValueMatcher> _dayOfMonthMatchers = new ArrayList<>();
	
	/**
	 * The ValueMatcher list for the "month" field.
	 */
	protected List<ValueMatcher> _monthMatchers = new ArrayList<>();
	
	/**
	 * The ValueMatcher list for the "day of week" field.
	 */
	protected List<ValueMatcher> _dayOfWeekMatchers = new ArrayList<>();
	
	/**
	 * How many matcher groups in this pattern?
	 */
	protected int _matcherSize = 0;
	
	protected Map<Integer, Integer> _hourAdder = new TreeMap<>();
	protected Map<Integer, Integer> _hourAdderRnd = new TreeMap<>();
	protected Map<Integer, Integer> _dayOfYearAdder = new TreeMap<>();
	protected Map<Integer, Integer> _minuteAdderRnd = new TreeMap<>();
	protected Map<Integer, Integer> _weekOfYearAdder = new TreeMap<>();
	
	/**
	 * Validates a string as a scheduling pattern.
	 * @param schedulingPattern The pattern to validate.
	 * @return true if the given string represents a valid scheduling pattern; false otherwise.
	 */
	public static boolean validate(String schedulingPattern)
	{
		try
		{
			new SchedulingPattern(schedulingPattern);
		}
		catch (RuntimeException e)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Builds a SchedulingPattern parsing it from a string.
	 * @param pattern The pattern as a crontab-like string.
	 * @throws RuntimeException If the supplied string is not a valid pattern.
	 */
	public SchedulingPattern(String pattern) throws RuntimeException
	{
		_asString = pattern;
		final StringTokenizer st1 = new StringTokenizer(pattern, "|");
		if (st1.countTokens() < 1)
		{
			throw new RuntimeException("invalid pattern: \"" + pattern + "\"");
		}
		
		while (st1.hasMoreTokens())
		{
			final String localPattern = st1.nextToken();
			final StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
			if (st2.countTokens() != 5)
			{
				throw new RuntimeException("invalid pattern: \"" + localPattern + "\"");
			}
			
			try
			{
				String minutePattern = st2.nextToken();
				final String[] minutePatternParts = minutePattern.split(":");
				if (minutePatternParts.length > 1)
				{
					for (int i = 0; i < (minutePatternParts.length - 1); ++i)
					{
						if (minutePatternParts[i].length() <= 1)
						{
							continue;
						}
						
						if (minutePatternParts[i].startsWith("~"))
						{
							_minuteAdderRnd.put(_matcherSize, Integer.parseInt(minutePatternParts[i].substring(1)));
							continue;
						}
						
						throw new RuntimeException("Unknown hour modifier \"" + minutePatternParts[i] + "\"");
					}
					minutePattern = minutePatternParts[minutePatternParts.length - 1];
				}
				
				_minuteMatchers.add(buildValueMatcher(minutePattern, MINUTE_VALUE_PARSER));
			}
			catch (Exception e)
			{
				throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing minutes field: " + e.getMessage() + ".");
			}
			
			try
			{
				String hourPattern = st2.nextToken();
				final String[] hourPatternParts = hourPattern.split(":");
				if (hourPatternParts.length > 1)
				{
					for (int i = 0; i < (hourPatternParts.length - 1); ++i)
					{
						if (hourPatternParts[i].length() <= 1)
						{
							continue;
						}
						
						if (hourPatternParts[i].startsWith("+"))
						{
							_hourAdder.put(_matcherSize, Integer.parseInt(hourPatternParts[i].substring(1)));
							continue;
						}
						
						if (hourPatternParts[i].startsWith("~"))
						{
							_hourAdderRnd.put(_matcherSize, Integer.parseInt(hourPatternParts[i].substring(1)));
							continue;
						}
						
						throw new RuntimeException("Unknown hour modifier \"" + hourPatternParts[i] + "\"");
					}
					hourPattern = hourPatternParts[hourPatternParts.length - 1];
				}
				
				_hourMatchers.add(buildValueMatcher(hourPattern, HOUR_VALUE_PARSER));
			}
			
			catch (Exception e)
			{
				throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing hours field: " + e.getMessage() + ".");
			}
			
			try
			{
				String dayOfMonthPattern = st2.nextToken();
				final String[] dayOfMonthPatternParts = dayOfMonthPattern.split(":");
				if (dayOfMonthPatternParts.length > 1)
				{
					for (int i = 0; i < (dayOfMonthPatternParts.length - 1); ++i)
					{
						if (dayOfMonthPatternParts[i].length() <= 1)
						{
							continue;
						}
						
						if (dayOfMonthPatternParts[i].startsWith("+"))
						{
							_dayOfYearAdder.put(_matcherSize, Integer.parseInt(dayOfMonthPatternParts[i].substring(1)));
							continue;
						}
						
						throw new RuntimeException("Unknown day modifier \"" + dayOfMonthPatternParts[i] + "\"");
					}
					dayOfMonthPattern = dayOfMonthPatternParts[dayOfMonthPatternParts.length - 1];
				}
				
				_dayOfMonthMatchers.add(buildValueMatcher(dayOfMonthPattern, DAY_OF_MONTH_VALUE_PARSER));
			}
			catch (Exception e)
			{
				throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing days of month field: " + e.getMessage() + ".");
			}
			
			try
			{
				_monthMatchers.add(buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
			}
			catch (Exception e)
			{
				throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing months field: " + e.getMessage() + ".");
			}
			
			try
			{
				_dayOfWeekMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
			}
			catch (Exception e)
			{
				throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing days of week field: " + e.getMessage() + ".");
			}
			
			if (st2.hasMoreTokens())
			{
				try
				{
					String weekOfYearAdderText = st2.nextToken();
					if (weekOfYearAdderText.charAt(0) != '+')
					{
						throw new RuntimeException("Unknown week of year addition in pattern \"" + localPattern + "\".");
					}
					weekOfYearAdderText = weekOfYearAdderText.substring(1);
					_weekOfYearAdder.put(_matcherSize, Integer.parseInt(weekOfYearAdderText));
				}
				catch (Exception e)
				{
					throw new RuntimeException("invalid pattern \"" + localPattern + "\". Error parsing days of week field: " + e.getMessage() + ".");
				}
			}
			
			_matcherSize++;
		}
	}
	
	/**
	 * A ValueMatcher utility builder.
	 * @param str The pattern part for the ValueMatcher creation.
	 * @param parser The parser used to parse the values.
	 * @return The requested ValueMatcher.
	 * @throws Exception If the supplied pattern part is not valid.
	 */
	private ValueMatcher buildValueMatcher(String str, ValueParser parser) throws Exception
	{
		if ((str.length() == 1) && str.equals("*"))
		{
			return new AlwaysTrueValueMatcher();
		}
		
		final List<Integer> values = new ArrayList<>();
		final StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreTokens())
		{
			final String element = st.nextToken();
			List<Integer> local;
			try
			{
				local = parseListElement(element, parser);
			}
			catch (Exception e)
			{
				throw new Exception("invalid field \"" + str + "\", invalid element \"" + element + "\", " + e.getMessage());
			}
			
			for (Integer value : local)
			{
				if (values.contains(value))
				{
					continue;
				}
				
				values.add(value);
			}
		}
		
		if (values.size() == 0)
		{
			throw new Exception("invalid field \"" + str + "\"");
		}
		
		if (parser == DAY_OF_MONTH_VALUE_PARSER)
		{
			return new DayOfMonthValueMatcher(values);
		}
		
		return new IntArrayValueMatcher(values);
	}
	
	/**
	 * Parses an element of a list of values of the pattern.
	 * @param str The element string.
	 * @param parser The parser used to parse the values.
	 * @return A list of integers representing the allowed values.
	 * @throws Exception If the supplied pattern part is not valid.
	 */
	private List<Integer> parseListElement(String str, ValueParser parser) throws Exception
	{
		final StringTokenizer st = new StringTokenizer(str, "/");
		final int size = st.countTokens();
		if ((size < 1) || (size > 2))
		{
			throw new Exception("syntax error");
		}
		
		List<Integer> values;
		try
		{
			values = parseRange(st.nextToken(), parser);
		}
		catch (Exception e)
		{
			throw new Exception("invalid range, " + e.getMessage());
		}
		
		if (size == 2)
		{
			final String dStr = st.nextToken();
			int div;
			try
			{
				div = Integer.parseInt(dStr);
			}
			catch (NumberFormatException e)
			{
				throw new Exception("invalid divisor \"" + dStr + "\"");
			}
			
			if (div < 1)
			{
				throw new Exception("non positive divisor \"" + div + "\"");
			}
			
			final List<Integer> values2 = new ArrayList<>();
			for (int i = 0; i < values.size(); i += div)
			{
				values2.add(values.get(i));
			}
			return values2;
		}
		
		return values;
	}
	
	/**
	 * Parses a range of values.
	 * @param str The range string.
	 * @param parser The parser used to parse the values.
	 * @return A list of integers representing the allowed values.
	 * @throws Exception If the supplied pattern part is not valid.
	 */
	private List<Integer> parseRange(String str, ValueParser parser) throws Exception
	{
		if (str.equals("*"))
		{
			final int min = parser.getMinValue();
			final int max = parser.getMaxValue();
			final List<Integer> values = new ArrayList<>();
			for (int i = min; i <= max; i++)
			{
				values.add(i);
			}
			return values;
		}
		
		final StringTokenizer st = new StringTokenizer(str, "-");
		final int size = st.countTokens();
		if ((size < 1) || (size > 2))
		{
			throw new Exception("syntax error");
		}
		
		final String v1Str = st.nextToken();
		int v1;
		try
		{
			v1 = parser.parse(v1Str);
		}
		catch (Exception e)
		{
			throw new Exception("invalid value \"" + v1Str + "\", " + e.getMessage());
		}
		
		if (size == 1)
		{
			final List<Integer> values = new ArrayList<>();
			values.add(v1);
			return values;
		}
		
		final String v2Str = st.nextToken();
		int v2;
		try
		{
			v2 = parser.parse(v2Str);
		}
		catch (Exception e)
		{
			throw new Exception("invalid value \"" + v2Str + "\", " + e.getMessage());
		}
		
		final List<Integer> values = new ArrayList<>();
		if (v1 < v2)
		{
			for (int i = v1; i <= v2; i++)
			{
				values.add(i);
			}
		}
		else if (v1 > v2)
		{
			final int min = parser.getMinValue();
			final int max = parser.getMaxValue();
			for (int i = v1; i <= max; i++)
			{
				values.add(i);
			}
			for (int i = min; i <= v2; i++)
			{
				values.add(i);
			}
		}
		else
		{
			// v1 == v2
			values.add(v1);
		}
		return values;
	}
	
	/**
	 * This methods returns true if the given timestamp (expressed as a UNIX-era millis value) matches the pattern, according to the given time zone.
	 * @param timezone A time zone.
	 * @param millis The timestamp, as a UNIX-era millis value.
	 * @return true if the given timestamp matches the pattern.
	 */
	public boolean match(TimeZone timezone, long millis)
	{
		final GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(millis);
		gc.setTimeZone(timezone);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		for (int i = 0; i < _matcherSize; ++i)
		{
			if (_weekOfYearAdder.containsKey(i))
			{
				gc.add(Calendar.WEEK_OF_YEAR, -_weekOfYearAdder.get(i).intValue());
			}
			if (_dayOfYearAdder.containsKey(i))
			{
				gc.add(Calendar.DAY_OF_YEAR, -_dayOfYearAdder.get(i).intValue());
			}
			if (_hourAdder.containsKey(i))
			{
				gc.add(Calendar.HOUR, -_hourAdder.get(i).intValue());
			}
			final int minute = gc.get(Calendar.MINUTE);
			final int hour = gc.get(Calendar.HOUR_OF_DAY);
			final int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
			final int month = gc.get(Calendar.MONTH) + 1;
			final int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK) - 1;
			final int year = gc.get(Calendar.YEAR);
			final ValueMatcher minuteMatcher = _minuteMatchers.get(i);
			final ValueMatcher hourMatcher = _hourMatchers.get(i);
			final ValueMatcher dayOfMonthMatcher = _dayOfMonthMatchers.get(i);
			final ValueMatcher monthMatcher = _monthMatchers.get(i);
			final ValueMatcher dayOfWeekMatcher = _dayOfWeekMatchers.get(i);
			if (minuteMatcher.match(minute) && hourMatcher.match(hour) && ((dayOfMonthMatcher instanceof DayOfMonthValueMatcher) ? ((DayOfMonthValueMatcher) dayOfMonthMatcher).match(dayOfMonth, month, gc.isLeapYear(year)) : dayOfMonthMatcher.match(dayOfMonth)) && monthMatcher.match(month) && dayOfWeekMatcher.match(dayOfWeek))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This methods returns true if the given timestamp (expressed as a UNIX-era millis value) matches the pattern, according to the system default time zone.
	 * @param millis The timestamp, as a UNIX-era millis value.
	 * @return true if the given timestamp matches the pattern.
	 */
	public boolean match(long millis)
	{
		return match(TimeZone.getDefault(), millis);
	}
	
	/**
	 * It returns the next matching moment as a millis value.
	 * @param timezone
	 * @param millis
	 * @return The next matching moment as a millis value.
	 */
	public long next(TimeZone timezone, long millis)
	{
		long result = -1;
		final GregorianCalendar gc = new GregorianCalendar(timezone);
		for (int i = 0; i < _matcherSize; ++i)
		{
			long next = -1;
			gc.setTimeInMillis(millis);
			gc.set(13, 0);
			gc.set(14, 0);
			if (_weekOfYearAdder.containsKey(i))
			{
				gc.add(3, _weekOfYearAdder.get(i));
			}
			if (_dayOfYearAdder.containsKey(i))
			{
				gc.add(6, _dayOfYearAdder.get(i));
			}
			if (_hourAdder.containsKey(i))
			{
				gc.add(10, _hourAdder.get(i));
			}
			final ValueMatcher minuteMatcher = _minuteMatchers.get(i);
			final ValueMatcher hourMatcher = _hourMatchers.get(i);
			final ValueMatcher dayOfMonthMatcher = _dayOfMonthMatchers.get(i);
			final ValueMatcher monthMatcher = _monthMatchers.get(i);
			final ValueMatcher dayOfWeekMatcher = _dayOfWeekMatchers.get(i);
			
			SEARCH: do
			{
				final int year = gc.get(1);
				final boolean isLeapYear = gc.isLeapYear(year);
				for (int month = gc.get(2) + 1; month <= MONTH_MAX_VALUE; ++month)
				{
					if (monthMatcher.match(month))
					{
						gc.set(2, month - 1);
						final int maxDayOfMonth = DayOfMonthValueMatcher.getLastDayOfMonth(month, isLeapYear);
						for (int dayOfMonth = gc.get(5); dayOfMonth <= maxDayOfMonth; ++dayOfMonth)
						{
							if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher ? ((DayOfMonthValueMatcher) dayOfMonthMatcher).match(dayOfMonth, month, isLeapYear) : dayOfMonthMatcher.match(dayOfMonth))
							{
								gc.set(5, dayOfMonth);
								final int dayOfWeek = gc.get(DAY_OF_WEEK_MAX_VALUE) - 1;
								if (dayOfWeekMatcher.match(dayOfWeek))
								{
									for (int hour = gc.get(11); hour <= HOUR_MAX_VALUE; ++hour)
									{
										if (hourMatcher.match(hour))
										{
											gc.set(11, hour);
											for (int minute = gc.get(MONTH_MAX_VALUE); minute <= MINUTE_MAX_VALUE; ++minute)
											{
												if (!minuteMatcher.match(minute))
												{
													continue;
												}
												
												gc.set(MONTH_MAX_VALUE, minute);
												final long next0 = gc.getTimeInMillis();
												if (next0 <= millis)
												{
													continue;
												}
												
												if ((next != -1) && (next0 >= next))
												{
													break SEARCH;
												}
												
												next = next0;
												if (_hourAdderRnd.containsKey(i))
												{
													next += Rnd.get(_hourAdderRnd.get(i)) * 60 * 60 * 1000;
												}
												
												if (!_minuteAdderRnd.containsKey(i))
												{
													break SEARCH;
												}
												
												next += Rnd.get(_minuteAdderRnd.get(i)) * 60 * 1000;
												break SEARCH;
											}
										}
										gc.set(MONTH_MAX_VALUE, 0);
									}
								}
							}
							gc.set(11, 0);
							gc.set(MONTH_MAX_VALUE, 0);
						}
					}
					gc.set(5, 1);
					gc.set(11, 0);
					gc.set(MONTH_MAX_VALUE, 0);
				}
				gc.set(2, 0);
				gc.set(11, 0);
				gc.set(MONTH_MAX_VALUE, 0);
				gc.roll(1, true);
			}
			
			while (true);
			if ((next <= millis) || ((result != -1) && (next >= result)))
			{
				continue;
			}
			
			result = next;
		}
		return result;
	}
	
	/**
	 * It returns the next matching moment as a long.
	 * @param millis
	 * @return The next matching moment as a long.
	 */
	public long next(long millis)
	{
		return next(TimeZone.getDefault(), millis);
	}
	
	/**
	 * Returns the pattern as a string.
	 * @return The pattern as a string.
	 */
	@Override
	public String toString()
	{
		return _asString;
	}
	
	/**
	 * This utility method changes an alias to an int value.
	 * @param value The value.
	 * @param aliases The aliases list.
	 * @param offset The offset appplied to the aliases list indices.
	 * @return The parsed value.
	 * @throws Exception If the expressed values doesn't match any alias.
	 */
	protected static int parseAlias(String value, String[] aliases, int offset) throws Exception
	{
		for (int i = 0; i < aliases.length; i++)
		{
			if (aliases[i].equalsIgnoreCase(value))
			{
				return offset + i;
			}
		}
		throw new Exception("invalid alias \"" + value + "\"");
	}
	
	/**
	 * <p>
	 * A ValueMatcher whose rules are in a plain array of integer values. When asked to validate a value, this ValueMatcher checks if it is in the array and, if not, checks whether the last-day-of-month setting applies.
	 * </p>
	 * @author Paul Fernley
	 */
	private static class DayOfMonthValueMatcher extends IntArrayValueMatcher
	{
		private static final int[] LAST_DAYS =
		{
			31,
			28,
			31,
			30,
			31,
			30,
			31,
			31,
			30,
			31,
			30,
			31
		};
		
		/**
		 * Builds the ValueMatcher.
		 * @param integers An ArrayList of Integer elements, one for every value accepted by the matcher. The match() method will return true only if its parameter will be one of this list or the last-day-of-month setting applies.
		 */
		public DayOfMonthValueMatcher(List<Integer> integers)
		{
			super(integers);
		}
		
		/**
		 * Returns true if the given value is included in the matcher list or the last-day-of-month setting applies.
		 * @param value
		 * @param month
		 * @param isLeapYear
		 * @return
		 */
		public boolean match(int value, int month, boolean isLeapYear)
		{
			return (super.match(value) || ((value > 27) && match(32) && isLastDayOfMonth(value, month, isLeapYear)));
		}
		
		public static int getLastDayOfMonth(int month, boolean isLeapYear)
		{
			if (isLeapYear && (month == 2))
			{
				return 29;
			}
			
			return LAST_DAYS[month - 1];
		}
		
		public static boolean isLastDayOfMonth(int value, int month, boolean isLeapYear)
		{
			return value == getLastDayOfMonth(month, isLeapYear);
		}
	}
	
	/**
	 * It returns the next matching moment as a long.
	 * @return The next matching moment as a long.
	 */
	public long getDelayToNextFromNow()
	{
		return next(System.currentTimeMillis()) - System.currentTimeMillis();
	}
	
	public long getOffsettedDelayToNextFromNow(int offsetInMinutes)
	{
		return Math.max(0, getDelayToNextFromNow() - TimeUnit.MINUTES.toMillis(offsetInMinutes));
	}
	
	public String getNextAsFormattedDateString()
	{
		return TimeUtil.getDateTimeString(next(System.currentTimeMillis()));
	}
	
	/**
	 * <p>
	 * A ValueMatcher whose rules are in a plain array of integer values. When asked to validate a value, this ValueMatcher checks if it is in the array.
	 * </p>
	 * @author Carlo Pelliccia
	 */
	private static class IntArrayValueMatcher implements ValueMatcher
	{
		/**
		 * The accepted values.
		 */
		private final int[] _values;
		
		/**
		 * Builds the ValueMatcher.
		 * @param integers a List of Integer elements, one for every value accepted by the matcher. The match() method will return true only if its parameter will be one of this list.
		 */
		public IntArrayValueMatcher(List<Integer> integers)
		{
			final int size = integers.size();
			_values = new int[size];
			for (int i = 0; i < size; i++)
			{
				try
				{
					_values[i] = integers.get(i).intValue();
				}
				catch (Exception e)
				{
					throw new IllegalArgumentException(e.getMessage());
				}
			}
		}
		
		/**
		 * Returns true if the given value is included in the matcher list.
		 */
		@Override
		public boolean match(int value)
		{
			for (int i = 0; i < _values.length; i++)
			{
				if (_values[i] == value)
				{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * This ValueMatcher always returns true!
	 * @author Carlo Pelliccia
	 */
	protected static class AlwaysTrueValueMatcher implements ValueMatcher
	{
		/**
		 * Always true!
		 */
		@Override
		public boolean match(int value)
		{
			return true;
		}
	}
	
	/**
	 * <p>
	 * This interface describes the ValueMatcher behavior. A ValueMatcher is an object that validate an integer value against a set of rules.
	 * </p>
	 * @author Carlo Pelliccia
	 */
	private static interface ValueMatcher
	{
		/**
		 * Validate the given integer value against a set of rules.
		 * @param value The value.
		 * @return true if the given value matches the rules of the ValueMatcher, false otherwise.
		 */
		public boolean match(int value);
	}
	
	/**
	 * The value parser for the day of week field.
	 */
	private static class DayOfWeekValueParser extends SimpleValueParser
	{
		/**
		 * Days of week aliases.
		 */
		private static String[] ALIASES =
		{
			"sun",
			"mon",
			"tue",
			"wed",
			"thu",
			"fri",
			"sat"
		};
		
		/**
		 * Builds the day value parser.
		 */
		public DayOfWeekValueParser()
		{
			super(DAY_OF_WEEK_MIN_VALUE, DAY_OF_WEEK_MAX_VALUE);
		}
		
		@Override
		public int parse(String value) throws Exception
		{
			try
			{
				// try as a simple value
				return super.parse(value) % 7;
			}
			catch (Exception e)
			{
				// try as an alias
				return parseAlias(value, ALIASES, 0);
			}
		}
	}
	
	/**
	 * The value parser for the months field.
	 */
	private static class MonthValueParser extends SimpleValueParser
	{
		/**
		 * Months of year aliases.
		 */
		private static String[] ALIASES = new String[]
		{
			"jan",
			"feb",
			"mar",
			"apr",
			"may",
			"jun",
			"jul",
			"aug",
			"sep",
			"oct",
			"nov",
			"dec"
		};
		
		/**
		 * Builds the months value parser.
		 */
		public MonthValueParser()
		{
			super(MONTH_MIN_VALUE, MONTH_MAX_VALUE);
		}
		
		@Override
		public int parse(String value) throws Exception
		{
			try
			{
				return super.parse(value);
			}
			catch (Exception e)
			{
				return parseAlias(value, ALIASES, 1);
			}
		}
	}
	
	/**
	 * The value parser for the day of month field.
	 */
	private static class DayOfMonthValueParser extends SimpleValueParser
	{
		/**
		 * Builds the value parser.
		 */
		public DayOfMonthValueParser()
		{
			super(DAY_OF_MONTH_MIN_VALUE, DAY_OF_MONTH_MAX_VALUE);
		}
		
		@Override
		public int parse(String value) throws Exception
		{
			if (value.equalsIgnoreCase("L"))
			{
				return 32;
			}
			return super.parse(value);
		}
	}
	
	/**
	 * The value parser for the hour field.
	 */
	private static class HourValueParser extends SimpleValueParser
	{
		/**
		 * Builds the value parser.
		 */
		public HourValueParser()
		{
			super(HOUR_MIN_VALUE, HOUR_MAX_VALUE);
		}
	}
	
	/**
	 * The minutes value parser.
	 */
	private static class MinuteValueParser extends SimpleValueParser
	{
		/**
		 * Builds the value parser.
		 */
		public MinuteValueParser()
		{
			super(MINUTE_MIN_VALUE, MINUTE_MAX_VALUE);
		}
	}
	
	/**
	 * A simple value parser.
	 */
	private static class SimpleValueParser implements ValueParser
	{
		/**
		 * The minimum allowed value.
		 */
		protected int _minValue;
		
		/**
		 * The maximum allowed value.
		 */
		protected int _maxValue;
		
		/**
		 * Builds the value parser.
		 * @param minValue The minimum allowed value.
		 * @param maxValue The maximum allowed value.
		 */
		public SimpleValueParser(int minValue, int maxValue)
		{
			_minValue = minValue;
			_maxValue = maxValue;
		}
		
		@Override
		public int parse(String value) throws Exception
		{
			int i;
			try
			{
				i = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				throw new Exception("invalid integer value");
			}
			if ((i < _minValue) || (i > _maxValue))
			{
				throw new Exception("value out of range");
			}
			return i;
		}
		
		@Override
		public int getMinValue()
		{
			return _minValue;
		}
		
		@Override
		public int getMaxValue()
		{
			return _maxValue;
		}
	}
	
	/**
	 * Definition for a value parser.
	 */
	private static interface ValueParser
	{
		/**
		 * Attempts to parse a value.
		 * @param value The value.
		 * @return The parsed value.
		 * @throws Exception If the value can't be parsed.
		 */
		public int parse(String value) throws Exception;
		
		/**
		 * Returns the minimum value accepted by the parser.
		 * @return The minimum value accepted by the parser.
		 */
		public int getMinValue();
		
		/**
		 * Returns the maximum value accepted by the parser.
		 * @return The maximum value accepted by the parser.
		 */
		public int getMaxValue();
	}
}
