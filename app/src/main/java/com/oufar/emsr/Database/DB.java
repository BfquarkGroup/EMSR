package com.oufar.emsr.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB_NAME";
    private static final String DB_TABLE = "DB_TABLE";

    // Columns
    private static final String ID = "ID";
    private static final String STORE_NAME = "STORE_NAME";

    // Command
    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+" ("+
            ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+  // 0
            STORE_NAME+ " TEXT "+")";  // 5

    public DB(Context context) {
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);

        onCreate(db);
    }

    // A method to insert data
    public boolean insertData(String storeName){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STORE_NAME, storeName);

        long result = db.insert(DB_TABLE, null, contentValues);

        if (result == -1){

            return false;
        }else {

            return true;
        }

        // return result != -1; // if result = -1 data doesn't inserted
    }

    // A method to delete data
    public void deleteData(String id, String storeName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,ID + " = ?",new String[]{id});
        db.delete(DB_TABLE,STORE_NAME + " = ?",new String[]{storeName});
        db.close();
    }

    // A method to view data
    public Cursor viewData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "Select * from "+ DB_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        return cursor;
    }
}
