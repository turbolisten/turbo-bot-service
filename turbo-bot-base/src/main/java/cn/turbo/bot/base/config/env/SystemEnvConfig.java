package cn.turbo.bot.base.config.env;

import cn.hutool.core.util.StrUtil;
import cn.turbo.bot.base.util.SmartEnumUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 系统环境
 *
 * @date 2021/08/13 18:56
 */
@Configuration
public class SystemEnvConfig {

    @Value("${spring.profiles.active}")
    private String systemEnvironment;

    @Value("${spring.application.name}")
    private String projectName;

    private static final String PROFILES_ACTIVE = "spring.profiles.active";

    @Bean
    public SystemEnvDTO initEnvironment() {
        SystemEnvEnum currentEnvironment = SmartEnumUtil.getEnumByValue(systemEnvironment, SystemEnvEnum.class);
        if (currentEnvironment == null) {
            throw new ExceptionInInitializerError("无法获取当前环境！请在 application.yaml 配置参数：spring.profiles.active");
        }
        if (StrUtil.isBlank(projectName)) {
            throw new ExceptionInInitializerError("无法获取当前项目名称！请在 application.yaml 配置参数：spring.application.name");
        }
        return new SystemEnvDTO(currentEnvironment == SystemEnvEnum.PROD, projectName, currentEnvironment);
    }

    /**
     * 测试环境
     */
    public static class IsTest implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            String property = conditionContext.getEnvironment().getProperty(PROFILES_ACTIVE);
            return StrUtil.isNotBlank(property) && SystemEnvEnum.SIT.equalsValue(property);
        }
    }

    /**
     * 测试环境
     */
    public static class IsTestOrDev implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            String property = conditionContext.getEnvironment().getProperty(PROFILES_ACTIVE);
            return StrUtil.isNotBlank(property) && (SystemEnvEnum.DEV.equalsValue(property) || SystemEnvEnum.SIT.equalsValue(property));
        }
    }
}
