package cn.guoxy.mate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
  private static final String LOGIN_URL = "/api/login";
  private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;
  private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
  private final RestAccessDeniedHandler restAccessDeniedHandler;
  private final BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint;

  public WebSecurityConfiguration(
      RestAuthenticationFailureHandler restAuthenticationFailureHandler,
      RestAuthenticationSuccessHandler restAuthenticationSuccessHandler,
      RestAccessDeniedHandler restAccessDeniedHandler,
      BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint) {
    this.restAuthenticationFailureHandler = restAuthenticationFailureHandler;
    this.restAuthenticationSuccessHandler = restAuthenticationSuccessHandler;
    this.restAccessDeniedHandler = restAccessDeniedHandler;
    this.bearerTokenAuthenticationEntryPoint = bearerTokenAuthenticationEntryPoint;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(LOGIN_URL)
                    .permitAll()
                    .requestMatchers("/public/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(bearerTokenAuthenticationEntryPoint)
                    .accessDeniedHandler(restAccessDeniedHandler))
        .formLogin(
            form ->
                form.failureHandler(restAuthenticationFailureHandler)
                    .successHandler(restAuthenticationSuccessHandler)
                    .loginProcessingUrl(LOGIN_URL));

    http.addFilterAfter(new MethodContextFilter(), SecurityContextHolderAwareRequestFilter.class);
    http.addFilterBefore(
        new BearerTokenAuthenticationFilter(bearerTokenAuthenticationEntryPoint),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
