package cn.guoxy.mate.oss;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class OssConfiguration {
  @Bean
  public MinioClient minioClient(MinioProperties minioProperties) {
    if (minioProperties == null) {
      throw new OssBeanCreationException("minioProperties can not be null");
    }
    if (minioProperties.getEndpoint() == null) {
      throw new OssBeanCreationException("minioProperties endpoint can not be null");
    }

    MinioClient.Builder minioClientBuilder = MinioClient.builder();
    minioClientBuilder
        .endpoint(minioProperties.getEndpoint())
        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey());
    MinioClient minioClient = minioClientBuilder.build();
    minioClient.setTimeout(
        minioProperties.getConnectTimeout().toMillis(),
        minioProperties.getWriteTimeout().toMillis(),
        minioProperties.getReadTimeout().toMillis());
    try {
      boolean bucketExists =
          minioClient.bucketExists(
              BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
      if (!bucketExists) {
        minioClient.makeBucket(
            MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
      }
    } catch (ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | IOException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      throw new RuntimeException(e);
    }

    return minioClient;
  }
}
