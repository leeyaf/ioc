package com.gsteam.common.util.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
	public String path() default "";
	
	public enum METHOD {GET,POST}
	
	METHOD method() default METHOD.GET;
}
