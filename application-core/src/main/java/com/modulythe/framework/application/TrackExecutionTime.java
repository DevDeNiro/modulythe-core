package com.modulythe.framework.application;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Define on a class annotated with @Aspect to intercept methods annotated
 * with @TrackExecutionTime and measure their duration
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackExecutionTime {
}
