package cn.guoxy.mate.security;

import java.io.Serial;
import org.springframework.security.core.AuthenticationException;

/**
 * 承载令牌格式错误身份验证异常
 *
 * @see AuthenticationException
 * @author Guo XiaoYong
 */
public class BearerTokenAuthenticationException extends AuthenticationException {
  @Serial private static final long serialVersionUID = 1L;

  public BearerTokenAuthenticationException(String msg) {
    super(msg);
  }

  public BearerTokenAuthenticationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
