package cn.guoxy.mate.data;

import cn.guoxy.mate.common.tsid.Tsid;
import java.util.EnumSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

/**
 * tsid发电机
 *
 * @see BeforeExecutionGenerator
 * @author Guo XiaoYong
 */
public class TsidGenerator implements BeforeExecutionGenerator {

  @Override
  public Object generate(
      SharedSessionContractImplementor session,
      Object owner,
      Object currentValue,
      EventType eventType) {
    return Tsid.fast().toString();
  }

  @Override
  public EnumSet<EventType> getEventTypes() {
    return EventTypeSets.INSERT_ONLY;
  }
}
