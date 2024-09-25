package cn.guoxy.mate.data;

import cn.guoxy.mate.security.MethodContext;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据配置
 *
 * @author Guo XiaoYong
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "cn.guoxy")
@EnableTransactionManagement
public class DataConfiguration {
  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> Optional.ofNullable(MethodContext.getCurrentUser());
  }
}
