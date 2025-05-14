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
package org.l2jpx.commons.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @author UnAfraid
 */
public class TimeUtil
{
	private static int findIndexOfNonDigit(CharSequence text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			if (Character.isDigit(text.charAt(i)))
			{
				continue;
			}
			return i;
		}
		return -1;
	}
	
	/**
	 * Parses patterns like:
	 * <ul>
	 * <li>1min or 10mins</li>
	 * <li>1day or 10days</li>
	 * <li>1week or 4weeks</li>
	 * <li>1month or 12months</li>
	 * <li>1year or 5years</li>
	 * </ul>
	 * @param datePattern
	 * @return {@link Duration} object converted by the date pattern specified.
	 * @throws IllegalStateException when malformed pattern specified.
	 */
	public static Duration parseDuration(String datePattern)
	{
		final int index = findIndexOfNonDigit(datePattern);
		if (index == -1)
		{
			throw new IllegalStateException("Incorrect time format given: " + datePattern);
		}
		try
		{
			final int val = Integer.parseInt(datePattern.substring(0, index));
			final String type = datePattern.substring(index);
			final ChronoUnit unit;
			switch (type.toLowerCase())
			{
				case "sec":
				case "secs":
				{
					unit = ChronoUnit.SECONDS;
					break;
				}
				case "min":
				case "mins":
				{
					unit = ChronoUnit.MINUTES;
					break;
				}
				case "hour":
				case "hours":
				{
					unit = ChronoUnit.HOURS;
					break;
				}
				case "day":
				case "days":
				{
					unit = ChronoUnit.DAYS;
					break;
				}
				case "week":
				case "weeks":
				{
					unit = ChronoUnit.WEEKS;
					break;
				}
				case "month":
				case "months":
				{
					unit = ChronoUnit.MONTHS;
					break;
				}
				case "year":
				case "years":
				{
					unit = ChronoUnit.YEARS;
					break;
				}
				default:
				{
					unit = ChronoUnit.valueOf(type);
					if (unit == null)
					{
						throw new IllegalStateException("Incorrect format: " + type + " !!");
					}
				}
			}
			return Duration.of(val, unit);
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Incorrect time format given: " + datePattern + " val: " + datePattern.substring(0, index));
		}
	}
	
	public static Calendar getCloseNextDay(int dayOfWeek, int hour, int minute)
	{
		final Calendar calendar = Calendar.getInstance(); // Today, now
		if (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek)
		{
			calendar.add(Calendar.DAY_OF_MONTH, ((dayOfWeek + 7) - calendar.get(Calendar.DAY_OF_WEEK)) % 7);
		}
		else
		{
			final int minOfDay = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
			if (minOfDay >= ((hour * 60) + minute))
			{
				calendar.add(Calendar.DAY_OF_MONTH, 7); // Bump to next week
			}
		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	/**
	 * Formats the given date using the specified format.
	 * @param date the date to format
	 * @param format the format pattern to use
	 * @return a string representation of the formatted date, or {@code null} if the date is {@code null}
	 */
	public static String formatDate(Date date, String format)
	{
		return date == null ? null : (new SimpleDateFormat(format)).format(date);
	}
	
	/**
	 * Formats the given date to a string representation in the "dd/MM/yyyy" format.
	 * @param date the date to format
	 * @return a string representation of the formatted date
	 */
	public static String getDateString(Date date)
	{
		return formatDate(date, "dd/MM/yyyy");
	}
	
	/**
	 * Formats the given date to a string representation in the "dd/MM/yyyy HH:mm:ss" format.
	 * @param date the date to format
	 * @return a string representation of the formatted date
	 */
	public static String getDateTimeString(Date date)
	{
		return formatDate(date, "dd/MM/yyyy HH:mm:ss");
	}
	
	/**
	 * Formats the date represented by the given milliseconds since the epoch to a string representation in the "dd/MM/yyyy" format.
	 * @param timeMillis the milliseconds since the epoch to format
	 * @return a string representation of the formatted date
	 */
	public static String getDateString(long timeMillis)
	{
		return getDateString(new Date(timeMillis));
	}
	
	/**
	 * Formats the date and time represented by the given milliseconds since the epoch to a string representation in the "dd/MM/yyyy HH:mm:ss" format.
	 * @param timeMillis the milliseconds since the epoch to format
	 * @return a string representation of the formatted date and time
	 */
	public static String getDateTimeString(long timeMillis)
	{
		return getDateTimeString(new Date(timeMillis));
	}
}
