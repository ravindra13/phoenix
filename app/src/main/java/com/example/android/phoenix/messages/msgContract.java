package com.example.android.phoenix.messages;

import android.net.Uri;
import android.provider.BaseColumns;

public class msgContract {


    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.farcry";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "places" directory
    public static final String MSG_PLACES = "messages";

    public static final class messageentry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MSG_PLACES).build();

        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_PIC = "pic";
        public static final String COLUMN_TIME = "time";
    }

}
