package cn.turbo.bot.base.module.mail;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.turbo.bot.base.common.CommonConst;

/**
 * 邮件业务
 *
 * @author huke
 * @date 2025/2/9 23:00
 */
public class MailService {

    private static final MailAccount account;

    static {
        account = new MailAccount();
        account.setHost("smtp.163.com");
        account.setPort(465);
        account.setAuth(true);
        account.setSslEnable(true);
        account.setFrom(CommonConst.DEV_EMAIL);
        // TODO 配置 按需可选
        account.setUser("turbohub");
        account.setPass("xxxx");
    }

    /**
     * 发送邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param isHtml
     */
    public static void send(String to, String subject, String content, boolean isHtml) {
        MailUtil.send(account, to, subject, content, isHtml, null);
    }

    public static void main(String[] args) {
        MailService.send("turbohub@163.com", "测试邮件", "<p>测试邮件</p>", true);
    }
}
