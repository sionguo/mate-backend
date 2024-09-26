package cn.guoxy.mate.account.dto;

import cn.guoxy.mate.account.Account;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

/**
 * 登录用户
 *
 * @see UserDetails
 * @author Guo XiaoYong
 */
@Data
public class LoginUser implements UserDetails {
  @Serial private static final long serialVersionUID = 1L;
  private String id;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String email;
  private Boolean accountExpired;
  private Boolean accountLocked;
  private Boolean enabled;
  private List<String> role;

  public static LoginUser from(Account account) {
    LoginUser loginUser = new LoginUser();
    loginUser.setId(account.getId());
    loginUser.setUsername(account.getUsername());
    loginUser.setPassword(account.getPassword());
    loginUser.setFirstName(account.getFirstName());
    loginUser.setLastName(account.getLastName());
    loginUser.setEmail(account.getEmail());
    loginUser.setAccountExpired(BooleanUtils.isTrue(account.getAccountExpired()));
    loginUser.setAccountLocked(BooleanUtils.isTrue(account.getAccountLocked()));
    loginUser.setEnabled(BooleanUtils.isTrue(account.getEnabled()));
    loginUser.setRole(CollectionUtils.isEmpty(account.getRole()) ? List.of() : account.getRole());
    return loginUser;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.stream().map(SimpleGrantedAuthority::new).toList();
  }

  @Override
  public boolean isAccountNonExpired() {
    return !BooleanUtils.isTrue(getAccountExpired());
  }

  @Override
  public boolean isAccountNonLocked() {
    return !BooleanUtils.isTrue(getAccountLocked());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return BooleanUtils.isTrue(getEnabled());
  }
}
