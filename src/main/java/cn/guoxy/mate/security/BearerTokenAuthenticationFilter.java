package cn.guoxy.mate.security;

import cn.guoxy.mate.account.dto.LoginUser;
import com.nimbusds.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 承载令牌身份验证过滤器
 *
 * @see OncePerRequestFilter
 * @author Guo XiaoYong
 */
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
  private static final Pattern authorizationPattern =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
  private final BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint;

  public BearerTokenAuthenticationFilter(
      BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint) {
    this.bearerTokenAuthenticationEntryPoint = bearerTokenAuthenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String token = resolveFromAuthorizationHeader(request);
    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      LoginUser loginUser = BearerTokenFactory.parseToken(token);
      BearerTokenAuthenticationToken authentication = new BearerTokenAuthenticationToken(loginUser);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (BearerTokenAuthenticationException e) {
      bearerTokenAuthenticationEntryPoint.commence(request, response, e);
      return;
    } catch (ParseException e) {
      bearerTokenAuthenticationEntryPoint.commence(
          request, response, new BearerTokenAuthenticationException("创建凭证失败"));
      return;
    }
    filterChain.doFilter(request, response);
  }

  private String resolveFromAuthorizationHeader(HttpServletRequest request) {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
      return null;
    }
    Matcher matcher = authorizationPattern.matcher(authorization);
    if (!matcher.matches()) {
      throw new BearerTokenAuthenticationException("令牌格式不正确");
    }
    return matcher.group("token");
  }
}
