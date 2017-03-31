package com.sequencing.androidoauth.helper;

/**
 * Created by omazurova on 3/6/2017.
 */

public class ValidationHelper {

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
