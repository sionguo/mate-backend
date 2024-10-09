package cn.guoxy.mate.data;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 基础实体
 *
 * @see Serializable
 * @author Guo XiaoYong
 */
@Data
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  @Id @Tsid private String id;
  @CreatedDate private Instant createStamp;
  @CreatedBy private String createBy;
  @LastModifiedDate private Instant lastModifiedStamp;
  @LastModifiedBy private String lastModifiedBy;
}
