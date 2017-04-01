package com.study.inovel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.study.inovel.bean.Book;
import com.study.inovel.util.HtmlParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnw on 2017/4/1.
 */
public class DatabaseUtil {
    public static final String DB_NAME="novel.db";
    public static final int VERSION=1;
    private SQLiteDatabase db;
    private static DatabaseUtil databaseUtil;
    public DatabaseUtil(Context context)
    {
        DatabaseHelper dbHelper=new DatabaseHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }
    public synchronized static DatabaseUtil getInstance(Context context)
    {
        if(databaseUtil==null)
        {
            databaseUtil=new DatabaseUtil(context);
        }
        return databaseUtil;
    }
    public boolean addLinkToDatabase(String novelName,String url)
    {
        ContentValues values=new ContentValues();
        values.put("novel_name",novelName);
        values.put("url",url);
        db.insert("novel_link",null,values);
        return true;
    }
    public int getNovelLinkCount()
    {
        String sql="select count(*) from novel_link";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToFirst();
        int count=(int)cursor.getLong(0);
        cursor.close();
        return count;
    }
    public List<String> getNovelLinkElement()
    {
        List<String> list=new ArrayList<>();
        Cursor cursor=db.query("novel_link",null,null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                String url=cursor.getString(cursor.getColumnIndex("url"));
                list.add(url);
            }while(cursor.moveToNext());
        }
        return list;
    }
    public boolean isExist(String novelName)
    {
        Cursor cursor=db.query("novel_link",null,"novel_name=?",new String[]{novelName},null,null,null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        return false;
    }
}
