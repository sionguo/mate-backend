package cn.guoxy.mate.board;

import cn.guoxy.mate.data.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 板
 *
 * @see BaseEntity
 * @author Guo XiaoYong
 */
@Entity
@Table(name = "board")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Board extends BaseEntity {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * 名称
   *
   * @see String
   */
  private String name;

  /**
   * 内容
   *
   * @see String
   */
  private String content;

  /**
   * 缩略图
   *
   * @see String
   */
  private String thumbnail;
}
