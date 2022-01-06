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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.Loader;

import com.example.android.pets.pets.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.android.pets.pets.data.PetContract.PetEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PET_LOADER = 0;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentPetUri;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mDateEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWorkEditText;
    private EditText mTimeFromEditText;
    private EditText mTimeToEditText;
    private EditText mWagesEditText;
    private  String names;
    private String nameString,dateString,workString, time_from_String,time_to_sting,wagesString;
    boolean isAllFieldsChecked = false;







    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mPetHasChanged = false;

    private   DatePickerDialog datePickerDialog;
    private   TimePickerDialog timePickerDialog;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_name);

        mDateEditText = (EditText) findViewById(R.id.date);
        mWorkEditText = (EditText) findViewById(R.id.edit_work);
        //mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mTimeFromEditText = (EditText) findViewById(R.id.time_from);
        mTimeToEditText = (EditText) findViewById(R.id.time_to);
        mWagesEditText = (EditText) findViewById(R.id.edit_wages);




        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int  day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH); // it calendar class month number starts from 0
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(EditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mDateEditText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });



        mTimeFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int  hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String timeSet = "";
                        int hour = hourOfDay;
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }
                        mTimeFromEditText.setText(hour +":"+minute+timeSet);
                    }
                },hour,minute,false);
                timePickerDialog.show();


            }
        });
        mTimeToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int  hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeSet = "";
                        int hour = hourOfDay;
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }
                        mTimeToEditText.setText(hour +":"+minute+timeSet);
                    }
                },hour,minute,false);
                timePickerDialog.show();


            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mWorkEditText.setOnTouchListener(mTouchListener);
        mTimeFromEditText.setOnTouchListener(mTouchListener);
        mTimeToEditText.setOnTouchListener(mTouchListener);
        mTimeFromEditText.setOnTouchListener(mTouchListener);
        mWagesEditText.setOnTouchListener(mTouchListener);

        //setupSpinner();

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentPetUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(R.string.editor_activity_title_new_pet);



            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();


        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(R.string.editor_activity_title_edit_pet);


            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

    }




    /**
     * Get user input from editor and save new pet into database.
     */
    private void savePet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        nameString = mNameEditText.getText().toString().trim();
        //names = nameString;
        dateString = mDateEditText.getText().toString().trim();
        workString = mWorkEditText.getText().toString().trim();
        time_from_String = mTimeFromEditText.getText().toString().trim();
        time_to_sting = mTimeToEditText.getText().toString().trim();
        wagesString = mWagesEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank




        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        //CheckAllFields();


        isAllFieldsChecked = CheckAllFields();
        if (isAllFieldsChecked) {
            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values
            ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_NAME, nameString);
            values.put(PetEntry.COLUMN_DATE, dateString);
            values.put(PetEntry.COLUMN_WORK, workString);
            values.put(PetEntry.COLUMN_TIME_FROM, time_from_String);
            values.put(PetEntry.COLUMN_TIME_TO, time_to_sting);
            values.put(PetEntry.COLUMN_WAGES, wagesString);
            if (mCurrentPetUri == null) {
                // Insert a new pet into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(
                        PetEntry.CONTENT_URI,
                        values
                );

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(this,CatalogActivity.class));
                     finishAffinity();
                }
            } else {
                // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_pet_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_pet_successful), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,CatalogActivity.class));
                    finishAffinity();
                }
        }





        }
    }

    private boolean CheckAllFields() {
        if (nameString.length() == 0) {
            mNameEditText.setError("Name field is required");
            return false;
        }

        else if (dateString.length() == 0) {
            mDateEditText.setError("Date field is required");
            return false;
        }

        else if (workString.length() == 0) {
            mWorkEditText.setError("Work field is required");
            return false;
        }

        else if (time_from_String.length() == 0) {
            mTimeFromEditText.setError("Time is required");
            return false;
        }
        else if (time_to_sting.length() == 0) {
            mTimeToEditText.setError("Time is required");
            return false;
        }
        else if (wagesString.length() == 0) {
            mWagesEditText.setError("Wages is required");
            return false;
        }

        // after all validation return true.
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            MenuItem menuItem2 = menu.findItem(R.id.data_sort);
            menuItem.setVisible(false);
            menuItem2.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                return true;
                //CheckAllFields();
                // Exit activity
            case R.id.data_sort:
                //insertPet();
                Intent i = new Intent(EditorActivity.this,SortActivity.class);
                i.putExtra("names",names);
                startActivity(i);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)

                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME,
                PetEntry.COLUMN_DATE,
                PetEntry.COLUMN_WORK,
                PetEntry.COLUMN_TIME_FROM,
                PetEntry.COLUMN_TIME_TO,
                PetEntry.COLUMN_WAGES

        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,           // Parent activity context
                mCurrentPetUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,          // No selection clause
                null,       // No selection arguments
                null);         // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
            int dateColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_DATE);
            int workColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_WORK);
            int time_fromColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_TIME_FROM);
            int time_toColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_TIME_TO);
            int wagesColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_WAGES);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String work = cursor.getString(workColumnIndex);
            String time_f = cursor.getString(time_fromColumnIndex);
            String time_to = cursor.getString(time_toColumnIndex);
            String wages = cursor.getString(wagesColumnIndex);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            names = mNameEditText.getText().toString();
            mDateEditText.setText(date);
            mWorkEditText.setText(work);
            mTimeFromEditText.setText(time_f);
            mTimeToEditText.setText(time_to);
            mWagesEditText.setText(wages);




        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mDateEditText.setText("");
        mWorkEditText.setText("");
        mTimeFromEditText.setText("");
        mTimeToEditText.setText("");
        mWagesEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentPetUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
            }

            // Close the activity

            startActivity(new Intent(this,CatalogActivity.class));
            finishAffinity();

        }
    }

}