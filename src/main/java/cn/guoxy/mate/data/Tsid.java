package cn.guoxy.mate.data;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.ValueGenerationType;

/**
 * TSID
 *
 * @author Guo XiaoYong
 */
@IdGeneratorType(TsidGenerator.class)
@ValueGenerationType(generatedBy = TsidGenerator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Tsid {}
