package cn.guoxy.mate.oss;

import java.io.FilterInputStream;
import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;

/**
 * 下载响应
 *
 * @author GuoXiaoyong
 */
@Getter
public class DownloadResponse extends FilterInputStream {
  private final String fileId;
  @Setter private String fileName;
  @Setter private String md5;
  @Setter private String contentType = "application/octet-stream";

  /**
   * Creates a {@code FilterInputStream} by assigning the argument {@code in} to the field {@code
   * this.in} so as to remember it for later use.
   *
   * @param in the underlying input stream, or {@code null} if this instance is to be created
   *     without an underlying stream.
   */
  public DownloadResponse(InputStream in, String fileId) {
    super(in);
    this.fileId = fileId;
  }
}
