package cn.turbo.bot.base.module.ai.siliconflow.constant;

/**
 * SiliconFlow 常量
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
public class SiliconFlowConst {

    public static final String BASE_URL = "https://api.siliconflow.cn/v1";

    /**
     * TODO 配置 按需可选
     * SiliconFlow api key 自行填写
     */
    public static final String KEY = "sk-xxxx";

    public static final String MODEL = "deepseek-ai/DeepSeek-V3";

    public static final String MODEL_R1 = "deepseek-ai/DeepSeek-R1";

    /**
     * 每分钟请求数
     */
    public static final double REQUESTS_PER_MINUTE = 1000;
}
