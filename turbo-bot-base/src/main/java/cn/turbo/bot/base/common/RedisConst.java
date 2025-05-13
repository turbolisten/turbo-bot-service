package cn.turbo.bot.base.common;

/**
 * redis 常量
 *
 * @author huke
 * @date 2025/2/11 19:50
 */
public class RedisConst {

    public static final String PROJECT = "wx-bot";

    public static class Key {

        public static final String AI_CHAT_SESSION = PROJECT + ":ai-chat-session:";

    }

    public static class Lock {

        private static final String LOCK = PROJECT + ":lock:";

        public static final String BOT_MSG_HANDLE = LOCK + "bot-msg-handle:";

        public static final String REMINDER_HANDLE = LOCK + "reminder-handle:";

        public static final String ADD_USER = LOCK + "add-user:";
    }

    public static class Limiter {

        private static final String LIMITER = PROJECT + ":limiter:";

        public static final String WEBHOOK_KEY = LIMITER + "webhook-key:";

    }

    public static class Topic {

        private static final String TOPIC = PROJECT + ":topic:";

        public static final String CACHE_TOPIC = TOPIC + "cache_reload";

    }

    public static class Queue {

        private static final String QUEUE = PROJECT + ":queue:";

        public static final String WX_BOT_MSG_DELAYED_QUEUE = QUEUE + "wx_bot_msg_delayed_queue";

    }
}
