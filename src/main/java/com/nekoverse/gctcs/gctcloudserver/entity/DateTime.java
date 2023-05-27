package com.nekoverse.gctcs.gctcloudserver.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class DateTime extends java.util.Date {
    @Deprecated(since = "1.2")
    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        super(year, month, day, hour, minute, second);
    }

    public DateTime(long date) {
        super(date);
    }

    public void setTime(long time) {
        super.setTime(time);
    }

    public static DateTime valueOf(String s) {
        if (s == null) {
            throw new java.lang.IllegalArgumentException();
        }
        final int YEAR_LENGTH = 4;
        final int MONTH_LENGTH = 2;
        final int DAY_LENGTH = 2;
        final int MAX_MONTH = 12;
        final int MAX_DAY = 31;

        int firstDash = s.indexOf('-');
        int secondDash = s.indexOf('-', firstDash + 1);
        int firstColon = s.indexOf(':');
        int secondColon = s.indexOf(':', firstColon + 1);
        int len = s.length();

        int year;
        int month;
        int day;
        if ((firstDash > 0) && (secondDash > 0) && (secondDash < len - 1)) {
            if (firstDash == YEAR_LENGTH &&
                    (secondDash - firstDash > 1 && secondDash - firstDash <= MONTH_LENGTH + 1) &&
                    (len - secondDash > 1 && len - secondDash <= DAY_LENGTH + 1)) {
                int _year = Integer.parseInt(s, 0, firstDash, 10);
                int _month = Integer.parseInt(s, firstDash + 1, secondDash, 10);
                int _day = Integer.parseInt(s, secondDash + 1, len, 10);

                if ((_month >= 1 && _month <= MAX_MONTH) && (_day >= 1 && _day <= MAX_DAY)) {
                    year = _year - 1900;
                    month = _month - 1;
                    day = _day;

                    int hour;
                    int minute;
                    int second;
                    if (firstColon > 0 && secondColon > 0 &&
                            secondColon < len - 1) {
                        hour = Integer.parseInt(s, 0, firstColon, 10);
                        minute = Integer.parseInt(s, firstColon + 1, secondColon, 10);
                        second = Integer.parseInt(s, secondColon + 1, len, 10);

                        return new DateTime(year, month, day, hour, minute, second);

                    } else {
                        throw new java.lang.IllegalArgumentException();
                    }
                } else {
                    throw new java.lang.IllegalArgumentException();
                }
            } else {
                throw new java.lang.IllegalArgumentException();
            }
        } else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    @SuppressWarnings("deprecation")
    public String toString() {
        int year = super.getYear() + 1900;
        int month = super.getMonth() + 1;
        int day = super.getDate();
        int hour = super.getHours();
        int minute = super.getMinutes();
        int second = super.getSeconds();

        char buf[] = new char[19];
        formatDecimalInt(year, buf, 0, 4);
        buf[4] = '-';
        DateTime.formatDecimalInt(month, buf, 5, 2);
        buf[7] = '-';
        DateTime.formatDecimalInt(day, buf, 8, 2);
        buf[10] = ' ';
        DateTime.formatDecimalInt(hour, buf, 11, 2);
        buf[13] = ':';
        DateTime.formatDecimalInt(minute, buf, 14, 2);
        buf[16] = ':';
        DateTime.formatDecimalInt(second, buf, 17, 2);

        return new String(buf);
    }

    static void formatDecimalInt(int val, char[] buf, int offset, int len) {
        int charPos = offset + len;
        do {
            buf[--charPos] = (char) ('0' + (val % 10));
            val /= 10;
        } while (charPos > offset);
    }

    public static DateTime valueOf(LocalDateTime dt) {
        return new DateTime(dt.getYear() - 1900, dt.getMonthValue() - 1,
                dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }

    @SuppressWarnings("deprecation")
    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(LocalDate.of(getYear() + 1900, getMonth() + 1, getDate()),
                LocalTime.of(getHours(), getMinutes(), getSeconds()));
    }

    @Override
    public Instant toInstant() {
        throw new java.lang.UnsupportedOperationException();
    }
}
