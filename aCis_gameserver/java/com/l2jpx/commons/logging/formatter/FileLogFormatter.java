package com.l2jpx.commons.logging.formatter;

import java.util.logging.LogRecord;

import com.l2jpx.commons.logging.MasterFormatter;

public class FileLogFormatter extends MasterFormatter
{
	@Override
	public String format(LogRecord record)
	{
		return "[" + getFormatedDate(record.getMillis()) + "]" + SPACE + record.getLevel().getName() + SPACE + record.getMessage() + CRLF;
	}
}