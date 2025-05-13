package cn.turbo.bot.wx;

import cn.turbo.bot.base.listener.ApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.turbo.bot.base", "cn.turbo.bot.wx"})
public class WxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxApplication.class, args);
        System.out.println(ApplicationListener.SYSTEM_PRINT_INFO);
    }

}
