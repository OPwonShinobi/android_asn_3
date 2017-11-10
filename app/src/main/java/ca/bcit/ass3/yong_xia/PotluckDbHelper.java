package ca.bcit.ass3.yong_xia;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Alex on 01/11/2017.
 */

public class PotluckDbHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "PotluckDb.sqlite";
    private static final int DB_VER = 1;

    public PotluckDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        updateDatabase(db, 0, DB_VER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        updateDatabase(db, i, i1);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVer, int newVer) {
        try {
            if (oldVer == 0) {
                db.execSQL( CreateTableEventMasterSQL() );
                db.execSQL( CreateTableEventDetailSQL() );
            }
        } catch ( SQLException sqlE) {
            String toastMsg = "PotluckDbhelper-> updateDatabase or insert" +
                    "table entry failed";
            toastMsg += "\n\n" + sqlE.toString();
            Toast t = Toast.makeText(context, toastMsg, Toast.LENGTH_LONG);
            t.show();
        }
    }

    //might as well be a variable
    //but will be much less readable
    public String CreateTableEventMasterSQL() {
        String sql = "";
        sql += "CREATE TABLE Event_Master (";
        sql += "_eventId INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "Name TEXT, ";
        sql += "Date TEXT, ";
        sql += "Time TEXT ); ";
        return sql;
    }

    public void dropTableSQL(SQLiteDatabase writeableDB, String tableName) {
        writeableDB.execSQL("DROP TABLE " + tableName + ";");
    }

    public String CreateTableEventDetailSQL() {
        String sql = "";
        sql += "CREATE TABLE Event_Detail (";
        sql += "_detailId INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "ItemName TEXT, ";
        sql += "ItemUnit TEXT, ";
        sql += "ItemQuantity INTEGER, ";
        sql += "_eventId INTEGER, ";
        sql += "CONSTRAINT fKey_eventId FOREIGN KEY (_eventId) ";
        sql += "REFERENCES Event_Master(_eventId));";
        return sql;
    }

    public void InsertSQLTableEntry(SQLiteDatabase db, Event event) {
        ContentValues entryRow = new ContentValues();
        entryRow.put("Name", event.getName());
        entryRow.put("Date", event.getDate());
        entryRow.put("Time", event.getTime());
        db.insert("Event_Master", null, entryRow);
    }

    public void InsertSQLTableEntry(SQLiteDatabase db, Item item) {
        ContentValues entryRow = new ContentValues();
        entryRow.put("ItemName", item.getName());
        entryRow.put("ItemUnit", item.getUnit());
        entryRow.put("ItemQuantity", item.getQuantity());
        entryRow.put("_eventID", item.getEventID());
        db.insert("Event_Detail", null, entryRow);
    }
}
