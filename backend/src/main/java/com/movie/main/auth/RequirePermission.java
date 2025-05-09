package com.movie.main.auth;

import com.movie.main.entity.Employee.Permission;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({
        ElementType.METHOD, ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    Permission value();
}
