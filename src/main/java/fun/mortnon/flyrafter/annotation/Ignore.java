package fun.mortnon.flyrafter.annotation;

import java.lang.annotation.*;

/**
 * 忽略字段或你类，不用于生成数据表字段
 * Ignore 主要用于标识了 Entity 类的父类，如果用于标识了 Entity 的子类，无效
 *
 * @author Moon Wu
 * @date 2021/5/10
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
