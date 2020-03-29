package com.nugraha.bluetoothabsen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

public class DataAbsenAdapter {
    myDbHelper myhelper;

    public DataAbsenAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    private long insertData(String macid, String name, String phone)
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
        String count = "SELECT count(_id) FROM "+myDbHelper.TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount <1) {
            db.execSQL(myDbHelper.TRUNCATE_TABLE);
            db.execSQL(myDbHelper.RESET_INDEX);
            this.insertData("20:5e:f7:55:08:cd", "Nabilla", "085793473XXX");
            this.insertData("d0:81:7a:9f:c8:a1", "Nugraha Macbook Air", "085759402XXX");
            this.insertData("1c:b7:2c:49:82:8a", "Nugraha", "085759402XXX");
            this.insertData("a8:7d:12:d8:3b:5e", "Imas Yukadarwati", "082116961XXX");
        }
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

    public String checkAbsen(String MacAddress) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String MY_QUERY = "SELECT A.Macid, B.Name FROM "+myDbHelper.TABLE_PRESENT+" A LEFT JOIN "+myDbHelper.TABLE_NAME+" B ON B."+
                myDbHelper.MACID+"=A."+myDbHelper.MACID+" WHERE A.Macid='"+MacAddress+"' AND A.date='"+date+"'";
        Cursor cursor = db.rawQuery(MY_QUERY,null);
        int total = cursor.getCount();
        StringBuffer buffer= new StringBuffer();
        if (total == 0) {
            String ResultAbsen = InsertAbsen(MacAddress,date);
            buffer.append(ResultAbsen);
        }else {
            cursor.moveToFirst();
            String macid =cursor.getString(cursor.getColumnIndex(myDbHelper.MACID));
            String name =cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            buffer.append(name+" Sudah Absen Hari ini");
        }
        return buffer.toString();
    }

    private String InsertAbsen(String MacAddress,String date) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String MY_QUERY = "SELECT Macid FROM "+myDbHelper.TABLE_NAME+" WHERE Macid='"+MacAddress+"'";
        Cursor cursor = db.rawQuery(MY_QUERY,null);
        int total = cursor.getCount();
        if (total > 0) {
            //Input to Absen Table
            long ResultId = insertDataAbsen(MacAddress,date);
            if (ResultId > 0) {
                return "Record Saved";
            }else{
                return "Failed to save to the databases";
            }
        }else {
            return "Device unknown";
        }
    }

    private long insertDataAbsen(String macid, String date)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.MACID, macid);
        contentValues.put(myDbHelper.TGL, date);
        long id = dbb.insert(myDbHelper.TABLE_PRESENT, null , contentValues);
        return id;
    }

    public String getAbsen()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String MY_QUERY = "SELECT A.date, A.Macid, B.Name FROM "+myDbHelper.TABLE_PRESENT+" A LEFT JOIN "+myDbHelper.TABLE_NAME+" B ON B."+
                myDbHelper.MACID+"=A."+myDbHelper.MACID+" WHERE A.date='"+date+"'";
        Log.d("query", MY_QUERY);
        Cursor cursor = db.rawQuery(MY_QUERY,null);
        int total = cursor.getCount();
        StringBuffer buffer= new StringBuffer();
        if (total == 0) {
            buffer.append("Daftar Kehadiran Hari ini tanggal "+date+" masih kosong");
            return buffer.toString();
        }
        while (cursor.moveToNext())
        {
            String tgl =cursor.getString(cursor.getColumnIndex(myDbHelper.TGL));
            String macid =cursor.getString(cursor.getColumnIndex(myDbHelper.MACID));
            String name =cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            buffer.append(tgl+ "   " + macid + "   " + name + "\n");
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
        private static final String TABLE_PRESENT = "presents";   // Table Absen
        private static final int DATABASE_Version = 7;    // Database Version
        private static final String UID = "_id";
        private static final String MACID = "Macid";
        private static final String TGL = "date";
        private static final String NAME= "Name";
        private static final String PHONE= "Phone";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MACID+" VARCHAR(255) ,"+NAME+" VARCHAR(255) ,"+ PHONE+" VARCHAR(225));";
        private static final String CREATE_TABLE_PRESENT = "CREATE TABLE "+TABLE_PRESENT+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TGL+" TEXT ,"+MACID+" VARCHAR(255) , status VARCHAR(255), created VARCHAR(255) );"; //Status A=Absent, I=Permit, P=Present, S=Sick
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private static final String DROP_TABLE_PRESENT ="DROP TABLE IF EXISTS "+TABLE_PRESENT;
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
                db.execSQL(CREATE_TABLE_PRESENT);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                db.execSQL(DROP_TABLE_PRESENT);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}
