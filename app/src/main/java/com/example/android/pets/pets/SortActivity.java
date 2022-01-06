package com.example.android.pets.pets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.pets.pets.data.PetDbHelper;

import com.example.android.pets.pets.data.PetContract.PetEntry;
import com.example.android.pets.pets.data.PetDbHelper;

import java.util.ArrayList;

public class SortActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private  String names;
    ;
    EditText nameTextView,salTextView,presentDate;
    //private ArrayList<Integer> p_date = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        linearLayout = findViewById(R.id.linear);
        PetDbHelper handler = new PetDbHelper(SortActivity.this);
        //// Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
       // nameTextView = (TextView) findViewById(R.id.name);
        salTextView = findViewById(R.id.sort_total_wages);
        nameTextView = findViewById(R.id.sort_name);
      //  presentDate = findViewById(R.id.sort_present_date);

        nameTextView.setEnabled(false);
        salTextView.setEnabled(false);
      //  presentDate.setEnabled(false);
        //salTextView.setText("sam");


        Intent intent = getIntent();
        names= intent.getStringExtra("names");

       Cursor cursorss = db.rawQuery("SELECT "+PetEntry.COLUMN_DATE+",name,SUM(" + PetEntry.COLUMN_WAGES + ") as Total FROM " + PetEntry.TABLE_NAME+" Where "+PetEntry.COLUMN_NAME+"='"+names+"'", null);


        if (cursorss.moveToFirst()) {

       int total = cursorss.getInt(cursorss.getColumnIndex("Total"));// get final total
           String name = cursorss.getString(cursorss.getColumnIndex("name"));// get final total
            String total_sal = Integer.toString(total);
            Cursor mCursor = db.rawQuery("SELECT "+PetEntry.COLUMN_DATE+" FROM " + PetEntry.TABLE_NAME+" Where "+PetEntry.COLUMN_NAME+"='"+names+"'", null);
            ArrayList<String> mArrayList = new ArrayList<String>();
            mCursor.moveToFirst();
            while(!mCursor.isAfterLast()) {
                mArrayList.add(mCursor.getString(mCursor.getColumnIndex(PetEntry.COLUMN_DATE))); //add the item
                mCursor.moveToNext();
            }
            LinearLayout layout = findViewById(R.id.la);


            for (int i=0;i<mArrayList.size();i++){
               // Log.v("msg","dates are "+mArrayList.get(i));
                EditText editText = new EditText(this);
                editText.setText(mArrayList.get(i));
                editText.setEnabled(false);
                editText.setTextColor(Color.parseColor("black"));

                editText.setTextSize(17);
                editText.setTypeface(null, Typeface.BOLD);

                layout.addView(editText);
            }

           nameTextView.setText(name);
           salTextView.setText(total_sal);


           //Log.v("msg", "sal is " + total);
          // Log.v("msg2", "name is " + name);








        }
    }
}