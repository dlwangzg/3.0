/**
 *
 */
package com.leadingsoft.bizfuse.quartz.core.annotition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Job的注解。
 *
 * @author liuyg
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JobMapping {
    /** Job id */
    String id() default "";

    /** 要执行的方法名，仅对类注解生效 */
    String method() default "";

    /** 是否允许并发执行 */
    boolean allowConcurrent() default true;

    /** 要执行的节点约束 */
    String enabledNode() default "";
}
