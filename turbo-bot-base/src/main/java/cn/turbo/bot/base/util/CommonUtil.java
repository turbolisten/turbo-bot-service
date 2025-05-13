package cn.turbo.bot.base.util;

import cn.hutool.crypto.digest.MD5;

/**
 * 基础 工具类
 *
 * @author huke
 * @date 2025/2/15 11:40
 */
public class CommonUtil {

    private CommonUtil() {

    }

    private static MD5 MD_5;

    /**
     * 获取 md5 懒加载
     *
     * @return
     */
    public static MD5 getMd5() {
        // 使用懒加载 处理同步
        if (null != MD_5) {
            return MD_5;
        }
        synchronized (CommonUtil.class) {
            if (null == MD_5) {
                MD_5 = MD5.create();
            }
        }
        return MD_5;
    }

}
