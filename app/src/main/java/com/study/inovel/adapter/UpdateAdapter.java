package com.study.inovel.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.study.inovel.util.NetworkState;

import java.util.List;

/**
 * Created by dnw on 2017/3/31.
 */
public class UpdateAdapter extends BaseAdapter{
    public Context context;
    public List<Book> bookUpdateList;
    SharedPreferences sharedPreferences;
    public UpdateAdapter(Context context, List<Book> list) {
        this.context=context;
        this.bookUpdateList=list;
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
        Book book=(Book) getItem(position);
        ViewHolder holder;
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
        if(!sharedPreferences.getBoolean("no_picture_mode",true))
        {
            holder.book_img.setVisibility(View.VISIBLE);
            Picasso.with(context).load("https:"+book.imgUrl).into(holder.book_img);
        }else
        {
            holder.book_img.setVisibility(View.GONE);
        }
        holder.book_name.setText(book.bookName);
        holder.author.setText(book.author);
        holder.info.setText(book.info);
        holder.update_title.setText(book.updateTitle);
        holder.update_time.setText(book.updateTime);
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
