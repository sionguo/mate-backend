package cn.guoxy.mate.common.exception;

import java.io.Serial;

/**
 * 工作流异常
 *
 * @see BusinessException
 * @author Guo XiaoYong
 */
public class WorkflowException extends BusinessException {
  @Serial private static final long serialVersionUID = 1L;

  public WorkflowException(Throwable cause, String message, Object... args) {
    super(cause, message, args);
  }

  public WorkflowException(String message) {
    super(message);
  }

  public WorkflowException(String message, Object... args) {
    super(message, args);
  }

  public WorkflowException(String message, Throwable cause) {
    super(message, cause);
  }
}
