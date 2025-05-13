package cn.turbo.bot.base.util;

import cn.turbo.bot.base.common.NumberConst;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 时间 工具类
 *
 * @author huke
 * @date 2025/2/9 15:10
 */
public class SmartLocalDateUtil {

    /**
     * 格式化 LocalDateTime 返回对应格式字符串
     *
     * @param time
     * @param formatterEnum {@link SmartDateFormatterEnum}
     * @return
     */
    public static String format(LocalDateTime time, SmartDateFormatterEnum formatterEnum) {
        if (null == time) {
            return null;
        }
        return time.format(formatterEnum.getFormatter());
    }

    /**
     * 格式化 LocalDate返回对应格式字符串
     *
     * @param date
     * @param formatterEnum {@link SmartDateFormatterEnum}
     * @return
     */
    public static String format(LocalDate date, SmartDateFormatterEnum formatterEnum) {
        if (null == date) {
            return null;
        }
        return date.format(formatterEnum.getFormatter());
    }

    public static String format(LocalTime time, SmartDateFormatterEnum formatterEnum) {
        if (null == time) {
            return null;
        }
        return time.format(formatterEnum.getFormatter());
    }

    public static String nowChineseDate() {
        return format(LocalDate.now(), SmartDateFormatterEnum.CHINESE_YMD);
    }

    public static String now() {
        return format(LocalDateTime.now(), SmartDateFormatterEnum.YMD_HMS);
    }

    public static String nowDate() {
        return format(LocalDate.now(), SmartDateFormatterEnum.YMD);
    }

    /**
     * 解析时间字符串 返回LocalDateTime
     *
     * @param time
     * @param formatterEnum {@link SmartDateFormatterEnum}
     * @return
     */
    public static LocalDateTime parse(String time, SmartDateFormatterEnum formatterEnum) {
        return LocalDateTime.parse(time, formatterEnum.getFormatter());
    }

    /**
     * 解析时间字符串 返回 LocalDate
     *
     * @param time
     * @param formatterEnum {@link SmartDateFormatterEnum}
     * @return
     */
    public static LocalDate parseDate(String time, SmartDateFormatterEnum formatterEnum) {
        return LocalDate.parse(time, formatterEnum.getFormatter());
    }

    /**
     * 获取指定日期时间戳
     *
     * @param time
     * @return
     */
    public static Long getTimestamp(LocalDateTime time) {
        return time.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 获取当前时间戳(秒)
     *
     * @return
     */
    public static long nowSecond() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 将时间格式化为 星期几，例：星期一 ... 星期日
     *
     * @param localDate
     * @return
     */
    public static String formatToChineseWeek(LocalDate localDate) {
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE);
    }

    /**
     * 将时间格式化为 周几，例：周一 ... 周日
     *
     * @param localDate
     * @return
     */
    public static String formatToChineseWeekZhou(LocalDate localDate) {
        return formatToChineseWeek(localDate).replace("星期", "周");
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 获取当天剩余时间 单位
     *
     * @param unit 时间单位
     * @return
     */
    public static Long getDayBalanceTime(ChronoUnit unit) {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, now.plusDays(1L).with(LocalTime.MIN)).get(unit);
    }

    /**
     * 转long
     *
     * @param localDateTime
     * @return
     */
    public static Long getLongTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将毫秒数转换为秒数。
     *
     * @return
     */
    public static long currentSecond() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * 13位时间戳 转 localTime
     *
     * @param longTime
     * @return
     */
    public static LocalDateTime timestamp2LocalDateTime(Long longTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(longTime), ZoneId.systemDefault());
    }

    /**
     * 获取年龄
     *
     * @param birthday
     * @return
     */
    public static Integer getAge(LocalDate birthday) {
        if (null == birthday) {
            return null;
        }
        LocalDate now = LocalDate.now();
        return birthday.until(now).getYears();
    }

    /**
     * 获取周时间段的每一天 k-> 星期名称 v-> 日期
     *
     * @return
     */
    public static Map<String, LocalDate> getWeekEveryDay(LocalDate startDate, LocalDate endDate) {
        Map<String, LocalDate> result = new HashMap();
        while (endDate.isAfter(startDate) || endDate.isEqual(startDate)) {
            result.put(formatToChineseWeek(startDate), startDate);
            startDate = startDate.plusDays(NumberConst.LONG_0);
        }
        return result;
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static LocalDate getThisWeekMonday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date date = cal.getTime();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static LocalDate getThisWeekSunday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date date = cal.getTime();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取上周的第一天
     *
     * @return
     * @throws Exception
     */
    public static LocalDate getPreviousWeekMonday(LocalDate date) {
        return date.minusDays(getThisDayValue(date) + 6);
    }

    /**
     * 获取上周的最后一天
     *
     * @return
     */
    public static LocalDate getPreviousWeekSunday(LocalDate date) {
        return date.minusDays(getThisDayValue(date));
    }

    /**
     * 获取当天的值
     *
     * @return
     */
    public static int getThisDayValue(LocalDate date) {
        return date.getDayOfWeek().getValue();
    }

    /**
     * 根据传入的日期获取上一天的日期
     *
     * @param date
     * @return
     */
    public static LocalDate getYesterday(LocalDate date) {
        return date.minusDays(1);
    }

    /**
     * 根据传入的日期获取周一的时间
     *
     * @param date
     * @return
     */
    public static LocalDate getWeekStart(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }

    /**
     * 根据传入的日期获取当月的1号日期
     *
     * @param date
     * @return
     */
    public static LocalDate getMonthStart(LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();
        return date.minusDays(dayOfMonth - 1);
    }

    public static void main(String[] args) {
    }
}
