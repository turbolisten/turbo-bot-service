package cn.turbo.bot.base.module.wxbot.basic.constant;

import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WxEmojiEnum implements BaseEnum {

    /**
     * 表情 - 庆祝
     */
    QING_ZHU("\uD83C\uDF89", "庆祝"),

    JIANG_BEI("\uD83C\uDFC6", "奖杯"),

    TIAO_SHENG("[跳绳]", "跳绳"),

    SHOU_ZHI_XIA("\uD83D\uDC47", "手指下"),

    HUO_MIAO("\uD83D\uDD25", "火苗"),

    QI_ZHI("\uD83C\uDFC1", "旗帜"),

    TAI_YANG("[太阳]", "太阳"),

    XIAN_WEN("[献吻]", "献吻"),

    LUAN_WU("[乱舞]", "乱舞"),

    ZHUAN_QUAN("[转圈]", "转圈"),

    TIAO_TIAO("[跳跳]", "跳跳"),

    FA_DOU("[发抖]", "发抖"),

    JI_DONG("[激动]", "激动"),

    YU_KUAI("[愉快]", "愉快"),

    WO_SHOU("[握手]", "握手"),

    JI("\uD83D\uDC24", "小鸡"),

    HU_TOU("\uD83D\uDC2F", "虎头"),

    NAN_GUA_TOU("\uD83C\uDF83", "南瓜头"),

    HUO_JIAN("\uD83D\uDE80", "火箭"),

    SHAN_DIAN("[闪电]", "闪电"),

    QI_QIU("\uD83C\uDF88", "红气球"),

    SHANG("⬆", "上"),
    XIA("⬇", "下"),
    ZUO("⬅", "左"),
    YOU("➡", "右"),
    HOUSE("\uD83C\uDFE0", "房屋"),
    PLAY("▶", "播放"),
    RAINBOW("\uD83C\uDF08", "彩虹"),

    QI_PAO("\uD83D\uDCAC", "气泡"),

    GE_BO_JI_ROU("\uD83D\uDCAA", "胳膊肌肉"),

    STAR("\uD83C\uDF1F", "星星"),

    WARN_LIGHT("\uD83D\uDEA8", "警示灯"),

    UFO("\uD83D\uDEF8", "UFO"),

    NUM_1("1️⃣", "1"),
    NUM_2("2️⃣", "2"),
    NUM_3("3️⃣", "3"),
    NUM_4("4️⃣", "4"),
    NUM_5("5️⃣", "5"),
    NUM_6("6️⃣", "6"),
    NUM_7("7️⃣", "7"),
    NUM_8("8️⃣", "8"),
    NUM_9("9️⃣", "9"),
    NUM_10("🔟", "10"),

    ;

    private final String value;

    private final String desc;

    public static WxEmojiEnum getByNum(int num) {
        switch (num) {
            case 1:
                return NUM_1;
            case 2:
                return NUM_2;
            case 3:
                return NUM_3;
            case 4:
                return NUM_4;
            case 5:
                return NUM_5;
            case 6:
                return NUM_6;
            case 7:
                return NUM_7;
            case 8:
                return NUM_8;
            case 9:
                return NUM_9;
            case 10:
                return NUM_10;
            default:
                return STAR;
        }
    }
}

