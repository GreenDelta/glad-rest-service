package com.greendelta.search.glad.rest.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface IndexField {

	IndexType type();

	String defaultValue() default "";

	boolean required() default false;

	boolean aggregatable() default false;

}