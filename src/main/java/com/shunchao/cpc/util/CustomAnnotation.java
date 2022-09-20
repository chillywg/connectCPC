package com.shunchao.cpc.util;

import java.lang.annotation.*;

/**
 * @author djlcc
 * @title: TmsveAnnotation
 * @projectName jeecg-boot-parent
 * @description: TODO 自定义注解
 * @date 2022/4/13 13:42
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CustomAnnotation {

    String value() default "";
}
