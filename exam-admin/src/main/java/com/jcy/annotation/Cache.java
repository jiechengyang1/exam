package com.jcy.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    String prefix() default "";

    // spel表达式
    String suffix() default "";

    long ttl() default 300;

    // 每个ttl后加随机的过期时间, 防止缓存雪崩
    int randomTime() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 如果为true则执行目标方法后, 重设cache
     * 这个目标方法必须返回**需要缓存的数据**
     */
    boolean resetCache() default false;
}
