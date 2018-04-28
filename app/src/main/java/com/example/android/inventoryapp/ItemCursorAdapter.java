package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ItemContract;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

import org.w3c.dom.Text;

public class ItemCursorAdapter extends CursorAdapter
{
    public ItemCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        // Find views to populate
        TextView nameView  = view.findViewById(R.id.name);
        TextView priceView = view.findViewById(R.id.price);
        TextView stockView = view.findViewById(R.id.stock);

        // Extract data from the cursor
        String name  = cursor.getString((cursor.getColumnIndex(ItemEntry.COL_ITEM_NAME)));
        int price    = cursor.getInt((cursor.getColumnIndex(ItemEntry.COL_ITEM_PRICE)));
        int priceDollars = price / 100;
        int priceCents = price % 100;
        int stock    = cursor.getInt((cursor.getColumnIndex(ItemEntry.COL_ITEM_STOCK)));

        // Populate the fields with the extracted data
        nameView.setText(name);

        // Show the right number of decimal places for the given amount of cents
        if(priceCents > 9)
        {
            priceView.setText("$" + priceDollars + "." + priceCents);
        }
        else
        {
            priceView.setText("$" + priceDollars + ".0" + priceCents);
        }

        stockView.setText(stock + "");

    }
}
