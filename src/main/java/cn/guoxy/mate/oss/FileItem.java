package cn.guoxy.mate.oss;

import cn.guoxy.mate.data.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 文件项
 *
 * @see BaseEntity
 * @author Guo XiaoYong
 */
@Entity
@Table(name = "FILE_ITEM")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FileItem extends BaseEntity {
  @Serial private static final long serialVersionUID = 1L;
  private String fileName;
  private String fileId;
  private String md5;
  private String contentType;
  private Long filesize;
}
