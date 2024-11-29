package com.example.expensemanagementstudent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensemanagementstudent.model.IconItem;

import java.util.List;
//tạo adapter hiển thị icon và tên
public class IconAdapter extends BaseAdapter {
    private Context context;
    private List<IconItem> iconList;

    public IconAdapter(Context context, List<IconItem> iconList) {
        this.context = context;
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        return iconList.size();
    }

    @Override
    public Object getItem(int position) {
        return iconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.ivIcon);
        TextView textView = convertView.findViewById(R.id.tvIconName);

        IconItem iconItem = iconList.get(position);

        // Set data to views
        imageView.setImageResource(iconItem.getIconResId());
        textView.setText(iconItem.getIconName());

        return convertView;
    }
}
