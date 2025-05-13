package cn.turbo.bot.base.common;

import java.util.Objects;

/**
 * 枚举类
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
public interface BaseEnum {

    /**
     * 获取枚举类的值
     *
     * @return Integer
     */
    Object getValue();

    /**
     * 获取枚举类的说明
     *
     * @return String
     */
    String getDesc();

    /**
     * 比较参数是否与枚举类的value相同
     *
     * @param value
     * @return boolean
     */
    default boolean equalsValue(Object value) {
        return Objects.equals(getValue(), value);
    }

    /**
     * 比较枚举类是否相同
     *
     * @param baseEnum
     * @return boolean
     */
    default boolean equals(BaseEnum baseEnum) {
        return Objects.equals(getValue(), baseEnum.getValue())
               && Objects.equals(getDesc(), baseEnum.getDesc());
    }
}
