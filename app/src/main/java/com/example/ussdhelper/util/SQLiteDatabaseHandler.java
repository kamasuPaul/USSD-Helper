package com.example.ussdhelper.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.ussdhelper.modals.UssdAction;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UssdActionsDB";
    private static final String TABLE_NAME = "UssdActions";
    private static final String TABLE_NAME1 = "steps";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_POSITION = "network";
    private static final String KEY_CODE = "code";
    //steps table
    private static final String KEY_ID1 = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ACTION_ID = "action_id";

    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_POSITION,
        KEY_CODE };

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE UssdActions ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
            + "network TEXT, " + "code TEXT )";
        String CREATE_STEPS_TABLE ="CREATE TABLE steps ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "type INTEGER, " +
            "description TEXT, action_id INTEGER )";

        db.execSQL(CREATION_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        this.onCreate(db);
    }

    public void deleteOne(UssdAction ussdAction) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(ussdAction.getId()) });
        db.delete(TABLE_NAME1, "action_id = ?", new String[] { String.valueOf(ussdAction.getId()) });
        db.close();
    }

    public UssdAction getUssdAction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.query(TABLE_NAME, // a. table
            COLUMNS, // b. column names
            " id = ?", // c. selections
            new String[] { String.valueOf(id) }, // d. selections args
            null, // e. group by
            null, // f. having
            null, // g. order by
            null); // h. limit
        String query = "SELECT * FROM "+TABLE_NAME+" LEFT JOIN "+TABLE_NAME1+" ON "+TABLE_NAME+".id = "+TABLE_NAME1+".action_id " +
            "WHERE UssdActions.id = ?";
       Cursor cursor  = db.rawQuery(query,new String[]{String.valueOf(id)});

        if (cursor != null)
            cursor.moveToFirst();

        UssdAction ussdAction = new UssdAction();
        ussdAction.setId(Integer.parseInt(cursor.getString(0)));
        ussdAction.setName(cursor.getString(1));
        ussdAction.setNetwork(cursor.getString(2));
        ussdAction.setCode(cursor.getString(3));
        ussdAction.setSteps(null);

        return ussdAction;
    }

    public List<UssdAction> allUssdActions() {

        List<UssdAction> ussdActions = new LinkedList<UssdAction>();
        String query = "SELECT * FROM "+TABLE_NAME+" LEFT JOIN "+TABLE_NAME1+" ON "+TABLE_NAME+".id = "+TABLE_NAME1+".action_id ";

            String query1 = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        UssdAction ussdAction = null;

        if (cursor.moveToFirst()) {
            do {
                ussdAction = new UssdAction();
                ArrayList<UssdAction.Step>steps = new ArrayList<>();
                ussdAction.setId(Integer.parseInt(cursor.getString(0)));
                ussdAction.setName(cursor.getString(1));
                ussdAction.setNetwork(cursor.getString(2));
                ussdAction.setCode(cursor.getString(3));
                //add step
                UssdAction.Step step = new UssdAction.Step();
                step.setType(cursor.getString(cursor.getColumnIndex("type")));
                steps.add(step);
                UssdAction.Step[]s = new UssdAction.Step[steps.size()];
                s =  steps.toArray(s);
                ussdAction.setSteps(s);
                ussdActions.add(ussdAction);
            } while (cursor.moveToNext());
        }

        return ussdActions;
    }

    public void addUssdAction(UssdAction ussdAction) {
        int id =0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ussdAction.getName());
        values.put(KEY_POSITION, ussdAction.getNetwork());
        values.put(KEY_CODE, ussdAction.getCode());
        // insert
        db.insert(TABLE_NAME,null, values);
        Cursor c = db.rawQuery("SELECT seq FROM sqlite_sequence WHERE name='"+TABLE_NAME+"'",null);
        if(c.moveToFirst()) id = Integer.parseInt(c.getString(0));
        //INSERT INTO THE SECOND TABLE
        for(UssdAction.Step step : ussdAction.getSteps()){
            ContentValues v = new ContentValues();
            v.put(KEY_TYPE,step.getType());
            v.put(KEY_DESCRIPTION,step.getDescription());
            v.put(KEY_ACTION_ID,id);
            db.insert(TABLE_NAME1,null,v);
        }
        db.close();
    }

    public int updateUssdAction(UssdAction ussdAction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, ussdAction.getName());
        values.put(KEY_POSITION, ussdAction.getNetwork());
        values.put(KEY_CODE, ussdAction.getCode());

        int i = db.update(TABLE_NAME, // table
            values, // column/value
            "id = ?", // selections
            new String[] { String.valueOf(ussdAction.getId()) });

        db.close();

        return i;
    }

}
