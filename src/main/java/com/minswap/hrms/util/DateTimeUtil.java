package com.minswap.hrms.util;

import static com.minswap.hrms.constants.CommonConstant.YYYY_MM_DD_HH_MM_SS;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static LocalDate convertStringToLocalDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            log.error("Getting error when parse String: {} to LocalDate", date);
            return null;
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime date) {
        try {
            return date.format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
        } catch (Exception e) {
            log.error("Getting error when parse String: {} to LocalDate", date);
            return null;
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTime, String formatter) {
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(formatter));
        } catch (Exception e) {
            log.error("Getting error when parse String: {} to LocalDateTime", dateTime);
            return null;
        }
    }

    public static Instant convertStringToInstant(String dateTime, String formatter) {
        try {
        	LocalDateTime localDateTime = convertStringToLocalDateTime(dateTime, formatter);
            return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            log.error("Getting error when parse String: {} to Instant", dateTime);
            return null;
        }
    }

    public static String convertInstantToString(Instant instant, String formatter) {
        try {
        	LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return localDateTime.format(DateTimeFormatter.ofPattern(formatter));
        } catch (Exception e) {
            log.error("Getting error when convert Instant: {} to String", instant);
            return null;
        }
    }

    public static LocalDateTime convertToLocalDateTime(Long time) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        } catch (Exception e) {
            log.error("Getting error when convert Local Date Time: {} to String", time);
            return null;
        }
    }
}
