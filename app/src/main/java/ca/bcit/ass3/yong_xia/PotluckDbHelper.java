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
                db.execSQL( CreateTableContributionSQL() );
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
    private String CreateTableEventMasterSQL() {
        String sql = "";
        sql += "CREATE TABLE Event_Master (";
        sql += "_eventId INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "Name TEXT, ";
        sql += "Date TEXT, ";
        sql += "Time TEXT ); ";
        return sql;
    }

    private String CreateTableEventDetailSQL() {
        String sql = "";
        sql += "CREATE TABLE Event_Details (";
        sql += "_detailId INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "ItemName TEXT, ";
        sql += "ItemUnit TEXT, ";
        sql += "ItemQuantity INTEGER, ";
        sql += "_eventId INTEGER, ";
        sql += "CONSTRAINT fKey_eventId FOREIGN KEY (eventId) ";
        sql += "REFERENCES Event_Master(eventId));";
        return sql;
    }

    private String CreateTableContributionSQL() {
        String sql = "";
        sql += "CREATE TABLE Contribution (";
        sql += "_contributionId INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "Name TEXT, ";
        sql += "Quantity INTEGER, ";
        sql += "Date TEXT, ";
        sql += "_detailId TEXT, ";
        sql += "CONSTRAINT fKey_detailId FOREIGN KEY (_detailId) ";
        sql += "REFERENCES Event_Detail (_detailId));";
        return sql;
    }

    public void InsertEventMasterSQLTableEntry(SQLiteDatabase db, Event event) {
        ContentValues entryRow = new ContentValues();
        entryRow.put("Name", event.getName());
        entryRow.put("Date", event.getDate());
        entryRow.put("Time", event.getTime());
        db.insert("Event_Master", null, entryRow);
    }

    public void InsertEventDetailsSQLTableEntry(SQLiteDatabase db, Item item) {
        ContentValues entryRow = new ContentValues();
        entryRow.put("Item", item.getName());
        entryRow.put("Quantity", item.getQuantity());
        entryRow.put("Unit", item.getUnit());
        db.insert("Event_Details", null, entryRow);
    }
}
