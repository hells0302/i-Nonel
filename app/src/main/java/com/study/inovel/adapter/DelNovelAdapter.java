package com.study.inovel.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.study.inovel.R;
import com.study.inovel.db.DatabaseUtil;

import java.util.List;

/**
 * Created by dnw on 2017/4/2.
 */
public class DelNovelAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    DatabaseUtil databaseUtil;

    public DelNovelAdapter(Context context,List<String> list,DatabaseUtil databaseUtil) {
        this.context=context;
        this.list=list;
        this.databaseUtil=databaseUtil;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, final View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;

        View view;
        if(convertView==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.layout_del_novel_item,null);
            holder=new ViewHolder();
            holder.novelName=(TextView)view.findViewById(R.id.del_novel_name);
            holder.delNovel=(Button)view.findViewById(R.id.del_novel_btn);
            view.setTag(holder);
        }else
        {
            view=convertView;
            holder=(ViewHolder)view.getTag();
        }
        final int location=i;
        holder.novelName.setText(list.get(i));
        holder.delNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                Log.d("xyz", "onClick: "+i+list.get(location).toString());
                final String name=list.get(location).toString();
                builder.setMessage("确定要删除《"+name+"》？");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除数据库中的记录
                        if(databaseUtil.delNovelLinkElement(name))
                        {

                            Toast.makeText(context,"删除"+name+"成功",Toast.LENGTH_SHORT).show();
                            list.remove(location);
                            DelNovelAdapter.this.notifyDataSetChanged();
                        }


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        return view;
    }

    class ViewHolder
    {
        private TextView novelName;
        private Button delNovel;
    }
}
