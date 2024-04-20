package com.l2jpx.commons.logging.formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;

import com.l2jpx.commons.logging.MasterFormatter;

public class ConsoleLogFormatter extends MasterFormatter
{
	@Override
	public String format(LogRecord record)
	{
		final StringWriter sw = new StringWriter();
		sw.append(record.getMessage());
		sw.append(CRLF);
		
		final Throwable throwable = record.getThrown();
		if (throwable != null)
			throwable.printStackTrace(new PrintWriter(sw));
		
		return sw.toString();
	}
}