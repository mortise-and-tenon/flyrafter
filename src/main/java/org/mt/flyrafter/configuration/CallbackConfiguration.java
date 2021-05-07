package org.mt.flyrafter.configuration;

import lombok.extern.slf4j.Slf4j;
import org.mt.flyrafter.FlyRafter;
import org.mt.flyrafter.FlyRafterBuilder;
import org.mt.flyrafter.callback.FlyRafterCallback;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 向 Spring Boot 添加 Flyway 的 Callback 配置
 * 并触发 FlyRafter 自动生成 SQL
 *
 * @author Moon Wu
 * @date 2021/04/22
 */
@Configuration
@Slf4j
public class CallbackConfiguration {
    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer(FlyRafterConfiguration flyRafterConfiguration, DataSource dataSource) {
        joinFlyway(flyRafterConfiguration, dataSource);
        return configuration -> configuration.callbacks(flyRafterCallback());
    }

    @Bean
    public FlyRafterCallback flyRafterCallback() {
        return new FlyRafterCallback();
    }

    private void joinFlyway(FlyRafterConfiguration flyRafterConfiguration, DataSource dataSource) {
        if (!flyRafterConfiguration.isEnabled()) {
            return;
        }

        log.info("startup flyrafter.");
        FlyRafter flyRafter = new FlyRafterBuilder(flyRafterConfiguration, dataSource).build();
        flyRafter.startup();
    }
}
