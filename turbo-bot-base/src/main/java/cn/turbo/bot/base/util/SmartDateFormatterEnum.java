package cn.turbo.bot.base.util;

import java.time.format.DateTimeFormatter;

/**
 * 时间格式化 枚举类
 *
 * @author huke
 * @date 2025/2/9 15:11
 */
public enum SmartDateFormatterEnum {

    /**
     * 日期格式 ：年月日 yyyy-MM-dd
     * 例：2021-10-15
     */
    YMD(DateTimeFormatter.ofPattern("yyyy-MM-dd")),

    /**
     * 日期格式 ：年月日 yyyy/MM/dd
     * 例：2021/10/15
     */
    YMD_SLASH(DateTimeFormatter.ofPattern("yyyy/MM/dd")),

    CHINESE_YMD(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),

    CHINESE_YMD_HM(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),

    /**
     * 日期格式 ：年月日 yyyyMMdd
     * 例：20211015
     */
    YMD_NO_SEPARATOR(DateTimeFormatter.ofPattern("yyyyMMdd")),

    /**
     * 日期格式 ：年月日 时分 yyyy-MM-dd HH:mm
     * 例：2021-10-15 10:15
     */
    YMD_HM(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),

    /**
     * 日期格式 ：年月日 时分秒 yyyy-MM-dd HH:mm:ss
     * 例：2021-10-15 10:15:00
     */
    YMD_HMS(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),

    /**
     * 日期格式 ：年月 yyyy-MM
     * 例：2021-10
     */
    YM(DateTimeFormatter.ofPattern("yyyy-MM")),

    /**
     * 日期格式：年月 MM-dd
     * 例：10-15
     */
    MD(DateTimeFormatter.ofPattern("MM-dd")),

    /**
     * 日期格式：年月 MM月dd日
     * 例：10月15日
     */
    CHINESE_MD(DateTimeFormatter.ofPattern("MM月dd日")),

    /**
     * 日期格式 ： yyyyMMddHHmmss
     * 例：20091225091010
     */
    YMDHMS(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),

    /**
     * 日期格式 ：时分秒 HH:mm:ss
     * 例：10:15:00
     */
    HMS(DateTimeFormatter.ofPattern("HH:mm:ss")),

    /**
     * 日期格式 ：时分 HH:mm
     * 例：10:15
     */
    HM(DateTimeFormatter.ofPattern("HH:mm")),

    /**
     * 日期格式 ：月.日 M.d
     * 例：10:15
     */
    MD_POINT(DateTimeFormatter.ofPattern("M.d")),

    /**
     * 日期格式 ：M月d日
     * 例：10:15
     */
    MD_CHINESE(DateTimeFormatter.ofPattern("M月d日")),

    /**
     * 日期格式 : 月日 M-dd
     * 例：11月21日 12:14
     */
    CHINESE_MD_HM(DateTimeFormatter.ofPattern("M月dd日 HH:mm")),

    ;

    private final DateTimeFormatter formatter;

    SmartDateFormatterEnum(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
