package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

import android.util.Log;
import android.widget.CursorAdapter;

public class ItemProvider extends ContentProvider
{
    // Constants for URI matcher
    private static final int ITEMS   = 100;
    private static final int ITEM_ID = 101;



    // Creating Uri matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Creating Uri patterns for given paths
    static
    {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    // Database Helper Object
    private ItemDbHelper mDbHelper;



    @Override
    public boolean onCreate()
    {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        // Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Make a cursor for holding the data
        Cursor cursor;

        // Get the Uri match
        int match = sUriMatcher.match(uri);

        // Use switch to determine what to do with the Uri
        switch (match)
        {
            case ITEMS:
                // Perform query to get all the items from the database
                cursor = db.query(ItemEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                // Set up the selection
                selection = ItemEntry._ID + "=?";

                // Get the Item ID from the Uri
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Make query with given pet ID
                cursor = db.query(ItemEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot open unknown URI " + uri);
        }

        // Set notification URI on the Cursor
        // so that we knw what Content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an Item into the database with the given content values. Return the new content URI
     * for that specific row in the database
     */
    private Uri insertItem(Uri uri, ContentValues values)
    {

        // Data validation checks
        //dataValidation(values);

        // Get the writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Get the id of the entry
        long id = db.insert(ItemEntry.TABLE_NAME, null, values);

        // Insert data into table
        if(id == -1)
        {
            Log.v("INSERTING ITEM INFO", "Unable to insert " + values.get(ItemEntry.COL_ITEM_NAME).toString());
            return null;
        }
        else
        {
            Log.v("CatalogActivity", "New row ID: " + id);
        }

        // Notify all Listeners that the data has changed for the Item content URI
        // uri: content://com.example.android.inventoryapp/items
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        // Get the Uri match
        int match = sUriMatcher.match(uri);

        // Use switch to determine what to do with the Uri
        switch (match)
        {
            case ITEMS:
                // Perform update to all the items from the database
                return updateItem(uri, values, selection, selectionArgs);
            case ITEM_ID:
                // Set up the selection
                selection = ItemEntry._ID + "=?";

                // Get the Item ID from the Uri
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Update the given Item
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot open unknown URI " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Check the size of the values
        if(values.size() == 0)
        {
            return 0;
        }

        // Notify all Listeners that the data for this Item has changed
        getContext().getContentResolver().notifyChange(uri, null);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Update the selected Items in the inventory with the given ContentValues
        int numRows = db.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        return numRows;
    }
}
