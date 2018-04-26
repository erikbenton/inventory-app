package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper
{
    // Database name and version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NOT_NULL_TYPE = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                    ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ItemEntry.COL_ITEM_NAME + TEXT_TYPE + NOT_NULL_TYPE + COMMA_SEP +
                    ItemEntry.COL_ITEM_STOCK + INTEGER_TYPE + NOT_NULL_TYPE + COMMA_SEP +
                    ItemEntry.COL_ITEM_PRICE + INTEGER_TYPE + NOT_NULL_TYPE + COMMA_SEP +
                    ItemEntry.COL_ITEM_DESCRIP+ TEXT_TYPE + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;

    /**
     * Constructor for the Item DB Helper
     * @param context
     */
    public ItemDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
