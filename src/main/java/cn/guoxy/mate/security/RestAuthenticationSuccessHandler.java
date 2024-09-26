package cn.guoxy.mate.security;

import cn.guoxy.mate.account.dto.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * rest身份验证成功处理程序
 *
 * @see AuthenticationSuccessHandler
 * @author Guo XiaoYong
 */
@Component
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper objectMapper;

  public RestAuthenticationSuccessHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    Object principal = authentication.getPrincipal();
    if (principal instanceof LoginUser loginUser) {
      Map<String, Object> result = new HashMap<>();
      result.put("token", BearerTokenFactory.generateToken(loginUser));
      result.put("tokenType", "Bearer");
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(objectMapper.writeValueAsString(result));
    }
  }
}
