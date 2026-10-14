/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CardAttr {

    public static final String NO_DEFAULT_VALUE = "CMDBUILD_ORM_NO_DEFAULT_VALUE", ANY = "CM_ANY";

    String value() default "";

    String defaultValue() default NO_DEFAULT_VALUE;

    boolean readFromDb() default true;

    boolean writeToDb() default true;

    boolean ignore() default false;

    EmbeddedReference embedded() default EmbeddedReference.NONE;

    enum EmbeddedReference {
        NONE, ALWAYS, MIXED
    }
}
