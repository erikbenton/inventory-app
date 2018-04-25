package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ItemContract
{

    // Constants for content
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    // Constructor
    private ItemContract(){}

    public static abstract class ItemEntry implements BaseColumns
    {

        // Complete Content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the CONTENT_URI for a list of items
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the CONTENT_URI for a single of items
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Constants for the items table database
        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COL_ITEM_NAME = "name";
        public static final String COL_ITEM_STOCK = "stock";
        public static final String COL_ITEM_PRICE = "price";
        public static final String COL_ITEM_DESCRIP = "description";
    }
}
