package com.example.expensemanagementstudent.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.DatabaseHelper;
import com.example.expensemanagementstudent.model.IconItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Map<String, Integer> iconMap;

    public CategoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
        initializeIconMap(context);
    }

    private void initializeIconMap(Context context) {
        iconMap = new HashMap<>();
        List<IconItem> iconList = Arrays.asList(
                new IconItem(R.drawable.ic_company, "ic_company"),
                new IconItem(R.drawable.ic_shoppingg, "ic_shopping"),
                new IconItem(R.drawable.ic_foodd, "ic_food"),
                new IconItem(R.drawable.ic_transport, "ic_transport"),
                new IconItem(R.drawable.ic_health, "ic_health"),
                new IconItem(R.drawable.ic_travell, "ic_travel"),
                new IconItem(R.drawable.ic_entertainment, "ic_entertainment"),
                new IconItem(R.drawable.ic_saving, "ic_saving")
        );
        for (IconItem item : iconList) {
            iconMap.put(item.getIconName(), item.getIconResId());
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.item_category, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iconView = view.findViewById(R.id.icon);
        TextView nameView = view.findViewById(R.id.name);
        TextView typeView = view.findViewById(R.id.type);

        String iconName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ICON_COL));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_NAME_COL));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_TYPE_COL));

        Integer iconResId = iconMap.get(iconName);

        if (iconResId != null) {
            Drawable iconDrawable = ContextCompat.getDrawable(context, iconResId);
            iconView.setImageDrawable(iconDrawable);
        }

        nameView.setText(name);
        typeView.setText(type == 0 ? "Income" : "Expense");
    }
}
