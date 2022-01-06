package com.example.android.pets.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.pets.R;
import com.example.android.pets.pets.data.PetContract.PetEntry;
import com.example.android.pets.pets.data.PetContract;

public class NameCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item2, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in list item layout
        TextView dateTextView = (TextView) view.findViewById(R.id.name);
        TextView workSummaryTextView = (TextView) view.findViewById(R.id.work_summary);
        //TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
        int workColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_WORK);
        //int dateColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_DATE);

        // Read the pet attributes from the Cursor for the current pet
        String name = cursor.getString(nameColumnIndex);
        String worksummary = cursor.getString(workColumnIndex);



        // Update the TextViews with the attributes for the current pet
        dateTextView.setText(name);
        workSummaryTextView.setText(worksummary);
        //summaryTextView.setText(name);
    }
}

