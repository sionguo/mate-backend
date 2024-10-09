package cn.guoxy.mate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * 春季文档配置
 *
 * @author GuoXiaoyong
 */
@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Springdoc OAS3.0 - Mate Application - RESTful API",
            description = "Springdoc OAS3.0 - Mate Application - RESTful API",
            version = "1.0.0"),
    security = {
      @SecurityRequirement(
          name = "OAuth2 Flow",
          scopes = {"openid", "profile", "email", "phone", "address", "offline_access"})
    })
@SecurityScheme(
    name = "OAuth2 Flow",
    type = SecuritySchemeType.OAUTH2,
    flows =
        @OAuthFlows(
            authorizationCode =
                @OAuthFlow(
                    authorizationUrl = "${springdoc.swagger-ui.oauth.authorization-url}",
                    tokenUrl = "${springdoc.swagger-ui.oauth.token-url}",
                    scopes = {
                      @OAuthScope(name = "openid"),
                      @OAuthScope(name = "profile"),
                      @OAuthScope(name = "email"),
                      @OAuthScope(name = "phone"),
                      @OAuthScope(name = "address"),
                      @OAuthScope(name = "offline_access"),
                    })),
    description = "OAuth2授权码认证流程，<br/>根据需要选择下方的Scopes。")
public class SpringDocConfiguration {}
