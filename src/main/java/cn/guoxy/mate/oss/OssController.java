package cn.guoxy.mate.oss;

import cn.guoxy.mate.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * oss控制器
 *
 * @author Guo XiaoYong
 */
@Controller
@Tag(name = "oss", description = "对象存储服务")
public class OssController {
  private final OssOperations ossOperations;

  public OssController(OssOperations ossOperations) {
    this.ossOperations = ossOperations;
  }

  @Operation(summary = "上传文件", description = "通过本接口可以上传文件到服务器")
  @PostMapping(
      value = {"/upload"},
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileItem> upload(
      @Parameter(description = "文件") @RequestPart MultipartFile file) {
    FileItem upload = null;
    try {
      upload =
          ossOperations.upload(
              file.getInputStream(), file.getOriginalFilename(), file.getContentType());
    } catch (IOException e) {
      throw new BusinessException(e, "上传文件失败");
    }
    return ResponseEntity.ok(upload);
  }

  @Operation(summary = "下载文件", description = "通过本接口可以从服务器下载文件")
  @GetMapping(
      value = {"/download"},
      name = "下载文件")
  public ResponseEntity<Void> downloadFile(
      @Parameter(description = "文件ID") @RequestParam String fileItemId,
      HttpServletResponse response) {

    // 下载文件
    try (DownloadResponse stream = ossOperations.download(fileItemId);
        ServletOutputStream outputStream = response.getOutputStream()) {
      if (stream == null) {
        return ResponseEntity.notFound().build();
      }
      response.setContentType(stream.getContentType());
      response.setHeader(
          HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\""
              + URLEncoder.encode(stream.getFileName(), StandardCharsets.UTF_8)
              + "\"; filename*="
              + StandardCharsets.UTF_8.name()
              + "'zh_cn'"
              + URLEncoder.encode(stream.getFileName(), StandardCharsets.UTF_8));
      IOUtils.copy(stream, outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.ok().build();
  }
}
