package com.backend.apiserver.utils;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeUtils {
	public static LocalDateTime fromCurrentTimeMillis(long currentTimeMillis) {
		return Instant.ofEpochMilli(currentTimeMillis).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
	}

	public static long toCurrentTimeMillis(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		return zonedDateTime.toInstant().toEpochMilli();
	}

	public static int minuteToHour(int minutes) {
		return Math.round((float) minutes / 60);
	}
}
