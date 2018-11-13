/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by edward.lo on 2017/3/17.
 */

public class Const {
	public static final long TIME_MS_SEC = 1000;
	public static final long TIME_MS_MIN = 60 * TIME_MS_SEC;
	public static final long TIME_MS_HOUR = 60 * TIME_MS_MIN;
	public static final long TIME_MS_DAY = 24 * TIME_MS_HOUR;
	public static final long TIME_MS_WEEK = 7 * TIME_MS_DAY;

	public static final String TIMELINE_EVENT_LP = "learningPeriod";
	public static final SimpleDateFormat TIMELINE_DATE_FORMAT =
//			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);//2017-02-20T19:19:05.234410974Z
//           new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);//2017-02-20T19:19:05.234Z
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);//2017-02-20T19:19:05Z
//	static{
//		TIMELINE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
//	}

}
