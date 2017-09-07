package com.study.inovel.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.inovel.R;
import com.study.inovel.bean.Book;
import com.study.inovel.util.CacheUtil;

import java.util.List;

import libcore.io.DiskLruCache;

/**
 * Created by dnw on 2017/3/31.
 */
public class UpdateAdapter extends BaseAdapter{
    public Context context;
    public List<Book> bookUpdateList;
    Bitmap bitmap;
    ViewHolder holder;
    DiskLruCache diskLruCache;
    SharedPreferences sharedPreferences;
    int getPosition;
    public UpdateAdapter(Context context, List<Book> list, DiskLruCache diskLruCache) {
        this.context=context;
        this.bookUpdateList=list;
        this.diskLruCache=diskLruCache;
    }

    @Override
    public int getCount() {
        return bookUpdateList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookUpdateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        //Book book=(Book) getItem(position);

        View view;
        UpdateAdapter.this.notifyDataSetChanged();
        if(convertView==null)
        {
            view=LayoutInflater.from(context).inflate(R.layout.book_item,null);
            holder=new ViewHolder();
            holder.book_name=(TextView)view.findViewById(R.id.book_name);
            holder.author=(TextView)view.findViewById(R.id.author);
            holder.info=(TextView)view.findViewById(R.id.info);
            holder.update_title=(TextView)view.findViewById(R.id.update_title);
            holder.update_time=(TextView)view.findViewById(R.id.update_time);
            holder.book_img=(ImageView)view.findViewById(R.id.book_img);
            view.setTag(holder);
        }else
        {
            view=convertView;
            holder=(ViewHolder) view.getTag();
        }
        if(!sharedPreferences.getBoolean("no_picture_mode",false))
        {
            getPosition=position;
            holder.book_img.setVisibility(View.VISIBLE);
            //先判断磁盘缓存中是否有图片，有图片则直接加载，无图片则从Picasso加载
            if(!CacheUtil.getBitmap(diskLruCache,"https:"+bookUpdateList.get(getPosition).imgUrl,holder.book_img))
            {
                Log.d("test","getBitmap with Picasso");
                Picasso.with(context).load("https:"+bookUpdateList.get(position).imgUrl).into(holder.book_img,new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {
                        //获取图片并缓存到磁盘
                        if(holder.book_img.getDrawable()!=null)
                        {
                            bitmap=((BitmapDrawable)holder.book_img.getDrawable()).getBitmap();
                            CacheUtil.saveBitmap(diskLruCache,"https:"+bookUpdateList.get(getPosition).imgUrl,bitmap);
                        }

                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            //((BitmapDrawable)holder.book_img.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG,90,out);

        }else
        {
            holder.book_img.setVisibility(View.GONE);
        }
        holder.book_name.setText(bookUpdateList.get(position).bookName);
        holder.author.setText(bookUpdateList.get(position).author);
        Log.d("test12",bookUpdateList.get(position).updateTitle+bookUpdateList.get(position).updateTime);
        holder.info.setText(bookUpdateList.get(position).info);
        holder.update_title.setText(bookUpdateList.get(position).updateTitle);
        holder.update_time.setText(bookUpdateList.get(position).updateTime);


       return view;
    }
    class ViewHolder
    {
        private TextView book_name;
        private TextView author;
        private TextView info;
        private TextView update_title;
        private TextView update_time;
        private ImageView book_img;
    }
}
