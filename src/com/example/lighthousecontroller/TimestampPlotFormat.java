package com.example.lighthousecontroller;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampPlotFormat extends Format {
	private static final long serialVersionUID = 4093517540928881647L;
	
	private DateFormat dateFormat;
	
	public TimestampPlotFormat() {
		dateFormat = SimpleDateFormat.getTimeInstance();
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
	    // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
	    // we multiply our timestamp by 1000:
	    long timestamp = ((Number) obj).longValue();
	    Date date = new Date(timestamp);
	    return dateFormat.format(date, toAppendTo, pos);
	}

	/** @param dateTimePattern @see {@link SimpleDateFormat}*/
	@SuppressLint("SimpleDateFormat")
	public void setTimestampFormat(String dateTimePattern){
		dateFormat = new SimpleDateFormat(dateTimePattern);
	}
	
	@Override
	public Object parseObject(String source, ParsePosition pos) {
	    return null;

	}
}