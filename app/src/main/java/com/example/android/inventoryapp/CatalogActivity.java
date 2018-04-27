package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{

    private CursorAdapter mItemCursorAdapter;
    private final static int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find ListView to populate
        ListView itemListView = findViewById(R.id.list);

        // Find and set EmptyView
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Create URI for clicked on item
                Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                // Create Intent for opening the Editor Activity
                Intent openEditItem = new Intent(CatalogActivity.this, EditorActivity.class);

                // Add the uri data to the Intent
                openEditItem.setData(uri);

                // Start the EditorActivity with the Intent
                startActivity(openEditItem);
            }
        });

        // Init the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        // Re-establish the ItemCursorAdapter
        mItemCursorAdapter = new ItemCursorAdapter(this, null);

        // Attach the CursorAdapter to the ListView
        itemListView.setAdapter(mItemCursorAdapter);

    }

    /**
     * Inserts dummy data into the inventory
     */
    private void insertItem()
    {
        // Creating pet values for inserting
        ContentValues values = createEntry("Razor Scooter", 5, 12000, "Fun scooter to ride around Alki in Seattle");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
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
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // User clicked on an options menu item
        switch (item.getItemId())
        {
            case R.id.action_insert_dummy_data:
                // Insert the dummy data
                insertItem();
                return true;
            case R.id.action_delete_all_data:
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
                ItemEntry.COL_ITEM_PRICE
        };

        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        mItemCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mItemCursorAdapter.swapCursor(null);
    }
}
