package cn.guoxy.mate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * rest身份验证失败处理程序
 *
 * @see AuthenticationFailureHandler
 * @author Guo XiaoYong
 */
@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
  private final ObjectMapper objectMapper;

  public RestAuthenticationFailureHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    Map<String, Object> result = new HashMap<>();
    result.put("msg", exception.getMessage());
    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(result));
  }
}
