package fun.mortnon.flyrafter.annotation;

import java.lang.annotation.*;

/**
 * Ignore the filed for table column.
 * @author Moon Wu
 * @date 2021/5/10
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
