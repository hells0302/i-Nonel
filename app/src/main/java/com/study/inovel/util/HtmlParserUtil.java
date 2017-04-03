package com.study.inovel.util;

import android.util.Log;
import android.widget.Toast;

import com.study.inovel.app.App;
import com.study.inovel.bean.Book;
import com.study.inovel.bean.CacheBook;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by dnw on 2017/3/31.
 */
public class HtmlParserUtil {
    public static String getNovelLink(String url)
    {
        Connection conn= Jsoup.connect(url);
        try {
            //获取到整个网页
            Document doc=conn.timeout(10000).get();
            //获取检索到的小说列表的第一个
            Element li1=doc.select("div.wrap").select("div.result-wrap").select("div.main-content-wrap").select("div.book-img-text").select("ul").select("li").get(0);
            //从第一个小说中，检索书籍详情链接，也可以在此获取更新状态，但是有时不准
            Element node2=li1.select("div.book-img-box").select("a[href]").first();
            return node2.attr("href");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    public static Book getUpdateInfo(String url)
    {
        Connection conn= Jsoup.connect(url);
        try {
            //获取到整个网页
            Book book=new Book();
            Document doc=conn.timeout(10000).get();
            //获取图片地址
            book.imgUrl=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-img").first().select("a.J-getJumpUrl").first().getElementsByTag("img").attr("src");
            //获取书名
            book.bookName=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-info").first().select("h1").select("em").text();
            //获取作者
            book.author=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-info").first().select("h1").select("a").text();
            //获取简介
            book.info=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-info").first().select("p.intro").text();
            //获取更新状态
            book.updateTitle=doc.select("div.book-detail-wrap").select("div.book-content-wrap").select("div.left-wrap").select("div.book-state").select("ul").select("li").get(1).select("a.blue").attr("title");
            book.updateTime=doc.select("div.book-detail-wrap").select("div.book-content-wrap").select("div.left-wrap").select("div.book-state").select("ul").select("li").get(1).select("em").text();
            return book;
        }catch(Exception e)
        {
            //Toast.makeText(App.getContext(),"网络连接差，请重试...",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    public static CacheBook getCacheUpdateInfo(String url)
    {
        Connection conn= Jsoup.connect(url);
        try {
            //获取到整个网页
            CacheBook book=new CacheBook();
            Document doc=conn.timeout(10000).get();
            //获取书名
            book.cacheBookName=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-info").first().select("h1").select("em").text();
            //获取作者
            book.cacheAuthor=doc.select("div.book-detail-wrap").select("div.book-information").select("div.book-info").first().select("h1").select("a").text();
            //获取更新状态
            book.cacheUpdateTitle=doc.select("div.book-detail-wrap").select("div.book-content-wrap").select("div.left-wrap").select("div.book-state").select("ul").select("li").get(1).select("a.blue").attr("title");
            book.cacheUpdateTime=doc.select("div.book-detail-wrap").select("div.book-content-wrap").select("div.left-wrap").select("div.book-state").select("ul").select("li").get(1).select("em").text();
            return book;
        }catch(Exception e)
        {
            //Toast.makeText(App.getContext(),"网络连接差，请重试...",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    public static String getNovelIsExist(String url)
    {
        Connection conn= Jsoup.connect(url);
        try {
            //获取到整个网页
            Document doc=conn.timeout(10000).get();
            //获取检索到的小说列表的第一个
            Element li1=doc.select("div.wrap").select("div.result-wrap").select("div.main-content-wrap").select("div.book-img-text").select("ul").select("li").get(0);
            //从第一个小说中，检索书籍详情链接，也可以在此获取更新状态，但是有时不准
            Element node2=li1.select("div.book-img-box").select("a[href]").first();
            return node2.select("cite").text();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
