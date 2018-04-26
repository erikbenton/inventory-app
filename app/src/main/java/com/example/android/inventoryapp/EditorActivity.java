package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity
{

    // Globals for all the EditText views
    private EditText mNameEditText;
    private EditText mStockEditText;
    private EditText mPriceEditText;
    private EditText mDescripEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

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
        int    price   = Integer.parseInt(mPriceEditText.getText().toString().trim());
        String descrip = mDescripEditText.getText().toString().trim();

        // Create the ContentValues
        ContentValues values = createEntry(name, stock, price, descrip);

        // Insert the entry
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

        if (newUri == null)
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
}
