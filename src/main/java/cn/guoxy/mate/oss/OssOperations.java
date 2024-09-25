package cn.guoxy.mate.oss;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * oss操作
 *
 * @author Guo XiaoYong
 */
public interface OssOperations {

  FileItem upload(InputStream input, String filename, String contentType);

  DownloadResponse download(String fileItemId);

  void delete(String fileItemId);
}
