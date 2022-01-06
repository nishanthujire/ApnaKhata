package com.example.android.pets.pets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.pets.pets.data.PetContract.PetEntry;
import com.example.android.pets.pets.data.PetDbHelper;


public class NameDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentPetUri;
    TextView mNameField;
    private static final int EXISTING_PET_LOADER = 0;
    private  String date;
    // This is the Adapter being used to display the list's pet
    NameCursorAdapter mNameCursorAdapter;
    ListView petListView;
    boolean searchCollapseClicked = false;
    //boolean searchExapandClicked = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_details);
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        //mCurrentPetUri = intent.getData();
        date= getIntent().getStringExtra("date");
        setTitle(R.string.name_activity);

        // Find the ListView which will be populated with the pet data
        petListView = (ListView) findViewById(R.id.list2);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        // Setup an adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mNameCursorAdapter = new NameCursorAdapter(this, null);
        petListView.setAdapter(mNameCursorAdapter);
        // Enable filtering in ListView
        petListView.setTextFilterEnabled(true);
        // Prepare your adapter for filtering
        mNameCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                PetDbHelper handler = new PetDbHelper(NameDetailsActivity.this);
                //// Get access to the underlying writeable database
                SQLiteDatabase db = handler.getWritableDatabase();
                // in real life, do something more secure than concatenation
                // but it will depend on your schema
                // This is the query that will run on filtering
                // String query = "SELECT _ID as _id, name FROM MYTABLE "+ "where name like '%" + constraint + "%' "+ "ORDER BY NAME ASC";
                //return db.rawQuery(query, null);
                return db.rawQuery( "select  * from "+PetEntry.TABLE_NAME+" WHERE "+PetEntry.COLUMN_NAME+" like '%" + constraint + "%' GROUP BY name,work Order by name ASC", null );

            }
        });

        // Setup the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(NameDetailsActivity.this, EditorActivity.class);


                // From the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME,
                PetEntry.COLUMN_WORK

        };
        String selection = PetEntry.COLUMN_DATE + "=?";
        String[] selectionArgs = new String[]{date};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,           // Parent activity context
                PetEntry.CONTENT_URI,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                selection,
                selectionArgs,       // No selection arguments
                PetEntry.COLUMN_NAME+" ASC");         // Default sort order

    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mNameCursorAdapter.swapCursor(cursor);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mNameCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_name_details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)menuItem.getActionView();

        //searchView.getQueryHint("dd");
        //searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new  androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!searchCollapseClicked) {
                    mNameCursorAdapter.getFilter().filter(newText);
                }
                //Toast.makeText(NameDetailsActivity.this, "Sam called", Toast.LENGTH_SHORT).show();



                //mCursorAdapter.notifyDataSetChanged();


                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchCollapseClicked = false;
                //Toast.makeText(NameDetailsActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }


            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchCollapseClicked = true;

                PetDbHelper handler = new PetDbHelper(NameDetailsActivity.this);
                //// Get access to the underlying writeable database
                SQLiteDatabase db = handler.getWritableDatabase();


                String[] projection = {
                        PetEntry._ID,
                        PetEntry.COLUMN_NAME,
                        PetEntry.COLUMN_WORK

                };
                String selection = PetEntry.COLUMN_DATE + "=?";
                String[] selectionArgs = new String[]{date};

                //Cursor cursorData = db.query(true, PetEntry.TABLE_NAME, new String[] { PetEntry._ID ,PetEntry.COLUMN_DATE }, null, null, PetEntry.COLUMN_DATE, null,  PetEntry.COLUMN_DATE+" DESC", null);


                Cursor cursorData = db.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, PetEntry.COLUMN_NAME + " ASC", null);


                mNameCursorAdapter.swapCursor(cursorData);
                mNameCursorAdapter.notifyDataSetChanged();
                // petListView.setTextFilterEnabled(false);


               // Toast.makeText(NameDetailsActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // User clicked on a menu option in the app bar overflow menu
//        switch (item.getItemId()) {
//            // Respond to a click on the "Insert dummy data" menu option
//            case R.id.action_insert_dummy_data:
//                //insertPet();
//                return true;
//            // Respond to a click on the "Delete all entries" menu option
//            case R.id.action_delete_all_entries:
//                deleteAllPets();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }






}