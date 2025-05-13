package cn.turbo.bot.base.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置
 *
 * @author huke
 * @date 2025/2/11 20:12
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Lists.newArrayList("*"));
        configuration.setAllowedMethods(Lists.newArrayList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Lists.newArrayList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }


}
