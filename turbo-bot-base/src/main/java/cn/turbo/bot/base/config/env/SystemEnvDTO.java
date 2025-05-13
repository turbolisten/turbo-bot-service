package cn.turbo.bot.base.config.env;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统环境
 *
 * @Date 2021/8/13
 */
@AllArgsConstructor
@Getter
public class SystemEnvDTO {

    /**
     * 是否位生产环境
     */
    private Boolean isProd;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 当前环境
     */
    private SystemEnvEnum env;
}
