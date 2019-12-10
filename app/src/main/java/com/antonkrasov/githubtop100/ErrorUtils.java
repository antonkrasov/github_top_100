package com.antonkrasov.githubtop100;

import java.net.UnknownHostException;

public class ErrorUtils {

    // We need to use resources here, I know :)
    public static String getHumanReadableText(Throwable ex) {
        if (ex instanceof UnknownHostException) {
            return "Cannot access GitHub.com,\nplease check your Internet connection.";
        }

        return "Unknown error";
    }

}
