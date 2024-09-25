package cn.guoxy.mate.security;

/**
 * 方法上下文
 *
 * @author GuoXiaoyong
 */
public class MethodContext {
  private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

  /**
   * 获取当前用户
   *
   * @return {@code Long}
   */
  public static String getCurrentUser() {
    return CURRENT_USER.get();
  }

  /**
   * 设置当前用户
   *
   * @param user 用户
   */
  public static void setCurrentUser(String user) {
    CURRENT_USER.set(user);
  }
}
