/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets.pets;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.widget.TextView;

import com.example.android.pets.pets.PetCursorAdapter;
import com.example.android.pets.pets.R;
import com.example.android.pets.pets.data.PetContract.PetEntry;
import com.example.android.pets.pets.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity  {

    private static final int PET_LOADER = 0;

    // This is the Adapter being used to display the list's pet
    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup an adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        PetDbHelper handler = new PetDbHelper(this);
//// Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.query(true, PetEntry.TABLE_NAME, new String[] { PetEntry._ID ,PetEntry.COLUMN_DATE }, null, null, PetEntry.COLUMN_DATE, null,  PetEntry.COLUMN_DATE+" DESC", null);
        mCursorAdapter = new PetCursorAdapter(this, cursor);

        petListView.setAdapter(mCursorAdapter);
        // Prepare your adapter for filtering
        mCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                PetDbHelper handler = new PetDbHelper(CatalogActivity.this);
                //// Get access to the underlying writeable database
                SQLiteDatabase db = handler.getWritableDatabase();
                // in real life, do something more secure than concatenation
                // but it will depend on your schema
                // This is the query that will run on filtering
                // String query = "SELECT _ID as _id, name FROM MYTABLE "+ "where name like '%" + constraint + "%' "+ "ORDER BY NAME ASC";
                //return db.rawQuery(query, null);
                Cursor cursor =  db.rawQuery( "select  * from "+PetEntry.TABLE_NAME+" WHERE "+PetEntry.COLUMN_DATE+" like '%" + constraint + "%' GROUP BY date Order by date DESC", null );
                if(cursor.getCount()>0){
                    return cursor;
                }
                else
                {
                    ImageView image = findViewById(R.id.empty_shelter_image);
                    TextView text1 = findViewById(R.id.empty_title_text);
                    TextView text2 = findViewById(R.id.empty_subtitle_text);

                    image.setVisibility(View.INVISIBLE);
                    text1.setVisibility(View.INVISIBLE);
                    text2.setVisibility(View.INVISIBLE);
                    return  cursor;


                }
                //return cursor;

            }
        });

        // Setup the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, NameDetailsActivity.class);




                // From the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                // Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                TextView dateView = (TextView) view.findViewById(R.id.name);
                String data = (String) dateView.getText();



                // Set the URI on the data field of the intent


                intent.putExtra("date",data);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        // getLoaderManager().initLoader(PET_LOADER, null, this);

    }


    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v(CatalogActivity.class.getSimpleName(), rowsDeleted + " rows deleted from pet database");
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCursorAdapter.getFilter().filter(newText);
                mCursorAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
