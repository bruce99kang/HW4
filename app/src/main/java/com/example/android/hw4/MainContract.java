package com.example.android.hw4;

import android.provider.BaseColumns;

public class MainContract {

    public static final class MainEntry implements BaseColumns{
        public static final String TABLE_NAME = "class";
        public static final String COLUMN_CLASS_NAME = "className";
        public static final String COLUMN_CLASS_DAY = "classDay";
        public static final String COLUMN_CLASS_TIME = "classTime";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
