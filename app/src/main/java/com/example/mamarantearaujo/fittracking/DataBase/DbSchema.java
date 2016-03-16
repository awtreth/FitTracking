package com.example.mamarantearaujo.fittracking.DataBase;

/**
 * Database Schema to store ActivityRecords
 */
public class DbSchema {
    public static final class activityTable {
        public static final String NAME = "activityDB";

        public static final class Cols {
            public static final String activityTime = "activityTime";
            public static final String activityType = "activityType";

        }
    }
}
