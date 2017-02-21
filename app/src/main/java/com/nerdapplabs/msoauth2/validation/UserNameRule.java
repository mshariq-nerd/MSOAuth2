package com.nerdapplabs.msoauth2.validation;

import com.mobsandgeeks.saripaar.AnnotationRule;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohd. Shariq on 21/02/17.
 */

public class UserNameRule extends AnnotationRule<UserName, String> {

    /*
    * http://stackoverflow.com/questions/1559751/
    * regex-to-make-sure-that-the-string-contains-at-least-one-lower-case-char-upper
    */
    private final Map<UserName.Scheme, String> SCHEME_PATTERNS =
            new HashMap<UserName.Scheme, String>() {{
                put(UserName.Scheme.ANY, ".+");
                put(UserName.Scheme.ALPHA, "\\w+");
                put(UserName.Scheme.ALPHA_MIXED_CASE, "(?=.*[a-z])(?=.*[A-Z]).+");
                put(UserName.Scheme.NUMERIC, "\\d+");
                put(UserName.Scheme.ALPHA_NUMERIC, "(?=.*[a-zA-Z])(?=.*[\\d]).+");
                put(UserName.Scheme.ALPHA_NUMERIC_MIXED_CASE,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+");
                put(UserName.Scheme.ALPHA_NUMERIC_SYMBOLS,
                        "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                put(UserName.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+");
            }};

    protected UserNameRule(final UserName userName) {
        super(userName);
    }

    @Override
    public boolean isValid(final String userName) {
        boolean hasMinChars = userName.length() >= mRuleAnnotation.min();
        boolean matchesScheme = userName.matches(SCHEME_PATTERNS.get(mRuleAnnotation.scheme()));
        return hasMinChars && matchesScheme;
    }


}