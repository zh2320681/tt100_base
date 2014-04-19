package com.tt100.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
//@AnnotationDefault(@DatabaseTable(tableName=""))
public @interface DatabaseTable
{
   String tableName();
}
