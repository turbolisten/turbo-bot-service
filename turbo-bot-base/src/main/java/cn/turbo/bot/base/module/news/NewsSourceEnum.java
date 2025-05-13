package cn.turbo.bot.base.module.news;

import cn.turbo.bot.base.common.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资讯来源 枚举类
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
@Getter
@AllArgsConstructor
public enum NewsSourceEnum implements BaseEnum {

    /**
     * chat
     */
    TOU_TIAO("toutiao", "今日头条"),

    WEI_BO("weibo", "微博"),

    ZHI_HU("zhihu", "知乎"),

    TIE_BA("tieba", "贴吧"),

    JUE_JIN("juejin", "稀土掘金"),

    ;

    private final String value;

    private final String desc;
}
