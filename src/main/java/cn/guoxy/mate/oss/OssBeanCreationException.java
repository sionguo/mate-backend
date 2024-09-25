package cn.guoxy.mate.oss;

import java.io.Serial;
import org.springframework.beans.factory.BeanCreationException;

/**
 * oss bean创建异常
 *
 * @see BeanCreationException
 * @author Guo XiaoYong
 */
public class OssBeanCreationException extends BeanCreationException {
  @Serial private static final long serialVersionUID = 1L;

  public OssBeanCreationException(String beanName, String msg) {
    super(beanName, msg);
  }

  public OssBeanCreationException(String beanName, String msg, Throwable cause) {
    super(beanName, msg, cause);
  }

  public OssBeanCreationException(String msg) {
    super(msg);
  }

  public OssBeanCreationException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public OssBeanCreationException(String resourceDescription, String beanName, String msg) {
    super(resourceDescription, beanName, msg);
  }

  public OssBeanCreationException(
      String resourceDescription, String beanName, String msg, Throwable cause) {
    super(resourceDescription, beanName, msg, cause);
  }
}
