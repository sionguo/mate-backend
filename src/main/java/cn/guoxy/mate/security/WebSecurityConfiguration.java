package cn.guoxy.mate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

/**
 * 网络安全配置
 *
 * @author Guo XiaoYong
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfiguration {
  private final OpaqueTokenIntrospector introspector;

  public WebSecurityConfiguration(OpaqueTokenIntrospector introspector) {
    this.introspector = introspector;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers("/public/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(rs -> rs.opaqueToken(ot -> ot.introspector(this.introspector())));

    http.addFilterAfter(new MethodContextFilter(), SecurityContextHolderAwareRequestFilter.class);

    return http.build();
  }

  private OpaqueTokenIntrospector introspector() {
    return new ZitadelAuthorityOpaqueTokenIntrospector(introspector);
  }
}
