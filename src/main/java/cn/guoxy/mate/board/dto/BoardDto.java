package cn.guoxy.mate.board.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;

/**
 * 板数据传输对象
 *
 * @author Guo XiaoYong
 */
@Data
public class BoardDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String id;
  private String title;
  private String content;
  private String thumbnail;
  private Instant createStamp;
  private Instant lastModifiedStamp;
}
