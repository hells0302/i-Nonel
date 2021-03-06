package com.study.inovel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dnw on 2017/4/1.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //添加小说表创建语句
        sqLiteDatabase.execSQL("create table if not exists novel_link("
                + "id integer primary key autoincrement,"
                + "novel_name text,"
                + "url text)");
        //小说详情链接表
        sqLiteDatabase.execSQL("create table if not exists novel_info_link("
                + "id integer primary key autoincrement,"
                + "novel_name text,"
                + "url text)");
        //小说更新缓存表
        sqLiteDatabase.execSQL("create table if not exists novel_cache("
                + "id integer primary key autoincrement,"
                + "novel_name text,"
                + "author text,"
                + "updateTitle text,"
                + "updateTime text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch(i)
        {
            case 1:
                sqLiteDatabase.execSQL("create table if not exists novel_info_link("
                        + "id integer primary key autoincrement,"
                        + "novel_name text,"
                        + "url text)");
                sqLiteDatabase.execSQL("create table if not exists novel_cache("
                        + "id integer primary key autoincrement,"
                        + "novel_name text,"
                        + "author text,"
                        + "updateTitle text,"
                        + "updateTime text)");
            case 2:
                sqLiteDatabase.execSQL("create table if not exists novel_cache("
                        + "id integer primary key autoincrement,"
                        + "novel_name text,"
                        + "author text,"
                        + "updateTitle text,"
                        + "updateTime text)");
                default:
        }
    }
}
