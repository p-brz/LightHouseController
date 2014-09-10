package com.example.lighthousecontroller;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampPlotFormat extends Format {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4093517540928881647L;
	// create a simple date format that draws on the year portion of our timestamp.
	// see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
	// for a full description of SimpleDateFormat.
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
	    // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
	    // we multiply our timestamp by 1000:
	    long timestamp = ((Number) obj).longValue();
	    Date date = new Date(timestamp);
	    return dateFormat.format(date, toAppendTo, pos);
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
	    return null;

	}
}