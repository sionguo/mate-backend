package cn.guoxy.mate.oss;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class OssCreationFailureAnalyzer extends AbstractFailureAnalyzer<OssBeanCreationException> {

  @Override
  protected FailureAnalysis analyze(Throwable rootFailure, OssBeanCreationException cause) {

    return new FailureAnalysis(cause.getMessage(), "set application.minio.endpoint", cause);
  }
}
