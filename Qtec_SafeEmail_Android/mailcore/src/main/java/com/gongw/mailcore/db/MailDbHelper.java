package com.gongw.mailcore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gongw on 2018/7/20.
 */

public class MailDbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "mail";
    public static final String TABLE_FOLDER_INFO = "folder_info";
    public static final String TABLE_MESSAGE = "message";
    public static final String TABLE_ATTACHMENT = "attachment";


    public MailDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
