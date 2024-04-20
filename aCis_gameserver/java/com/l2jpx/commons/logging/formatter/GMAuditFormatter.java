package com.l2jpx.commons.logging.formatter;

import java.util.logging.LogRecord;

import com.l2jpx.commons.logging.MasterFormatter;

public class GMAuditFormatter extends MasterFormatter
{
	@Override
	public String format(LogRecord record)
	{
		return "[" + getFormatedDate(record.getMillis()) + "]" + SPACE + record.getMessage() + CRLF;
	}
}