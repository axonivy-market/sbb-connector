package com.axonivy.connector.sbb.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
	public static String convertDateToISO8601(Date date, int ZoneOffsetHours) {
		ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneOffset.ofHours(ZoneOffsetHours));

		// Format to ISO 8601 string
		return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
