package cn.guoxy.mate;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 春季文档配置
 *
 * @author GuoXiaoyong
 */
@Configuration
public class SpringDocConfiguration {
  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    "authScheme",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .bearerFormat("JWT")
                        .scheme("bearer")))
        .info(
            new Info()
                .title("Meta Application API")
                .version("1.0.0")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .addSecurityItem(new SecurityRequirement().addList("authScheme"));
  }
}
