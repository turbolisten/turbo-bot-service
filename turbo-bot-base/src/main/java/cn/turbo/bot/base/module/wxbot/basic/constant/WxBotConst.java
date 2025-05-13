package cn.turbo.bot.base.module.wxbot.basic.constant;

/**
 * wx bot 常量
 *
 * @author huke
 * @date 2025/2/6 21:47
 */
public class WxBotConst {

    public static final String HORIZONTAL_LINE = "\n-------------------\n";

    public static class WxId {
        public static final String WX_TEAM = "weixin";

        /**
         * 文件助手
         */
        public static final String FILE_HELPER = "filehelper";
    }


    public static class Api {

        /**
         * 通用 API/v2/api
         */
        public static final String COMMON_API = "/v2/api?funcname=MagicCgi&timeout=10&wxid={}";

        /**
         * 推送登录
         */
        public static final String LOGIN_PUSH = "/v2/login/push?wxid={}";
    }

    public static class Cmd {

        public static final Integer ContactInfo = 182;
        public static final Integer SEND_TEXT_MSG = 522;

        public static final Integer SEND_AUDIO = 127;

        public static final Integer SendImage = 110;

        public static final Integer SendAppMsg = 222;

        public static final Integer GetChatMemberInfo = 551;
    }
}
