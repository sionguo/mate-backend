package cn.guoxy.mate.security;

import cn.guoxy.mate.account.dto.LoginUser;
import java.io.Serial;
import java.text.ParseException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 承载令牌身份验证令牌
 *
 * @see AbstractAuthenticationToken
 * @author Guo XiaoYong
 */
public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {
  @Serial private static final long serialVersionUID = 1L;
  private final LoginUser loginUser;

  public BearerTokenAuthenticationToken(LoginUser loginUser) throws ParseException {
    super(
        loginUser.getRole().stream()
            .map(String::valueOf)
            .map(SimpleGrantedAuthority::new)
            .toList());
    super.setAuthenticated(true);
    this.loginUser = loginUser;
  }

  @Override
  public Object getCredentials() {
    return this.loginUser;
  }

  @Override
  public Object getPrincipal() {
    return this.loginUser;
  }
}
