package cn.guoxy.mate.common.exception;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author GuoXiaoyong
 */
public class BusinessException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Object... args) {
    this(format(message, args));
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessException(Throwable cause, String message, Object... args) {
    super(format(message, args), cause);
  }

  private static String format(String format, Object... args) {
    format = format.replaceAll("\\{}", "%s");
    return String.format(format, args);
  }
}
