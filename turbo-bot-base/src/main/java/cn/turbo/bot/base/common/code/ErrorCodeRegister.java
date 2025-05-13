package cn.turbo.bot.base.common.code;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ClassUtil;
import cn.turbo.bot.base.common.StringConst;

import java.util.*;

/**
 * 注册code状态码
 *
 * @author huke
 * @date 2024/4/17 23:48
 */
public class ErrorCodeRegister {

    public static void init() {

    }

    static {
        // 校验code
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(StringConst.EMPTY_STR, ErrorCode.class);
        register(classSet);
        System.out.println("------------- ErrorCodeRegister Complete -------------");
    }

    /**
     * 校验是否重复
     *
     * @param classSet
     */
    private static void register(Set<Class<?>> classSet) {
        if (CollUtil.isEmpty(classSet)) {
            return;
        }
        Map<Class<? extends ErrorCode>, Pair<Integer, Integer>> codeRangeMap = new HashMap<>(10);
        for (Class<?> aClass : classSet) {
            // 校验是否重复 范围是否冲突
            Object[] enumConstants = aClass.getEnumConstants();
            ErrorCode firstErrorCode = (ErrorCode) enumConstants[0];
            String simpleName = aClass.getSimpleName();
            boolean containsKey = codeRangeMap.containsKey(aClass);
            if (containsKey) {
                throw new ExceptionInInitializerError(String.format("ErrorCode error: Enum %s already exist !", simpleName));
            }
            // 校验 开始结束值 是否越界
            int[] range = firstErrorCode.range();
            int start = range[0];
            int end = range[1];
            codeRangeMap.forEach((k, v) -> {
                if (isExistOtherRange(start, end, v)) {
                    throw new IllegalArgumentException(String.format("ErrorCode error: %s[%d,%d] has intersection with class:%s[%d,%d]", simpleName, start, end,
                                                                     k.getSimpleName(), v.getKey(), v.getValue()));
                }
            });

            // 校验code
            List<Integer> codeList = new ArrayList<>();
            for (Object enumConstant : enumConstants) {
                ErrorCode errorCode = (ErrorCode) enumConstant;
                int code = errorCode.getCode();
                if (code < start || code > end) {
                    throw new IllegalArgumentException(String.format("ErrorCode error: %s[%d,%d] code %d out of range", simpleName, start, end, code));
                }
                if (codeList.contains(code)) {
                    throw new IllegalArgumentException(String.format("ErrorCode error: %s[%d] code repeat", simpleName, code));
                }
                codeList.add(code);
            }
            codeRangeMap.put(firstErrorCode.getClass(), Pair.of(start, end));
        }
    }

    /**
     * 是否存在于其他范围
     *
     * @param start
     * @param end
     * @param range
     * @return
     */
    private static boolean isExistOtherRange(int start, int end, Pair<Integer, Integer> range) {
        if (start >= range.getKey() && start <= range.getValue()) {
            return true;
        }
        if (end >= range.getKey() && end <= range.getValue()) {
            return true;
        }
        return false;
    }
}
