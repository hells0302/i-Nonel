package com.study.inovel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.study.inovel.bean.Book;
import com.study.inovel.bean.CacheBook;
import com.study.inovel.util.HtmlParserUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 添加想要跟踪的小说到数据库
     * @param novelName
     * @param url
     * @return
     */
    public boolean addLinkToDatabase(String novelName,String url)
    {
        ContentValues values=new ContentValues();
        values.put("novel_name",novelName);
        values.put("url",url);
        db.insert("novel_link",null,values);
        return true;
    }

    /**
     * 获取到的小说详情链接存储到数据库
     * @param novelName
     * @param url
     * @return
     */
    public boolean addLinkToNovelInfoLink(String novelName,String url)
    {
        ContentValues values=new ContentValues();
        values.put("novel_name",novelName);
        values.put("url",url);
        db.insert("novel_info_link",null,values);
        return true;
    }

    /**
     * 缓存到数据库
     * @param novelName
     * @param author
     * @param update
     * @param time
     * @return
     */
    public boolean cacheToNovelCache(String novelName,String author,String update,String time)
    {
        ContentValues values=new ContentValues();
        values.put("novel_name",novelName);
        values.put("author",author);
        values.put("updateTitle",update);
        values.put("updateTime",time);
        db.insert("novel_cache",null,values);
        return true;
    }

    /**
     * 获取所添加的小说数量
     * @return
     */
    public int getNovelLinkCount()
    {
        String sql="select count(*) from novel_link";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToFirst();
        int count=(int)cursor.getLong(0);
        cursor.close();
        return count;
    }

    /**
     * 取小说详情链接的数量
     * @return
     */
    public int getNovelInfoLinkCount()
    {
        String sql="select count(*) from novel_info_link";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToFirst();
        int count=(int)cursor.getLong(0);
        cursor.close();
        return count;
    }

    /**
     * 获取添加的小说list
     * @return
     */
    public List<Map<String,String>> getNovelLinkElement()
    {
        List<Map<String,String>> list=new ArrayList<>();
        Map<String,String> map;
        Cursor cursor=db.query("novel_link",null,null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                map=new HashMap<>();
                String url=cursor.getString(cursor.getColumnIndex("url"));
                String name=cursor.getString(cursor.getColumnIndex("novel_name"));
                map.put(name,url);
                list.add(map);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 获取获取的小说详情链接list
     * @return
     */
    public List<String> getNovelInfoLinkElement()
    {
        List<String> list=new ArrayList<>();
        Cursor cursor=db.query("novel_info_link",null,null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                String url=cursor.getString(cursor.getColumnIndex("url"));
                list.add(url);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 获取添加的小说链接
     * @return
     */
    public List<String> getNovelNameElement()
    {
        List<String> list=new ArrayList<>();
        Cursor cursor=db.query("novel_link",null,null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                String url=cursor.getString(cursor.getColumnIndex("novel_name"));
                list.add(url);
            }while(cursor.moveToNext());
        }
        return list;
    }
    public List<CacheBook> getNovelCacheElement()
    {
        List<CacheBook> list=new ArrayList<>();
        Cursor cursor=db.query("novel_cache",null,null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do {
                CacheBook book=new CacheBook();
                book.cacheBookName=cursor.getString(cursor.getColumnIndex("novel_name"));
                book.cacheAuthor=cursor.getString(cursor.getColumnIndex("author"));
                book.cacheUpdateTitle=cursor.getString(cursor.getColumnIndex("updateTitle"));
                book.cacheUpdateTime=cursor.getString(cursor.getColumnIndex("updateTime"));

                list.add(book);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**
     * 删除数据库中添加的小说
     * @param novelName
     * @return
     */
    public boolean delNovelLinkElement(String novelName)
    {
        int deleteRow=db.delete("novel_link","novel_name=?",new String[]{novelName});
        if(deleteRow>=0)
            return true;
        return false;
    }

    /**
     * 删除cache数据库链接
     * @return
     */
    public boolean delAllNovelCacheElement()
    {
        db.delete("novel_cache",null,null);
        return true;
    }
    public boolean delNovelInfoLinkElement()
    {
        db.delete("novel_info_link",null,null);
        return true;
    }
    /**
     * 判断小说是否存在
     * @param novelName
     * @return
     */
    public boolean isExist(String novelName)
    {
        Cursor cursor=db.query("novel_link",null,"novel_name=?",new String[]{novelName},null,null,null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 判断小说链接是否存在
     * @param novelName
     * @return
     */
    public boolean isNovelInfoExist(String novelName)
    {
        Cursor cursor=db.query("novel_info_link",null,"novel_name=?",new String[]{novelName},null,null,null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        cursor.close();
        return false;
    }
}
