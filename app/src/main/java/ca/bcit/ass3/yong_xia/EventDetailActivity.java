package ca.bcit.ass3.yong_xia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailActivity extends Activity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private SQLiteOpenHelper helper;
    private int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        helper = new PotluckDbHelper(this);
        eventID = getIntent().getIntExtra("EVENT_ID", 0);

        //init btn
        Button addBtn = findViewById(R.id.addItemBtn);
        addBtn.setOnClickListener(new myButtonListener());

        //fill event info
        String[] eventDetails = findEventDetails();
        TextView headerRow = (TextView) findViewById(R.id.eventDetailsHeaderTextView);
        headerRow.setText(eventDetails[0]);

        //fill added items
        populateItemsList();
    }

    @Override
    public void recreate() {
        super.recreate();
        clearInputFields();
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

    private void clearInputFields() {
        TextView nameTextView = (TextView) findViewById(R.id.itemNameTextView);
        nameTextView.setText("");
        TextView unitTextView = (TextView) findViewById(R.id.itemUnitTextView);
        unitTextView.setText("");
        TextView quantityTextView = (TextView) findViewById(R.id.itemQuantityTextView);
        quantityTextView.setText("");
    }

    private String[] findEventDetails() {
        //as 1 string
//        String headerStr = getIntent().getStringExtra("EVENT_STR");
//        TextView headerRow = (TextView) findViewById(R.id.eventDetailsHeaderTextView);
//        headerRow.setText(headerStr);

        //as separate fields
        //int eventID = getIntent().getIntExtra("EVENT_ID", 0);
        db = helper.getReadableDatabase();
        //cursor = db.rawQuery(String.format(getResources().getString(R.string.item_query_specific), "_eventID", String.valueOf(eventID)), null);
        cursor = db.rawQuery("SELECT Name, Date, Time FROM Event_Master WHERE _eventID=" + eventID + ";", null);
        int count = cursor.getCount();
        int columns = cursor.getColumnCount();
        String[] eventDetails = new String[columns];
        if (cursor.moveToFirst() && count > 0) {
            for(int i = 0; i < columns; ++i) {
                eventDetails[i] = cursor.getString(i);
            }
        }
        db.close();
        return eventDetails;
    }

    private void populateItemsList() {
        ListView eventsListView = (ListView) findViewById(R.id.eventDetailsListView);
        String[] items = null;
        String eventName = null;
        //int eventID = getIntent().getIntExtra("EVENT_ID", 0); //bc list item 1 is db item 0
        try {
            db = helper.getReadableDatabase();
            //String itemQuery = String.format(getResources().getString(R.string.item_query), String.valueOf(eventID));
            String itemQuery = "SELECT ItemName, ItemQuantity, ItemUnit FROM Event_Detail WHERE _eventID=" + eventID + ";";
            cursor = db.rawQuery(itemQuery, null);
            int count = cursor.getCount();
            items = new String[count+1]; //+1 for desc header
            items[0] = String.format(getResources().getString(R.string.item_locale), getResources().getString(R.string.item));
            items[0] += String.format(getResources().getString(R.string.unit_locale), getResources().getString(R.string.unit));
            items[0] += String.format(getResources().getString(R.string.quantity_locale), getResources().getString(R.string.quantity));
            if (cursor.moveToFirst()) {
                int i = 1;
                do {
                    String item = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.item_name_col)));
                    String unit = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.item_unit_col)));
                    String quantity = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.item_quantity_col)));

                    items[i] = String.format(getResources().getString(R.string.item_locale), item);
                    items[i] += String.format(getResources().getString(R.string.unit_locale), unit);
                    items[i] += String.format(getResources().getString(R.string.quantity_locale), quantity);
                    ++i;
                } while( cursor.moveToNext());
            }
        } catch (SQLiteException sqle) {
            String msg = "[EventDetailActivity / populateEventList] DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.custom_list_view, items);
        eventsListView.setAdapter(myAdapter);
        db.close();
    }

    private class myButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //item
            TextView nameTextView = (TextView) findViewById(R.id.itemNameTextView);
            TextView unitTextView = (TextView) findViewById(R.id.itemUnitTextView);
            TextView quantityTextView = (TextView) findViewById(R.id.itemQuantityTextView);
            boolean hasEmptyField = false;
            String name = nameTextView.getText().toString().trim();
            String unit = unitTextView.getText().toString().trim();
            String quantityStr = quantityTextView.getText().toString().trim();
            if (name.equals("") || unit.equals("") || quantityStr.equals("")) {
                String emptyError = getResources().getString(R.string.empty_field_error);
                Toast t = Toast.makeText(EventDetailActivity.this, emptyError, Toast.LENGTH_LONG);
                t.show();
            } else {
                int quantity = Integer.parseInt(quantityStr);
                Item item = new Item(name, unit, quantity, eventID);
                addItemToDB(item);
            }
        }

        private void addItemToDB(Item item) {
//            Item[] items = new Item[4];
//            items[0] = new Item("Coca cola", "6 packs", 5, eventID);
//            items[1] = new Item("Pizza", "Large", 3, eventID);
//            items[2] = new Item("Potato Chips", "Large Bag", 5, eventID);
//            items[3] = new Item("Napkins", "Pieces", 100, eventID);
            db = helper.getWritableDatabase();
            try {
                //@TODO: will need to remove this
                //db.execSQL("delete from Event_Detail;");
                //((PotluckDbHelper)helper).dropTableSQL(db, "Event_Detail");
                //db.execSQL( ((PotluckDbHelper)helper).CreateTableEventDetailSQL());
                ((PotluckDbHelper)helper).InsertSQLTableEntry(db, item);
            } catch (SQLiteException sqle) {
                String msg = "[MainActivity / populateDb] DB unavailable";
                msg += "\n\n" + sqle.toString();
                Toast t = Toast.makeText(EventDetailActivity.this, msg, Toast.LENGTH_LONG);
                t.show();
            }
            db.close();
            EventDetailActivity.this.recreate();
        }
    }
}
