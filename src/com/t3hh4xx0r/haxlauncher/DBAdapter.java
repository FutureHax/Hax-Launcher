package com.t3hh4xx0r.haxlauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    private final Context context; 
    
    private static DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    private static final String DATABASE_CREATE =
            "create table hotseats (_id integer primary key autoincrement, "
            + "name text not null, icon blob not null, intent text not null);";

    
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, "hotseats.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS hotseats");
            onCreate(db);
        }
    }    
    
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
    	DBHelper.close();
    }

    public long insertHotseat(String intent, byte[] icon, String title) {
    	ContentValues values = new ContentValues();
    	values.put("name", title);
    	values.put("icon", icon);
    	values.put("intent", intent);
        return db.insert("hotseats", null, values);      		
    } 
    
    public Cursor getAllHotseats() {
    	Cursor mCursor = db.query("hotseats", new String[] {
        		"_id",
        		"icon",
        		"name",
        		"intent"}, 
                null, 
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }
    
    public boolean removeHotseat(String i) {
        return db.delete("hotseats", "intent" + 
        		"= ?", new String[] {i}) > 0;        		
    }
    
}
