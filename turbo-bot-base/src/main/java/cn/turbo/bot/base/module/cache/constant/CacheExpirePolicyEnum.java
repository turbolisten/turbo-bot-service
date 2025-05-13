package cn.turbo.bot.base.module.cache.constant;

import cn.turbo.bot.base.common.BaseEnum;
import cn.turbo.bot.base.util.SmartEnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存过期策略 枚举类
 *
 * @author huke
 * @date 2024/10/22
 */
@Getter
@AllArgsConstructor
public enum CacheExpirePolicyEnum implements BaseEnum {

    /**
     * 0 永不过期
     */
    NEVER_EXPIRE("永不过期"),

    EXPIRE_AFTER_WRITE("写入后N段时间后过期"),

    EXPIRE_AFTER_ACCESS("访问后N段时间后过期"),

    ;

    private final String desc;


    @Override
    public String getValue() {
        return this.name();
    }

    public static void main(String[] args) {
        CacheExpirePolicyEnum enumByValue = SmartEnumUtil.getEnumByValue("NEVER_EXPIRE", CacheExpirePolicyEnum.class);
        System.out.println(enumByValue);
    }
}
