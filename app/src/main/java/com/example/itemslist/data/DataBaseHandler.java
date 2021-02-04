package com.example.itemslist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.itemslist.Util.Util;
import com.example.itemslist.model.Item;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHandler";

    public DataBaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHandler(@Nullable Context context){
    super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Util.TABLE_NAME+ " ( " + Util.KEY_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +  Util.KEY_NAME +  " TEXT, " + Util.KEY_QUANTITY + " INTEGER, " + Util.KEY_WEIGHT +
                " TEXT, " + Util.KEY_SIZE + " INTEGER, " + Util.KEY_IMAGE + " BLOG, " + Util.KEY_PRICE + " REAL)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);
        onCreate( db);
    }

    public void addItem( Item item){
        Log.d(TAG, "addItem: " + item);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( Util.KEY_NAME, item.getName());
        values.put( Util.KEY_QUANTITY, item.getQuantity());
        values.put( Util.KEY_WEIGHT, item.getWeight());
        values.put( Util.KEY_SIZE, item.getSize());
        values.put( Util.KEY_IMAGE, item.getImage());
        values.put( Util.KEY_PRICE, item.getPrice());

        db.insert( Util.TABLE_NAME, null, values);
        db.close();
        Log.d(TAG, "addItem: " + "added");

    }

    public ArrayList< Item> getAllItems(){
        ArrayList< Item> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Util.TABLE_NAME, null);

        if( cursor != null)
            if( cursor.moveToFirst()){
                do{
                    items.add( new Item( cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getBlob(5),
                            cursor.getFloat(6),
                            cursor.getInt(0)));
                }while ( cursor.moveToNext());
            }
        return items;
    }

    public Item readItem( int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query( Util.TABLE_NAME,
                new String[]{ Util.KEY_ID, Util.TABLE_NAME, Util.KEY_QUANTITY, Util.KEY_WEIGHT,
                        Util.KEY_SIZE, Util.KEY_PRICE, Util.KEY_IMAGE}, Util.KEY_ID + "=?", new String[]{ String.valueOf( id)}, null,
        null, null );

        if( cursor != null)
            cursor.moveToFirst();

        return new Item( cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getBlob(5),
                cursor.getFloat(6),
                cursor.getInt(0));
    }

    public void updateItem( Item item){
        SQLiteDatabase db = this.getWritableDatabase();


        Log.d(TAG, "updateItem: " + item);
        ContentValues values = new ContentValues();

        values.put( Util.KEY_NAME, item.getName());
        values.put( Util.KEY_QUANTITY, item.getQuantity());
        values.put( Util.KEY_WEIGHT, item.getWeight());
        values.put( Util.KEY_SIZE, item.getSize());
        values.put( Util.KEY_IMAGE, item.getImage());
        values.put( Util.KEY_PRICE, item.getPrice());

        int i = db.update(Util.TABLE_NAME, values, Util.KEY_ID + "=?",
                new String[]{ String.valueOf(item.getId())});

        Log.d(TAG, "updateItem: all" + i);


        ArrayList<Item> items = getAllItems();
        for( Item t: items)
            Log.d(TAG, "updateItem: all" + t);
    }

    public void deleteItem( Item item){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete( Util.TABLE_NAME, Util.KEY_ID + "=?",
                new String[]{ String.valueOf( item.getId())});
        db.close();
    }

    public int getCount(){
        String COUNT_QUERY = "SELECT * FROM " + Util.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( COUNT_QUERY, null);

        return cursor.getCount();
    }

}
