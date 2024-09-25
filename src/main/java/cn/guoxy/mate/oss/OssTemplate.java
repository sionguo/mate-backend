package cn.guoxy.mate.oss;

import cn.guoxy.mate.common.exception.BusinessException;
import cn.guoxy.mate.common.tsid.Tsid;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * oss模板
 *
 * @see OssOperations
 * @author Guo XiaoYong
 */
@Service
public class OssTemplate implements OssOperations {
  private final MinioProperties minioProperties;
  private final MinioClient minioClient;
  private final FileItemRepository fileItemRepository;

  public OssTemplate(
      MinioProperties minioProperties,
      MinioClient minioClient,
      FileItemRepository fileItemRepository) {
    this.minioProperties = minioProperties;
    this.minioClient = minioClient;
    this.fileItemRepository = fileItemRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public FileItem upload(InputStream input, String filename, String contentType) {
    if (contentType == null) {
      Tika tika = new Tika();
      contentType = tika.detect(filename);
    }
    if (StringUtils.isBlank(filename)) {
      throw new IllegalArgumentException("fileName cannot be blank");
    }
    DigestInputStream inputStream = new DigestInputStream(input, DigestUtils.getMd5Digest());
    try {
      LocalDate localDate = LocalDate.now();

      String objectId =
          "%s/%s/%s"
              .formatted(localDate.getYear(), localDate.getMonthValue(), Tsid.fast().toString());
      int filesize = inputStream.available();
      PutObjectArgs putObjectArgs =
          PutObjectArgs.builder().bucket(minioProperties.getBucket()).stream(
                  inputStream, filesize, -1)
              .contentType(contentType)
              .object(objectId)
              .userMetadata(Map.of("originalName", filename))
              .build();

      minioClient.putObject(putObjectArgs);
      FileItem fileItem = new FileItem();
      fileItem.setFileName(filename);
      fileItem.setFileId(objectId);
      fileItem.setMd5(Hex.encodeHexString(inputStream.getMessageDigest().digest()));
      fileItem.setContentType(contentType);
      fileItem.setFilesize((long) filesize);
      return fileItemRepository.save(fileItem);
    } catch (IOException
        | ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      return null;
    }
  }

  @Override
  public DownloadResponse download(String fileItemId) {
    Optional<FileItem> fileItemOptional = fileItemRepository.findById(fileItemId);
    if (fileItemOptional.isEmpty()) {
      return null;
    }
    final FileItem fileItem = fileItemOptional.get();
    String fileId = fileItem.getFileId();

    try {
      GetObjectResponse getObjectResponse =
          minioClient.getObject(
              GetObjectArgs.builder().bucket(minioProperties.getBucket()).object(fileId).build());
      DownloadResponse downloadResponse = new DownloadResponse(getObjectResponse, fileId);
      downloadResponse.setContentType(fileItem.getContentType());
      downloadResponse.setFileName(fileItem.getFileName());
      downloadResponse.setMd5(fileItem.getMd5());
      return downloadResponse;
    } catch (ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidKeyException
        | InvalidResponseException
        | IOException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException e) {
      throw new BusinessException(e, "上传文件失败");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String fileItemId) {
    Optional<FileItem> fileItemOptional = fileItemRepository.findById(fileItemId);
    if (fileItemOptional.isEmpty()) {
      return;
    }
    final FileItem fileItem = fileItemOptional.get();
    String fileId = fileItem.getFileId();
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder().bucket(minioProperties.getBucket()).object(fileId).build());
    } catch (ServerException
        | InsufficientDataException
        | ErrorResponseException
        | IOException
        | NoSuchAlgorithmException
        | InvalidKeyException
        | InvalidResponseException
        | XmlParserException
        | InternalException e) {
      throw new BusinessException("删除文件失败[{}]", fileId, e);
    }
  }
}
