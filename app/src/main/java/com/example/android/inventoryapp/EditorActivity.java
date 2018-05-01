package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

    // Boolean for determining if Item has been edited
    private boolean mItemHasChanged = false;

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

            // Invalidate the options menu, so the "Delete" menu option can be hidden
            // (It doesn't make sense to delete a pet that isn't in the database)
            invalidateOptionsMenu();
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

        // Putting the touch Listener on all the views
        mNameEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mDescripEditText.setOnTouchListener(mTouchListener);

        // Adding TextWatcher to Price input
        mPriceEditText.addTextChangedListener(new NumberTextWatcher(mPriceEditText));
    }


    /**
     * OnTouchListener that listens for if any edits have been made to the
     * Input fields of the editor so that the user can be warned before
     * leaving editor if their changes have been saved
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                mItemHasChanged = true;
                view.requestFocus();
                return true;
            }
            return false;
        }
    };

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
                // Show Dialog Interface for displaying confirmation of pet deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If there haven't been any changes to the Item
                if(!mItemHasChanged)
                {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise there are unsaved changes
                // Need to create a ClickListener to handle the user confirming that
                // changes should be discarded
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // User clicked the discard button, navigate to the parent activity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show a dialog that notifies the user they unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        // If this is a new pet then hide the delete button
        if(mContentItemUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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

        int priceDollars = itemPrice / 100;
        int priceCents = itemPrice % 100;

        // Give the views the appropriate values
        mNameEditText.setText(itemName, TextView.BufferType.EDITABLE);
        mStockEditText.setText(itemStock + "", TextView.BufferType.EDITABLE);

        if(priceCents > 9)
        {
            mPriceEditText.setText(priceDollars + "." + priceCents, TextView.BufferType.EDITABLE);
        }
        else
        {
            mPriceEditText.setText(priceDollars + ".0" + priceCents, TextView.BufferType.EDITABLE);
        }

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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        // Create an alert dialog builder and set the message and click listeners for the
        // positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Keep Editing" button, so dismiss the dialog
                // and continue editing the pet
                if(dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog()
    {
        // Create and AlertDialog.Builder and set the message and click listener
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                // User clicked the delete button
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                if(dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * If the back button has been pressed
     */
    @Override
    public void onBackPressed()
    {
        // If there haven't been any changes
        if(!mItemHasChanged)
        {
            super.onBackPressed();
            return;
        }

        // Otherwise if there an unsaved changes, setup a dialog to warn the user
        // Create a ClickListener to handle the user confirming that the changes should be discarded
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                // User clicked the discard button, close the current activity
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Delete the pet from the database
     */
    private void deleteItem()
    {
        if(mContentItemUri == null)
        {
            return;
        }
        else
        {
            int numberOfPetsDeleted = getContentResolver().delete(mContentItemUri, null, null);

            if(numberOfPetsDeleted == 0)
            {
                // Unable to delete pet
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Pet was deleted
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
