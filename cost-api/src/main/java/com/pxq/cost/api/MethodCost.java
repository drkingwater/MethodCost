package com.pxq.cost.api;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一种约束，用来标记要统计耗时的方法
 * author : pxq
 * date : 19-9-22 下午3:36
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MethodCost {

}
