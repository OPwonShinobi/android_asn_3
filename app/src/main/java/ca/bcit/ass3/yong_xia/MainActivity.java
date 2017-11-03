package ca.bcit.ass3.yong_xia;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private SQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new PotluckDbHelper(this);

        populateDb();

        populateEventsList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    private void populateDb() {
        Event[] events = new Event[3];
        events[0] = new Event("Halloween Party", "Oct 30, 2017", "6:30 PM", "N/A Desc");
        events[1] = new Event("Christmas Party", "December 20, 2017", "12:30 PM", "N/A Desc");
        events[2] = new Event("New Year Eve", "December 31, 2017", "8:00 PM", "N/A Desc");
        db = helper.getWritableDatabase();
        try {
            //@TODO: will need to remove this
            db.execSQL("delete from Event_Master");
            for(Event event : events) {
                ((PotluckDbHelper)helper).InsertEventMasterSQLTableEntry(db, event);
            }

        } catch (SQLiteException sqle) {
            String msg = "[MainActivity / populateDb] DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        db.close();
    }

    private void populateEventsList() {
        ListView eventsListView = (ListView) findViewById(R.id.eventsListView);
        String[] events = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select Name, Date, Time, Desc from Event_Master", null);

            //@TODO: should check if getCount == 0
            int count = cursor.getCount();
            events = new String[count+1];

            //maybe excessive string formatting
            //header row
            events[0] = String.format(getResources().getString(R.string.name_locale), "Name");
            events[0] += String.format(getResources().getString(R.string.date_locale), "Date");
            events[0] += String.format(getResources().getString(R.string.time_locale), "Time");
            if (cursor.moveToFirst()) {
                int i = 1;
                do {
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    String date = cursor.getString(cursor.getColumnIndex("Date"));
                    String time = cursor.getString(cursor.getColumnIndex("Time"));
                    String desc = cursor.getString(cursor.getColumnIndex("Desc"));

                    events[i] = String.format(getResources().getString(R.string.name_locale), name);
                    events[i] += String.format(getResources().getString(R.string.date_locale), date);
                    events[i] += String.format(getResources().getString(R.string.time_locale), time);
                    events[i] += "\n" +desc;
                    ++i;
                } while( cursor.moveToNext() );
            }
        } catch (SQLiteException sqle) {
            String msg = "[MainActivity / populateEventList] DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.custom_list_view, events);
        eventsListView.setAdapter(myAdapter);
        db.close();
    }
}
