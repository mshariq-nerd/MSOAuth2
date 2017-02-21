package com.nerdapplabs.msoauth2.validation;

import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Mohd. Shariq on 21/02/17.
 */

@ValidateUsing(UserNameRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UserName {
    int min()           default 3;
    UserName.Scheme scheme()     default UserName.Scheme.ANY;

    int sequence()      default -1;
    int messageResId()  default -1;
    String message()    default "Invalid UserName";

    enum Scheme {
        ANY, ALPHA, ALPHA_MIXED_CASE,
        NUMERIC, ALPHA_NUMERIC, ALPHA_NUMERIC_MIXED_CASE,
        ALPHA_NUMERIC_SYMBOLS, ALPHA_NUMERIC_MIXED_CASE_SYMBOLS
    }
}
