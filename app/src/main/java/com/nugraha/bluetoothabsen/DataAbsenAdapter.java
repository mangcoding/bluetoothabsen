package com.nugraha.bluetoothabsen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataAbsenAdapter {
    myDbHelper myhelper;
    public DataAbsenAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String macid, String name, String phone)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.MACID, macid);
        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.PHONE, phone);
        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public void generateDataDummy() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        db.execSQL(myDbHelper.TRUNCATE_TABLE);
        db.execSQL(myDbHelper.RESET_INDEX);
        this.insertData("20:5e:f7:55:08:ce","Nabilla","085793473XXX");
        this.insertData("d0:81:7a:9f:c8:a0","Nugraha Macbook Air","085759402XXX");
        this.insertData("58:d9:d5:b3:6c:e1","Nugraha","085759402XXX");
        this.insertData("a8:7d:12:d8:3b:5e","Imas Yukadarwati","082116961XXX");
    }

    public String getData()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID,myDbHelper.MACID,myDbHelper.NAME,myDbHelper.PHONE};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String macid =cursor.getString(cursor.getColumnIndex(myDbHelper.MACID));
            String name =cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String phone =cursor.getString(cursor.getColumnIndex(myDbHelper.PHONE));
            buffer.append(cid+ "   " + macid + "   " + name + "   " + phone + "\n");
        }
        return buffer.toString();
    }

    public  int delete(String uname)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={uname};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.NAME+" = ?",whereArgs);
        return  count;
    }

    public int updateName(String oldName , String newName)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        String[] whereArgs= {oldName};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.NAME+" = ?",whereArgs );
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "DbAbsen";    // Database Name
        private static final String TABLE_NAME = "devices";   // Table Name
        private static final int DATABASE_Version = 2;    // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String MACID = "Macid";    //Column II
        private static final String NAME= "Name";    // Column III
        private static final String PHONE= "Phone";    // Column III
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MACID+" VARCHAR(255) ,"+NAME+" VARCHAR(255) ,"+ PHONE+" VARCHAR(225));";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private static final String TRUNCATE_TABLE="DELETE FROM "+TABLE_NAME;
        private static final String RESET_INDEX= "DELETE from sqlite_sequence where name='"+TABLE_NAME+"'";
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}
