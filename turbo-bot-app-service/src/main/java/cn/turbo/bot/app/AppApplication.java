package cn.turbo.bot.app;

import cn.turbo.bot.base.listener.ApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"cn.turbo.bot.base", "cn.turbo.bot.app"})
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
		System.out.println(ApplicationListener.SYSTEM_PRINT_INFO);
	}

}
