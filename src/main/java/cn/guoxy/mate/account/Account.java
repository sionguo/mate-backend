package cn.guoxy.mate.account;

import cn.guoxy.mate.data.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 用户
 *
 * @see BaseEntity
 * @author Guo XiaoYong
 */
@Entity
@Table(name = "account")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
  @Serial private static final long serialVersionUID = 1L;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String email;
  private Boolean accountExpired;
  private Boolean accountLocked;
  private Boolean enabled;
  private List<String> role;
}
