package cn.guoxy.mate.oss;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 迷你房产
 *
 * @author Guo XiaoYong
 */
@ConfigurationProperties(prefix = "application.minio")
@Data
public class MinioProperties {
  private String endpoint;
  private String accessKey;
  private String secretKey;
  private String bucket = "mate-oss";
  private Duration connectTimeout = Duration.ofSeconds(10);

  private Duration writeTimeout = Duration.ofSeconds(60);

  private Duration readTimeout = Duration.ofSeconds(10);
}
