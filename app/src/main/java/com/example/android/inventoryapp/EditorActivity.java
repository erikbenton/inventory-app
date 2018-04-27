package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    // Globals for all the EditText views
    private EditText mNameEditText;
    private EditText mStockEditText;
    private EditText mPriceEditText;
    private EditText mDescripEditText;

    // Global if Uri is sent for editing
    private Uri mContentItemUri;

    // ID for the loader
    private static final int ITEM_EDIT_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // If sent with an Intent, grab it
        Intent sentIntent = getIntent();
        mContentItemUri = sentIntent.getData();

        // Set the title of the EditorActivity on which situation we have
        // If the EditorActivity was opened using the ListView item, then we will
        // have URI of Item to change app bar to say "Edit Item"
        // Otherwise if this is a new Item, URI is null so change app to say "Add Item"
        if(mContentItemUri == null)
        {
            // Clicking on the FAB for adding a pet
            setTitle(R.string.editor_activity_title_new_item);
        }
        else
        {
            // Clicking on the FAB for adding a pet
            setTitle(R.string.editor_activity_title_edit_item);

            // Init loader for getting data
            // Init the loader
            getLoaderManager().initLoader(ITEM_EDIT_LOADER, null, this);
        }

        // Setting up all the EditText views
        mNameEditText    = findViewById(R.id.edit_item_name);
        mStockEditText   = findViewById(R.id.edit_item_stock);
        mPriceEditText   = findViewById(R.id.edit_item_price);
        mDescripEditText = findViewById(R.id.edit_item_description);
    }

    private void saveItem()
    {
        // Getting Item values
        String name    = mNameEditText.getText().toString().trim();
        int    stock   = Integer.parseInt(mStockEditText.getText().toString().trim());
        int    price;
        String descrip = mDescripEditText.getText().toString().trim();



        int rowsAffected = 0;

        if (mContentItemUri == null)
        {
            // Create the ContentValues
            price   = Integer.parseInt(mPriceEditText.getText().toString().trim());
            ContentValues values = createEntry(name, stock, price, descrip);
            // Insert the entry
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            rowsAffected++;
        }
        else
        {
            double priceDouble   = (Double.parseDouble(mPriceEditText.getText().toString().trim().substring(1))*100);
            price = (int)priceDouble;

            // Create the ContentValues
            ContentValues values = createEntry(name, stock, price, descrip);

            rowsAffected = getContentResolver().update(mContentItemUri, values, null, null);
        }

        if (rowsAffected == 0)
        {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Creates the ContentValues for entering an Item into a SQLite db
     * @param name - Name of the Item
     * @param stock - Stock of the Item
     * @param price - Price of the Item ($12.34 -> 1234)
     * @param description - Description of the Item
     * @return ContentValues object with the desired entries
     */
    private ContentValues createEntry(String name, int stock, int price, String description)
    {
        // Creating Item
        ContentValues itemValues = new ContentValues();
        itemValues.put(ItemEntry.COL_ITEM_NAME, name);
        itemValues.put(ItemEntry.COL_ITEM_STOCK, stock);
        itemValues.put(ItemEntry.COL_ITEM_PRICE, price);
        itemValues.put(ItemEntry.COL_ITEM_DESCRIP, description);

        return itemValues;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // User clicked on an options menu item
        switch (item.getItemId())
        {
            case R.id.action_save:
                // Save the item
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                // Delete all the entries
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // Define projection for query
        // Get the ID, Name, Stock, Price
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COL_ITEM_NAME,
                ItemEntry.COL_ITEM_STOCK,
                ItemEntry.COL_ITEM_PRICE,
                ItemEntry.COL_ITEM_DESCRIP
        };

        return new CursorLoader(this, mContentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // Cursor check
        if(cursor == null || (cursor != null && cursor.getCount() == 0))
        {
            return;
        }

        // Move to first position in cursor
        cursor.moveToFirst();

        // Get the attributes of the pet to fill View with
        String itemName = cursor.getString((cursor.getColumnIndex(ItemEntry.COL_ITEM_NAME)));
        int itemStock = cursor.getInt((cursor.getColumnIndex(ItemEntry.COL_ITEM_STOCK)));
        int itemPrice = cursor.getInt((cursor.getColumnIndex(ItemEntry.COL_ITEM_PRICE)));
        String itemDescrip = cursor.getString((cursor.getColumnIndex(ItemEntry.COL_ITEM_DESCRIP)));

        // Give the views the appropriate values
        mNameEditText.setText(itemName, TextView.BufferType.EDITABLE);
        mStockEditText.setText(itemStock + "", TextView.BufferType.EDITABLE);
        mPriceEditText.setText("$" + (itemPrice/100) + "." + (itemPrice%100), TextView.BufferType.EDITABLE);
        mDescripEditText.setText(itemDescrip, TextView.BufferType.EDITABLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        // Give the views the appropriate values
        mNameEditText.setText("", TextView.BufferType.EDITABLE);
        mStockEditText.setText("", TextView.BufferType.EDITABLE);
        mPriceEditText.setText("", TextView.BufferType.EDITABLE);
        mDescripEditText.setText("", TextView.BufferType.EDITABLE);
    }
}
