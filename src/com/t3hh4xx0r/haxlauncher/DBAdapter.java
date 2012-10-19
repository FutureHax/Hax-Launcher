package com.t3hh4xx0r.haxlauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {

    private final Context context; 
    
    private static DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    private static final String CREATE_HOTSEATS =
            "create table hotseats (_id integer primary key autoincrement, "
            + "name text not null, icon blob not null, intent text not null);";

    private static final String CREATE_PANELS =
            "create table panels (_id integer primary key autoincrement, "
    		+ "version text not null, "
            + "author text not null, name text not null, package text not null, "
            + "desc text not null, icon blob not null, screencap blob not null, status text not null, "
            + "unique(name, package, version) on conflict ignore);";

    
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, "launcher_shitz.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(CREATE_HOTSEATS);
        	db.execSQL(CREATE_PANELS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS hotseats");
            db.execSQL("DROP TABLE IF EXISTS panels");
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
    
    public long insertPanel(String version, String author, String name, String packageN, String desc, byte[] icon, byte[] screencap) {
    	ContentValues values = new ContentValues();
    	values.put("icon", icon);
    	values.put("version", version);
    	values.put("screencap", screencap);
    	values.put("name", name);
    	values.put("desc", desc);
    	values.put("author", author);
    	values.put("package", packageN);
    	values.put("status", 0);
        return db.insert("panels", null, values);      		
    }
    
    public Cursor getAllPanels() {
    	Cursor mCursor = db.query("panels", new String[] {
        		"_id",
        		"name",
        		"author",
        		"package",
        		"desc",
        		"screencap",
        		"icon",
        		"status", 
        		"version"}, 
                null, 
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }    
    
    public void updateStatus(String value, int id) {
    	ContentValues values = new ContentValues();
    	values.put("status", value);
    	db.update("panels", values, "_id = " + id, null);
    }

    public boolean removePanel(String i) {
        return db.delete("panels", "package" + 
        		"= ?", new String[] {i}) > 0;        		
    }    
}
