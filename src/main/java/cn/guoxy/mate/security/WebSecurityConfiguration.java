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
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class WebSecurityConfiguration {
  private final OpaqueTokenIntrospector opaqueTokenIntrospector;

  WebSecurityConfiguration(OpaqueTokenIntrospector opaqueTokenIntrospector) {
    this.opaqueTokenIntrospector = opaqueTokenIntrospector;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            smc -> {
              smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
        .authorizeHttpRequests(
            arc -> {
              arc.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .oauth2ResourceServer(rs -> rs.opaqueToken(o -> o.introspector(this.introspector())));

    http.addFilterAfter(new MethodContextFilter(), SecurityContextHolderAwareRequestFilter.class);

    return http.build();
  }

  private OpaqueTokenIntrospector introspector() {
    return new ZitadelAuthorityOpaqueTokenIntrospector(opaqueTokenIntrospector);
  }
}
