package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract
{

    // Constants for content
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    private ItemContract(){}

    // Constants for the items table database
    public static final String TABLE_NAME       = "items";
    public static final String _ID              = BaseColumns._ID;
    public static final String COL_ITEM_NAME    = "name";
    public static final String COL_ITEM_STOCK   = "stock";
    public static final String COL_ITEM_PRICE   = "price";
    public static final String COL_ITEM_DESCRIP = "description";
}
